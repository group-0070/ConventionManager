package user_interface;

import event_system.*;
import login_system.*;
import messaging_system.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * A Controller Class that interacts with other UIControllers to run the Conference System program
 */
public class ConferenceSystem {
    private final Scanner scanner = new Scanner(System.in);

    private final LoginPresenter loginPresenter = new LoginPresenter();
    private final MessagePresenter messagePresenter = new MessagePresenter();
    private final EventPresenter eventPresenter = new EventPresenter();
    private final MenuPresenter menuPresenter = new MenuPresenter();

    private final EventSchedulingSystem eventSchedulingSystem = new EventSchedulingSystem();
    private final EventSignUpSystem eventSignUpSystem = new EventSignUpSystem();

    /**
     * Interacts with different parts of the program based on user input to manage the UI
     */
    public void run() {
        // LoginSystem
        boolean loginStatus;
        do {
            // ask for username and password
            System.out.println(loginPresenter.loginPromptUsername());
            String username = scanner.nextLine();
            System.out.println(loginPresenter.loginPromptPassword());
            String password = scanner.nextLine();

            // Checks if login is correct
            ILoginController loginController = new LoginController("assets/accounts.txt", username, password);
            String userType = loginController.getCurrentUserType();
            if (loginController.loginUser(username, password)) {

                // Log into appropriate menu, call helper functions inside the class
                System.out.println(loginPresenter.loginSuccess(username));
                if (userType.equals("Attendee")) loginStatus = this.attendeeRun(loginController);
                else if (userType.equals("Organizer")) loginStatus = this.organizerRun(loginController);
                else loginStatus = this.speakerRun(loginController);


            } else {
                // Login has failed
                System.out.println(loginPresenter.loginFailed());
                loginStatus = true;
            }

            if (loginStatus) {
                loginStatus = this.exitNow();
            }
        } while (loginStatus);
    }

    // a helper function that asks the user if they want to exit now for typing something wrong
    private boolean exitNow() {
        boolean exitStatus;
        do {
            System.out.println(loginPresenter.exitMessage());
            String ans = scanner.nextLine();
            switch (ans) {
                case "y": //Want to terminate the program
                    System.out.println(loginPresenter.exitProgram());
                    exitStatus = false;
                    break;
                case "n": //Does not want to terminate the program, try login with different credentials again
                    return true;
                default:
                    System.out.println(menuPresenter.invalidSelection());
                    exitStatus = true;
                    break;
            }
        } while (exitStatus);
        return false;
    }

    // a helper function to create speaker account
    private void createSpeakerAccount(ILoginController loginController) {
        System.out.println(loginPresenter.addSpeakerUsernamePrompt());
        String speakerUsername = scanner.nextLine();
        System.out.println(loginPresenter.addSpeakerPasswordPrompt());
        String speakerPassword = scanner.nextLine();
        if (loginController.addNewSpeakerUser(speakerUsername, speakerPassword)) {
            System.out.println(loginPresenter.newSpeakerAddedSuccess(speakerUsername));
            loginController.exitAction();
        } else {
            System.out.println(loginPresenter.newSpeakerAddedFailed());
        }
    }

    // the main menu of attendee after the user (Type: Attendee) successfully login to the system
    private boolean attendeeRun(ILoginController loginController) {
        boolean leave;
        IEventController eventController =
                new EventController("assets/events.csv", loginController.getUserService());
        boolean res = eventController.readEvents();
        if (!res) {
            System.out.println(eventPresenter.failToRead());
        }
        do {
            System.out.println(menuPresenter.generateMenuItems("Attendee"));
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // View Events
                    leave = eventSignUpSystem.run(loginController.getCurrentUserId(), eventPresenter,
                            eventController, scanner);
                    eventController.saveEvents();
                    break;
                case "2": // Messaging Service
                    MessageSystem messageSystem = new MessageSystem(messagePresenter,
                            eventController.getEventService(), loginController, scanner);
                    leave = messageSystem.attendeeRun();
                    messageSystem.save();
                    break;
                case "3": // log out (common in all 3)
                    leave = false;
                    System.out.println(loginPresenter.logOffPrompt(loginController.getCurrentUserId()));
                    break;
                default:
                    System.out.println(menuPresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while (leave);
        return false;
    }

    // the main menu of organizer after the user (Type: Organizer) successfully login to the system
    private boolean organizerRun(ILoginController loginController){
        boolean leave = true;
        IEventController eventController =
                new EventController("assets/events.csv", loginController.getUserService());
        boolean res = eventController.readEvents();
        if (!res) {
            System.out.println(eventPresenter.failToRead());
        }
        do {
            System.out.println(menuPresenter.generateMenuItems("Organizer"));
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1.Event Manager
                    leave = eventSchedulingSystem.run(eventPresenter, eventController, scanner);
                    eventController.saveEvents();  //save events
                    break;
                case "2": // 2.Create Speaker Account
                    this.createSpeakerAccount(loginController);
                    break;
                case "3": // 3.Messaging Service
                    MessageSystem messageSystem = new MessageSystem(messagePresenter,
                            eventController.getEventService(), loginController, scanner);
                    leave = messageSystem.organizerRun();
                    messageSystem.save();
                    break;
                case "4": // 4.Log out
                    leave = false;
                    System.out.println(loginPresenter.logOffPrompt(loginController.getCurrentUserId()));
                    break;
                default:
                    System.out.println(menuPresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while (leave);
        return false;
    }

    // the main menu of speaker after the user (Type: Speaker) successfully login to the system
    private boolean speakerRun(ILoginController loginController) {
        boolean leave;
        IEventController eventController =
                new EventController("assets/events.csv", loginController.getUserService());
        boolean res = eventController.readEvents();
        if (!res) {
            System.out.println(eventPresenter.failToRead());
        }
        do {
            System.out.println(menuPresenter.generateMenuItems("Speaker"));
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1. View Scheduled Talks
                    ArrayList<String> eventsForUser = eventController.getEventsForUser(loginController.getCurrentUserId());
                    for (String event : eventsForUser){
                        System.out.println(event);
                    }
                    leave = true;
                    break;
                case "2": // 2. Messaging Service
                    MessageSystem messageSystem = new MessageSystem(messagePresenter,
                            eventController.getEventService(), loginController, scanner);
                    leave = messageSystem.speakerRun();
                    messageSystem.save();
                    break;
                case "3": // 3. Log out
                    leave = false;
                    System.out.println(loginPresenter.logOffPrompt(loginController.getCurrentUserId()));
                    break;
                default:
                    System.out.println(menuPresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while (leave);
        return false;
    }
}