package messaging_system;

import event_system.EventService;
import login_system.ILoginController;

import java.util.List;
import java.util.Scanner;

/**
 * A Controller Class that interacts with other Controllers to run the Messaging Service
 */
public class MessageSystem {

    private final MessageController messageController;
    private final MessagePresenter messagePresenter;
    private final Scanner scanner;
    private final String userID;

    /**
     * A Controller to to call presenter classes to display text UI
     * @param messagePresenter  Presenter class of Message System
     * @param eventService      Use case class of Event passed by EventController into MessageController
     * @param loginController   Controller class of the loginController
     * @param scanner           Scanner object to read in user input
     */
    public MessageSystem(MessagePresenter messagePresenter, EventService eventService,
                         ILoginController loginController, Scanner scanner) {
        this.messageController = new MessageController(loginController, eventService,
                "assets/message.txt");
        this.messagePresenter = messagePresenter;
        this.scanner = scanner;
        this.userID = loginController.getCurrentUserId();
    }

    /**
     * Runs the organizer Menu for messaging service
     * @return  boolean     Returns true when the user wants to exit this current menu
     */
    public boolean organizerRun() {
        boolean leave;
        do {
            System.out.println(messagePresenter.messageMainMenu());
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1. MultiMessage
                    System.out.println(messagePresenter.enterMessageInfo());
                    String info = scanner.nextLine();
                    System.out.println(messagePresenter.enterReceiverType());
                    String receiverType = scanner.nextLine();
                    if (messageController.multicastMessage(info, receiverType)) {
                        System.out.println(messagePresenter.multicastSuccess());
                    } else {
                        System.out.println(messagePresenter.multicastFail());
                    }
                    leave = true;
                    break;
                case "2": // 2. DirectMessage
                    this.directMessage();
                    leave = true;
                    break;
                case "3": // 3. Show Message Log
                    this.showMessageLog();
                    leave = true;
                    break;
                case "4": // 4.Return to the previous menu
                    leave = false;
                    break;
                default:
                    System.out.println(messagePresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while(leave);
        return true;
    }

    // helper function for direct message the user logged into the system
    private void directMessage(){
        System.out.println(messagePresenter.enterMessageInfo());
        String info = scanner.nextLine();
        System.out.println(messagePresenter.enterReceiverUsername());
        String receiverId = scanner.nextLine();
        if (messageController.sendMessage(info, receiverId)) {
            System.out.println(messagePresenter.messageSuccess());
        } else {
            System.out.println(messagePresenter.messageFail());
        }
    }

    // helper function to show message log of the user logged into the system
    private void showMessageLog(){
        List<String> messages = messageController.showMessage(userID);
        for (String message : messages){
            System.out.println(message);
        }
    }

    /**
     * Runs the Attendee Menu for messaging service
     * @return  boolean     Returns true when the user wants to exit this current menu
     */
    public boolean attendeeRun() {
        boolean leave;
        do {
            System.out.println(messagePresenter.messageAttendeeMainMenu());
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1. DirectMessage
                    this.directMessage();
                    leave = true;
                    break;
                case "2": // 2. Show Message Log
                    this.showMessageLog();
                    leave = true;
                    break;
                case "3": // 3. Return to the previous menu
                    leave = false;
                    break;
                default:
                    System.out.println(messagePresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while(leave);
        return true;
    }

    /**
     * Runs the Speaker Menu for messaging service
     * @return  boolean     Returns true when the user wants to exit this current menu
     */
    public boolean speakerRun() {
        boolean leave;
        do {
            System.out.println(messagePresenter.messageMainMenu());
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1. MultiMessage
                    this.speakerMulticastMessageForEvents();
                    leave = true;
                    break;
                case "2": // 2. DirectMessage
                    this.directMessage();
                    leave = true;
                    break;
                case "3": // 3. Show Message Log
                    this.showMessageLog();
                    leave = true;
                    break;
                case "4": // 4.Return to the previous menu
                    leave = false;
                    break;
                default:
                    System.out.println(messagePresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while(leave);
        return true;
    }

    // helper function for multicast message attendee in the events given a set of event names
    private void speakerMulticastMessageForEvents() {
        boolean leave;
        do {
            System.out.println(messagePresenter.promptSpeakerMessageAll());
            String userInput = scanner.nextLine();
            switch (userInput) {
                case "1": // 1. Message all attendees who are attending my events
                    System.out.println(messagePresenter.enterMessageInfo());
                    String info = scanner.nextLine();
                    if (messageController.speakerMulticastMessage(info)){
                        System.out.println(messagePresenter.multicastSuccess());
                    } else {
                        System.out.println(messagePresenter.multicastFail());
                    }
                    leave = true;
                    break;
                case "2": // 2. Message attendees in some events or a particular event
                    System.out.println(messagePresenter.enterEventIDs());
                    String eventIDs = scanner.nextLine();
                    System.out.println(messagePresenter.enterMessageInfo());
                    info = scanner.nextLine();
                    if (messageController.speakerMulticastMessageForEvents(info, eventIDs)){
                        System.out.println(messagePresenter.multicastSuccess());
                    } else {
                        System.out.println(messagePresenter.speakerEventMulticastFail());
                    }
                    leave = true;
                    break;
                case "3": // 3. Return to the previous menu
                    leave = false;
                    break;
                default:
                    System.out.println(messagePresenter.invalidSelection());
                    leave = true;
                    break;
            }
        } while(leave);
    }

    /**
     * Saves the messages in the system to the external file by calling a controller interface method
     */
    public void save(){
        this.messageController.save();
    }
}
