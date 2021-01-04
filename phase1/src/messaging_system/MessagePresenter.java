package messaging_system;

import java.util.ArrayList;

/**
 * Presenter class that presents outputs as a result of any use case operations with the Messaging Service.
 */
public class MessagePresenter {
    /**
     * Present a Main Menu for Messaging service with 4 options
     * (Multimessage, DirectMessage, Show Message Log, and Return to previous menu)
     * @return A list of string to be printed on screen to show as menu options.
     */
    public ArrayList<String> messageMainMenu(){
        ArrayList<String> messageServiceItem = new ArrayList<>();
        messageServiceItem.add("1. MultiMessage");
        messageServiceItem.add("2. DirectMessage");
        messageServiceItem.add("3. Show Message Log");
        messageServiceItem.add("4. Return to the previous menu");
        return messageServiceItem;
    }

    /**
     * Present a Main Menu for Messaging service with 3 options for Attendees only.
     * (DirectMessage, Show Message Log, and Return to previous menu)
     * @return A list of string to be printed on screen to show as menu options.
     */
    public ArrayList<String> messageAttendeeMainMenu(){
        ArrayList<String> messageServiceItem = new ArrayList<>();
        messageServiceItem.add("1. DirectMessage");
        messageServiceItem.add("2. Show Message Log");
        messageServiceItem.add("3. Return to the previous menu");
        return messageServiceItem;
    }

    /**
     * Return the message receiver's username entered by the user.
     * @return the receiverId/username in String form
     */
    public String enterReceiverUsername(){
        return("Please enter the username of the Receiver");
    }

    /**
     * Return the message receiver's username and their type entered by the user.
     * @return the receiverId/username and their type in String form
     */
    // for multicast
    public String enterReceiverType(){
        return("Please enter the userType of the Receivers from the following: 'Speaker', 'Attendee'");
    }

    /**
     * Return the eventIDs for which the speaker entered and would like to multicast their attendees
     * @return the eventIDs entered by the user in String form
     */
    public String enterEventIDs(){
        return("Please enter the name of events that you want to send message to all its attendees. Use comma ',' to" +
                " separate the multiple event names");
    }

    /**
     * Present a Main Menu for Messaging service with 3 options for Speaker only.
     * @return A list of string to be printed on screen to show as menu options.
     */
    public ArrayList<String> promptSpeakerMessageAll(){
        ArrayList<String> speakerMessageAll = new ArrayList<>();
        speakerMessageAll.add("1. Message all attendees who are attending my events");
        speakerMessageAll.add("2. Message attendees in some events or a particular event");
        speakerMessageAll.add("3. Return to the previous menu");
        return speakerMessageAll;
    }

    /**
     * Return a copy of message content entered by the user.
     * @return message content in String form entered by user
     */
    public String enterMessageInfo(){
        return("Please enter the Information of the message");
    }

    /**
     * Return a prompt stating failure in sending a message
     * @return a prompt indicating a failed messaging operation in String form.
     */
    public String messageFail(){
        return("Direct message: Unauthorized request or invalid Receiver Username");
    }

    /**
     * Return a prompt stating a success in sending a message
     * @return a prompt indicating a successful messaging operation in String form.
     */
    public String messageSuccess(){
        return("Direct Message: Sent successfully");
    }

    /**
     * Return a prompt stating a failed in multicasting a message
     * @return a prompt indicating a failed multicasting operation in String form.
     */
    public String multicastFail(){
        return("Multicasting: Unauthorized request or invalid input");
    }

    /**
     * Return a prompt stating a success in multicasting a message
     * @return a prompt indicating a successful multicasting operation in String form.
     */
    public String multicastSuccess(){
        return("Multicasting: Sent successfully");
    }

    /**
     * Return a prompt stating a failed in speaker multicasting a message for an event
     * @return a prompt indicating a failed multicasting operation for a speaker in String form.
     */
    public String speakerEventMulticastFail(){
        return("Multicasting: Unauthorized request or invalid input for event name, messages are being sent to " +
                "correct event name");
    }

    /**
     * Return a prompt stating an invalid selection of menu options.
     * @return a prompt indicating an invalid selection of menu options.
     */
    public String invalidSelection(){
        return("Invalid Selection, Please Try Again.");
    }
}
