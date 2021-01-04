package messaging_system;

import java.io.*;

/**
 * Gateway class that read and persistent information between the program and the disk.s
 */
public class MessageFileDataProvider implements IMessageDataProvider {
    private final String file_name;
    private final MessageService messageServiceEngine;


    /**
     * Initialize ScheduleDataProvider
     * @param fileName             String       Start of the event.
     */
    public MessageFileDataProvider(String fileName, MessageService messageServiceEngine) {
        this.messageServiceEngine = messageServiceEngine;
        file_name = fileName;
    }

    /**
     * Insert the information of an event by writing to a .txt file
     */
    @Override
    public void write() {
        File file = new File(file_name);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file_name);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String messageString : this.messageServiceEngine.formatStringMessage()) {
                bw.write(messageString + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("message_list text file is missing");
            e.printStackTrace();
        }
    }

    /**
     * Read all the messages in the data source into the MessageServiceEngine class to store
     */
    public void read() {
        File file = new File(file_name);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(file_name));
            String row;

            boolean empty = file_name.length() == 0;
            //execute reading loop if file is not empty
            if (!empty) {
                while ((row = br.readLine()) != null) {

                    // Skip over empty lines
                    if (row.length() > 0) {

                        // split csv and get data
                        String[] messageString = row.split("ç");
                        String senderId = messageString[0];
                        String receiverId = messageString[1];
                        String info = messageString[2];
                        String time = messageString[3];

                        // load the old messages into the MessageService
                        this.messageServiceEngine.addMessage(info, senderId, receiverId, time);
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
