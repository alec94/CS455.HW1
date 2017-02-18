package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * Message sent during the
 */
public class Message implements Event {
    private final EventType type;
    private final String destinationKey;
    private final String sourceKey;
    private final int payload;


    public Message(int payload, String destinationKey, String sourceKey) {
        this.payload = payload;
        this.destinationKey = destinationKey;
        this.sourceKey = sourceKey;
        this.type = EventType.Message;
    }

    public byte[] getBytes() {
        byte[] marshalledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        try {
            //Type.ordianl() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.type.ordinal());

            //Write destinationKey
            byte[] destinationBytes = this.destinationKey.getBytes("UTF-8");
            dataOutputStream.writeInt(destinationBytes.length);
            dataOutputStream.write(destinationBytes);

            //Write sourceKey
            byte[] sourceBytes = this.sourceKey.getBytes("UTF-8");
            dataOutputStream.writeInt(sourceBytes.length);
            dataOutputStream.write(sourceBytes);

            //Write payload
            dataOutputStream.writeInt(payload);

            dataOutputStream.flush();
            marshalledBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException ioe) {
            System.out.println("Error getting bytes for Message. " + ioe.getMessage());
        }

        return marshalledBytes;
    }

    public int getPayload() {
        return this.payload;
    }

    public String getDestinationKey() {
        return this.destinationKey;
    }

// --Commented out by Inspection START (2/14/2017 8:18 PM):
//    public String getSourceKey() {
//        return this.sourceKey;
//    }
// --Commented out by Inspection STOP (2/14/2017 8:18 PM)

    public EventType getType() {
        return this.type;
    }
}
