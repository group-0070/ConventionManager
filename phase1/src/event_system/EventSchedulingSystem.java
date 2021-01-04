package event_system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Controller class for event_system to interact with the user in the scheduling system
 */
public class EventSchedulingSystem {
    /**
     * Method to communicate with user and makes changes in the schedule
     * @param eventPresenter   EventPresenter   A variable to access methods in the presenter class
     * @param eventController  IEventController Variable to access controller methods to use the user input
     * @param scanner          Scanner          Read an store user input
     * @return                 Boolean          True, if the user wants to exit the sign up main menu
     */
    public boolean run(EventPresenter eventPresenter, IEventController eventController, Scanner scanner) {
        // EventSchedulingSystem
        boolean leave = false;
        outerloop:
        do {
            System.out.println(eventPresenter.scheduleMainMenu());
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1. View currently scheduled events
                    ArrayList<String> events = eventController.getDisplayStringEvents();
                    for (String event : events){
                        System.out.println(eventPresenter.showScheduledEvents(event));
                    }
                    leave = true;
                    break;
                case "2": // 2. Add an event to the schedule
                    System.out.println(eventPresenter.promptUser());
                    boolean status4;
                    String eventID;
                    boolean exitDoWhile = false;
                    do{ // keep prompting user until valid eventID entered
                        System.out.println(eventPresenter.enterEventID());
                        eventID = scanner.nextLine();
                        status4 = this.isValidParam(eventID);
                        if (!status4){
                            System.out.println(eventPresenter.invalidInput());
                            exitDoWhile = abort(scanner);
                            if (exitDoWhile){
                                //status4=true;
                                break outerloop;
                            }
                        }
                    } while (!status4);

                    boolean status5;
                    String roomID;
                    do { // keep prompting user until valid roomID entered
                        System.out.println(eventPresenter.enterRoomID());
                        roomID = scanner.nextLine();
                        status5 = this.isValidParam(roomID);
                        if (!status5){
                            System.out.println(eventPresenter.invalidInput());
                            exitDoWhile = abort(scanner);
                            if (exitDoWhile){
                                break outerloop;
                            }
                        }
                    } while (!status5);

                    String speakerID;
                    ArrayList<String> speakers = new ArrayList<>();
                    boolean status0;
                    do{
                        System.out.println(eventPresenter.enterSpeakerID());
                        speakerID = scanner.nextLine();
                        status0 = this.isValidParam(speakerID);
                        if (!status0){
                            System.out.println(eventPresenter.invalidInput());
                            exitDoWhile = abort(scanner);
                            if (exitDoWhile){
                                break outerloop;
                            }
                        }
                    } while (!status0);
                    speakers.add(speakerID);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    // loop while start time is an invalid format
                    String startTimeDate;
                    boolean status;
                    do{
                        System.out.println(eventPresenter.enterStartTime());
                        startTimeDate = scanner.nextLine();
                        status = this.isValidDate(startTimeDate, formatter);
                        if (!status){
                            System.out.println(eventPresenter.invalidTimeInput());
                            exitDoWhile = abort(scanner);
                            if (exitDoWhile){
                                break outerloop;
                            }
                        }
                    } while (!status);
                    // Convert a string into a LocalDateTime variable
                    // https://www.java67.com/2016/04/how-to-convert-string-to-localdatetime-in-java8-example.html
                    LocalDateTime startTime = LocalDateTime.parse(startTimeDate, formatter);

                    // Loop while the end time is an invalid format
                    String endTimeDate;
                    boolean status1;
                    do{
                        System.out.println(eventPresenter.enterEndTime());
                        endTimeDate = scanner.nextLine();
                        status1 = this.isValidDate(endTimeDate, formatter);
                        if (!status1){
                            System.out.println(eventPresenter.invalidTimeInput());
                            exitDoWhile = abort(scanner);
                            if (exitDoWhile){
                                break outerloop;
                            }
                        }
                    } while (!status1);
                    LocalDateTime endTime = LocalDateTime.parse(endTimeDate, formatter);

                    // try to schedule the event
                    if (eventController.addEvent(eventID, startTime, endTime, roomID, speakers)) {
                        System.out.println(eventPresenter.bookingSuccess(eventID));
                    } else {
                        System.out.println(eventPresenter.eventAddFail(eventID, startTime.format(formatter),
                                endTime.format(formatter)));
                    }
                    leave = true;
                    break;
                case "3": // 3.Return to the previous menu
                    leave = false;
                    break;
                default:
                    System.out.println(eventPresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while(leave);
        return true;
    }

    // return true if the input does not contain "," or "|"
    private boolean isValidParam(String input){
        return !input.contains(",") && !input.contains("|") && !input.contains("ε");
    }

    // return true if the format of the date string is in the required format
    private boolean isValidDate(String dateStr, DateTimeFormatter format) {
        try {
            LocalDateTime.parse(dateStr, format);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private boolean abort(Scanner scanner){
        System.out.println("Enter \"1\" to cancel adding this event and return to the previous menu. Otherwise, " +
                "enter \"0\" to add a valid input");
        boolean stat;
        String input;
        do{
            input = scanner.nextLine();
            stat = input.equals("0") || input.equals("1");
            if (!stat){
                System.out.println("Enter \"0\" or \"1\"");
            }
        } while(!stat);
        return input.equals("1");
    }
}