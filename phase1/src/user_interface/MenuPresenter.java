package user_interface;

import java.util.ArrayList;

/**
 * A Presenter class that generates Menu items depending on userType of user logged in
 * i.e. Attendee, Organizer, or Speaker
 */
public class MenuPresenter {

    /**
     * Based on userType, adds list of available options to arraylist
     *
     * @param currentUserType String user Type for current user logged in
     * @return Array list of Strings with menu options for specific user
     */
    public ArrayList<String> generateMenuItems(String currentUserType) {
        ArrayList<String> menuItemsAttendee = new ArrayList<>();
        ArrayList<String> menuItemsOrganizer = new ArrayList<>();
        ArrayList<String> menuItemsSpeaker = new ArrayList<>();

        if (currentUserType.equals("Attendee")) {
            menuItemsAttendee.add("1. Events Service");
            menuItemsAttendee.add("2. Messaging Service");
            menuItemsAttendee.add("3. Log Out");
            return menuItemsAttendee;
        }
        else if (currentUserType.equals("Organizer")) {
            menuItemsOrganizer.add("1. Event Manager");
            menuItemsOrganizer.add("2. Create Speaker Account");
            menuItemsOrganizer.add("3. Messaging Service");
            menuItemsOrganizer.add("4. Log Out");
            return menuItemsOrganizer;
        }
        else {
            menuItemsSpeaker.add("1. View Scheduled Talks");
            menuItemsSpeaker.add("2. Messaging Service");
            menuItemsSpeaker.add("3. Log Out");
            return menuItemsSpeaker;
        }
    }

    /**
     * Returns a message that says invalid selection from the user
     * @return  String  a message that tells the user they have selected an invalid selection
     */
    public String invalidSelection(){
        return("Invalid Selection, Please Try Again.");
    }
}
