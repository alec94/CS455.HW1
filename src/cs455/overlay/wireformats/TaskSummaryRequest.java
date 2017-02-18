package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * Triggers the messagingNode to send the network stats to the registry
 */
public class TaskSummaryRequest implements Event {
    private final EventType Type;

    public TaskSummaryRequest() {
        this.Type = EventType.TaskSummaryRequest;
    }

    public byte[] getBytes() {
        byte[] marshalledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        try {
            //Type.ordinal() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.Type.ordinal());

            dataOutputStream.flush();
            marshalledBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException ioe) {
            System.out.println("Error getting bytes for " + Type + " event. " + ioe.getMessage());
        }

        return marshalledBytes;
    }

    public EventType getType() {
        return this.Type;
    }
}
