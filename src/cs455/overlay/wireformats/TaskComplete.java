package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * Task Complete message
 */
public class TaskComplete implements Event {
    private EventType Type;
    public byte StatusCode;
    public String AdditionalInfo;

    public byte[] getBytes(){
        byte[] marshelledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        try {
            //Type.ordianl() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.Type.ordinal());

            //Write StatusCode to bytes
            dataOutputStream.writeByte(StatusCode);

            //Write additional info to bytes
            byte[] IPBytes = AdditionalInfo.getBytes("UTF-8");
            dataOutputStream.writeInt(IPBytes.length);
            dataOutputStream.write(IPBytes);

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

    public TaskComplete(byte StatusCode, String AdditionalInfo){
        this.StatusCode = StatusCode;
        this.AdditionalInfo = AdditionalInfo;
        this.Type = EventType.TaskComplete;
    }
}
