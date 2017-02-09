package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Alec on 1/23/2017.
 */
public class EventFactory {

    public static Event parseEvent(byte[] Bytes){
        Event event = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Bytes);
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        try {
            Event.EventType eventType = Event.EventType.values()[dataInputStream.readInt()];

            switch (eventType){

                case Deregister:
                    //event = new Deregister();
                    break;
                case LinkWeights:
                    event = new LinkWeights();
                    break;
                case Message:
                    event = new Message();
                    break;
                case MessagingNodesList:

                    int numberOfKeys = dataInputStream.readInt();
                    String[] keys = new String[numberOfKeys];

                    for (int i = 0; i < numberOfKeys; i++){
                        int keyLength = dataInputStream.readInt();
                        byte[] keyBytes = new byte[keyLength];
                        dataInputStream.read(keyBytes,0,keyLength);

                        keys[i] = new String(keyBytes,"UTF-8");
                    }

                    event = new MessagingNodesList(keys);
                    break;
                case Register:
                    int IPAddressLength = dataInputStream.readInt();
                    byte[] IPAddressBytes = new byte[IPAddressLength];
                    dataInputStream.read(IPAddressBytes,0,IPAddressLength);
                    String IPAddress = new String(IPAddressBytes,"UTF-8");

                    int Port = dataInputStream.readInt();

                    event = new Register(IPAddress,Port);
                    break;
                case TaskComplete:
                    //event = new TaskComplete();
                    break;
                case TaskInitiate:
                    event = new TaskInitiate();
                    break;
                case TaskSummaryRequest:
                    event = new TaskSummaryRequest();
                    break;
                case TaskSummaryResponse:
                    event = new TaskSummaryResponse();
                    break;
            }

        }catch (IOException ioe){
            System.out.println("Error converting byte[] into Event. " + ioe.getMessage());
        }

        return event;
    }
}
