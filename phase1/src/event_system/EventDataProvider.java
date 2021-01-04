package event_system;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class EventDataProvider implements IEventDataProvider {

    private final EventService event_service;
    private final String file_name;

    /**
     * Initialize ScheduleDataProvider
     * @param  eventService EventService Contains the Event ID.
     * @param  fileName     String       Start of the event.
     */
    public EventDataProvider(EventService eventService, String fileName){
        event_service = eventService;
        file_name = fileName;
    }

    /**
     * Insert the information of an event by writing to a .csv file
     * @return true if the file was successfully written to.
     */
    @Override
    public boolean write(){
        // Fix gateway ScheduleDataProvider dependency on entity Event.
        File eventListFile = new File(this.file_name);
        try{

            // clear file if it exists
            if (eventListFile.exists()) {
                eventListFile.delete();
            }
            eventListFile.createNewFile();

            // initialize writers
            FileWriter fw = new FileWriter(eventListFile);
            BufferedWriter bw = new BufferedWriter(fw);

            // write to file
            bw.write("name,startTime,endTime,roomID,speakerIDs,attendeeIDs\n");
            for (String eventString: event_service.getStringEvents()) {
                bw.write(eventString + "\n");
                //JOptionPane.showMessageDialog(null, "Event saved");
            }
            bw.flush();
            bw.close();
            return true;
        }
        catch (IOException e) {
            System.out.println("events.csv is missing");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Read the list of events from the .csv file
     * @return true if the file was successfully read.
     */
    @Override
    public boolean read(){
        try{
            BufferedReader br = new BufferedReader(new FileReader(file_name));

            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            String row;
            br.readLine(); // skip column names
            boolean empty = file_name.length() == 0;
            //execute reading loop if file is not empty
            if (!empty){
                while((row = br.readLine()) != null){
                    try {
                        // Skip over empty lines
                        if(row.length() > 0) {

                            // split csv and get data
                            String[] temp = row.replace("\n", "").split(",");
                            String eventID = temp[0];
                            LocalDateTime start = LocalDateTime.parse(temp[1], format);
                            LocalDateTime end = LocalDateTime.parse(temp[2], format);
                            String roomID = temp[3];

                            // get attendee and speaker arraylists
                            ArrayList<String> speakerIDs = new ArrayList<>(Arrays.asList(temp[4].split("\\|")));
                            ArrayList<String> attendeeIDs = new ArrayList<>(Arrays.asList(temp[5].split("\\|")));
                            if (speakerIDs.get(0).equals("ε")) speakerIDs = new ArrayList<>(); // empty
                            if (attendeeIDs.get(0).equals("ε")) attendeeIDs = new ArrayList<>(); // empty

                            // add as event
                            event_service.addEvent(eventID, start, end, roomID, speakerIDs, attendeeIDs);
                        }
                    }
                    catch (Exception e){
                        System.out.println("Cannot add row to events due to poor formatting: \"" + row + "\"");
                    }
                }
            }
            return true;
        }
        catch (IOException e) {
            return false;
        }

    }
}
