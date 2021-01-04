package event_system;

import java.util.ArrayList;
import java.util.Scanner;

public class EventSignUpSystem {
    /**
     * Gateway class for event_system to interact with the user in the sign up system
     * @param userID           String           Contains the user ID
     * @param eventPresenter   EventPresenter   A variable to access methods in the presenter class
     * @param eventController  IEventController Variable to access controller methods to use the user input
     * @param scanner          Scanner          Read an store user input
     * @return                 Boolean          True, if the user wants to exit the sign up main menu
     */
    public boolean run(String userID, EventPresenter eventPresenter, IEventController eventController, Scanner scanner) {
        boolean leave;
        do {
            System.out.println(eventPresenter.signupMainMenu());
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1. View currently scheduled events for sign up
                    ArrayList<String> events = eventController.getDisplayStringEvents();
                    for (String event : events){
                        System.out.println(eventPresenter.showScheduledEvents(event));
                    }
                    leave = true;
                    break;
                case "2": // 2. Sign up for an event
                    System.out.println(eventPresenter.signupPrompt());
                    String eventID = scanner.nextLine();
                    // try to signup for the event
                    if (eventController.signUp(userID, eventID)) {
                        System.out.println(eventPresenter.signUpSuccess(eventID));
                    } else {
                        System.out.println(eventPresenter.signUpFail(eventID));
                    }
                    leave = true;
                    break;
                case "3": // 3. View events that I have signed up for
                    ArrayList<String> eventsForUser = eventController.getEventsForUser(userID);
                    for (String event : eventsForUser){
                        System.out.println(event);
                        //eventPresenter.showScheduledEvents(event)
                    }
                    leave = true;
                    break;
                case "4": // 4. Cancel a event that I have signed up for
                    System.out.println(eventPresenter.getEventToRemove());
                    String eventToRemove = scanner.nextLine();
                    if (eventController.cancelSignUp(userID, eventToRemove)) {
                        System.out.println(eventPresenter.cancelSuccess(eventToRemove));
                    } else {
                        System.out.println(eventPresenter.cancelFail(eventToRemove));
                    }
                    leave = true;
                    break;
                case "5": // 3.Return to the previous menu
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
}
