package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * contains the number of rounds for the messaging node to send
 */
public class TaskInitiate implements Event {
    private EventType Type;
    private int rounds;

    public byte[] getBytes() {
        byte[] marshalledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        try {
            //Type.ordianl() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.Type.ordinal());

            dataOutputStream.writeInt(this.rounds);

            dataOutputStream.flush();
            marshalledBytes = byteArrayOutputStream.toByteArray();

        } catch (IOException ioe) {
            System.out.println("Error getting bytes for " + Type + " event. " + ioe.getMessage());
        }

        return marshalledBytes;
    }

    public EventType getType(){
        return this.Type;
    }

    public int getRounds(){
        return this.rounds;
    }

    public TaskInitiate(int rounds){
        this.rounds = rounds;
        this.Type = EventType.TaskInitiate;
    }
}
