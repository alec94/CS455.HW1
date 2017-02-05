package cs455.overlay.wireformats;

import sun.java2d.pipe.hw.AccelDeviceEventNotifier;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * deregistration request
 */
public class Deregister implements Event{
    public int Port;
    public String IPAddress;
    private EventType Type;

    public EventType getType(){
        return this.Type;
    }

    public byte[] getBytes(){
        byte[] marshalledBytes = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        try {
            //Type.ordianl() gets the integer value for the Type enum
            dataOutputStream.writeInt(this.Type.ordinal());

            //Write ip address to bytes
            byte[] IPBytes = IPAddress.getBytes();
            dataOutputStream.writeInt(IPBytes.length);
            dataOutputStream.write(IPBytes);

            //Write port to bytes
            dataOutputStream.writeInt(this.Port);

            dataOutputStream.flush();
            marshalledBytes = byteArrayOutputStream.toByteArray();

        }catch (IOException ioe){
            System.out.println("Error getting bytes for " + Type + " event. " + ioe.getMessage());
        }

        return marshalledBytes;
    }

    public Deregister(String IPAddress, int Port){
        this.Port = Port;
        this.IPAddress = IPAddress;
        this.Type = EventType.Deregister;
    }
}

