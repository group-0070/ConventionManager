package event_system;

import java.util.ArrayList;

public class EventPresenter {

    //Presenter class to display schedule information in the UI

    /**
     * Method to get a list of options to be displayed in the schedule main menu
     * @return   ArrayList<String>  An list of strings that contain menu options
     */
    public ArrayList<String> scheduleMainMenu() {
        ArrayList<String> scheduleServiceItem = new ArrayList<>();
        scheduleServiceItem.add("1. View currently scheduled events");
        scheduleServiceItem.add("2. Add an event to the schedule");
        scheduleServiceItem.add("3. Return to the previous menu");
        return scheduleServiceItem;
    }

    /**
     * Method to get a list of option to be displayed in the signup main menu
     * @return   ArrayList<String>     A list of strings that contain menu options
     */
    public ArrayList<String> signupMainMenu(){
        ArrayList<String> signupServiceItem = new ArrayList<>();
        signupServiceItem.add("1. View currently scheduled events");
        signupServiceItem.add("2. Sign up for an event");
        signupServiceItem.add("3. View events that I have signed up for");
        signupServiceItem.add("4. Cancel a event that I have signed up for");
        signupServiceItem.add("5. Return to the previous menu");
        return signupServiceItem;
    }

    /**
     * Return a string prompting user to enter the event ID
     * @return  String    "Enter the event ID for sign up"
     */
    public String signupPrompt(){
        return ("Enter the event ID for sign up");
    }

    /**
     * Return a string prompting user enter information that does not contain "," or "|"
     * @return  String  "Please enter the following information for the event you wish to add (do not include "," or "|"
     *                   character in any of the fields:"
     */
    public String promptUser() {
        return ("Please enter the following information for the event you wish to add (do not include \",\" or \"|\" " +
                "character in any of the fields:");
    }

    /**
     * Return a string informing the user that they have made an invalid selection of a menu option
     * @return  String   "Invalid Selection, Please Try Again."
     */
    public String invalidSelection(){
        return ("Invalid Selection, Please Try Again.");
    }

    /**
     * Return a string prompting user to re-enter an input that does not contain "," or "|"
     * @return  String  "Please enter an input that does not contain "," or "|""
     */
    public String invalidInput(){
        return ("Please enter an input that does not contain \",\" or \"|\"");
    }

    /**
     * Return a string prompting user to enter a time in the required format
     * @return  String  "Please enter a valid date in the form "yyyy-MM-dd HH:mm""
     */
    public String invalidTimeInput(){
        return ("Please enter a valid date in the form \"yyyy-MM-dd HH:mm\"");
    }

    /**
     * Return a string prompting user to enter the event ID
     * @return   String  "Enter the event ID"
     */
    public String enterEventID() {
        return ("Enter the event ID");
    }

    /**
     * Return a string prompting user to enter the start time in the required format
     * @return  String  "Enter the start time in the form of "yyyy-MM-dd HH:mm""
     */
    public String enterStartTime() {
        return ("Enter the start time in the form of \"yyyy-MM-dd HH:mm\"");
    }

    /**
     * Return a string prompting user to enter the end time in the required format
     * @return  String  "Enter the end time in the form of "yyyy-MM-dd HH:mm""
     */
    public String enterEndTime() {
        return ("Enter the end time in the form \"yyyy-MM-dd HH:mm\"");
    }

    /**
     * Return a string prompting the user to enter the room ID
     * @return  String  "Enter the room ID"
     */
    public String enterRoomID() {
        return ("Enter the room ID");
    }

    /**
     * Return a string prompting the user to enter the speaker ID
     * @return  String  "Enter the speaker ID"
     */
    public String enterSpeakerID() {
        return ("Enter the speaker ID");
    }

    /**
     * Return a string announcing the program is unable to read from the events.csv file
     * @return  String  "Unable to read file, using an empty event list for now."
     */
    public String failToRead() { return ("Unable to read file, using an empty event list for now."); }

    /**
     * Return a string notifying user they have signed for an event
     * @param eventID  String  Contains the event ID
     * @return         String  "You have signed up for the event: eventID successfully."
     */
    public String signUpSuccess(String eventID) {
        return ("You have signed up for the event: " + eventID + " successfully.");
    }

    /**
     * Returns a string notifying an organizer that they cannot add the event
     * @param eventID  String  Contains the event ID
     * @param start    String  Contains the start time in the form "yyyy-MM-dd HH:mm"
     * @param end      String  Contains the end time in the form "yyyy-MM-dd HH:mm"
     * @return         String  "The event eventID cannot be added because there is already am event from start
     *                          to end \nYou are trying to book the same speaker at simultaneous events \nThe speaker
     *                          you are trying to book for an event does not exist in the system"
     */
    public String eventAddFail(String eventID, String start, String end){
        return ("The event \"" + eventID + "\" cannot be added because there is already am event from " + start +
                " to " + end + "\nYou are trying to book the same speaker at simultaneous events\nThe " +
                "speaker you are trying to book for an event does not exist in the system");
    }

    /**
     * Return a string informing the user their attempt to sign up for an event failed
     * @param eventID  String  Contains the event ID
     * @return         String  "Unauthorized Sign Up: Event eventID is already full or you have signed up before or
     *                          event name does not exist."
     */
    public String signUpFail(String eventID) {
        return ("Unauthorized Sign Up: Event " + eventID + " is already full or you have signed up before or " +
                "event name does not exist.");
    }

    /**
     * Return a string informing organizer the event has been booked successfully
     * @param eventID  String  Contains the event ID
     * @return         String  "The event eventID has been booked successfully"
     */
    public String bookingSuccess(String eventID){
        return ("The event \"" + eventID + "\" has been booked successfully");
    }

    /**
     * Return a string displaying the information pertaining to the event
     * @param event  String  Contains a string containing event information separated by commas
     * @return       String  The event information presented in a readable format
     */
    //UI will loop over event list and use this method to display the event information
    public String showScheduledEvents(String event){
        String[] eventInfo = event.split(",");
        String eventID = eventInfo[0];
        String startTime = eventInfo[1];
        String endTime = eventInfo[2];
        String roomID = eventInfo[3];

        // Format Speakers List
        String[] speakers = eventInfo[4].split("\\|");
        StringBuilder all_speakers = new StringBuilder();
        String prefix = "";
        for (String speaker : speakers) {
            all_speakers.append(prefix);
            prefix = ",";
            all_speakers.append(speaker);
        }
        String string_speakers = all_speakers.toString();

        // Format Attendee List
        String[] attendees = eventInfo[5].split("\\|");
        StringBuilder all_attendees = new StringBuilder();
        for (String attendee : attendees) {
            all_attendees.append(attendee);
            all_attendees.append(",");
        }
        all_attendees.deleteCharAt(all_attendees.length()-1);
        String string_attendees = all_attendees.toString();

        return ("Event ID: " + eventID + "\nStart Time: " + startTime + "\nEnd Time: " + endTime + "\nroomID: " +
                roomID + "\nSpeakers: " + string_speakers + "\nAttendees: " + string_attendees + "\n\n");
    }

    /**
     * Return a string prompting the user to enter the ID of the event they wish to remove
     * @return  String  "Enter the eventID of the event you wish to be removed from attending"
     */
    public String getEventToRemove(){
        return ("Enter the eventID of the event you wish to be removed from attending");
        //UI will call removeUserFromEvent(userID, eventID) and call cancelSuccess if removeUserFromEvent returns true
    }

    /**
     * Return a string informing user they have un-enrolled from an event
     * @param eventID  String  Contains the event ID
     * @return         String  "You have been un-enrolled from eventID"
     */
    public String cancelSuccess(String eventID){
        //return ("You have been un-enrolled from the following event: \n" + this.showScheduledEvents(eventToRemove));
        return ("You have been un-enrolled from " + eventID);
    }

    /**
     * Return a string informing user their attempt to un-enrol from an event has failed
     * @param eventID  String  Contains the event ID
     * @return         String  "Fail to un-enrol from eventID, please make sure you have entered the correct event name"
     */
    public String cancelFail(String eventID){
        return ("Fail to un-enrol from " + eventID + ", please make sure you have entered the correct event name");
    }

}
