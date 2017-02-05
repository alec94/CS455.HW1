package cs455.overlay.wireformats;

import cs455.overlay.node.MessagingNode;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * List of MessagingNodes for other Messaging nodes to connect to to form the overlay
 * keys are the in the form of <IPAddress>:<Port> where <Port> is the port the node is listening on for new connections
 */
public class MessagingNodesList implements Event {

    private EventType Type;
    private String[] keys;

    public byte[] getBytes(){
        byte[] marshelledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        try {
            //Type.ordianl() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.Type.ordinal());

            //Write ip address to bytes
            dataOutputStream.writeInt(this.keys.length);

            //write list of keys to bytes
            for (String key:this.keys) {
                byte[] keyBytes = key.getBytes("UTF-8");
                dataOutputStream.writeInt(keyBytes.length);
                dataOutputStream.write(keyBytes);
            }

            dataOutputStream.flush();
            marshelledBytes = byteArrayOutputStream.toByteArray();

        }catch (IOException ioe){
            System.out.println("Error getting bytes for " + Type + " event. " + ioe.getMessage());
        }
        return marshelledBytes;
    }

    public EventType getType(){
        return this.Type;
    }

    public String[] getKeys(){
        return keys;
    }

    public int numberOfNodes(){return keys.length;}

    public MessagingNodesList(String[] keys){
        this.Type = EventType.MessagingNodesList;
        this.keys = keys;
    }


}
