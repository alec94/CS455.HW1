package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Alec on 1/23/2017.
 * returns an event object for a given byte array
 */
public class EventFactory {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Event parseEvent(byte[] Bytes) {
        String sourceKey;
        Event event = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Bytes);
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        try {
            Event.EventType eventType = Event.EventType.values()[dataInputStream.readInt()];

            switch (eventType) {
                case Register:
                case Deregister:
                    int IPAddressLength = dataInputStream.readInt();
                    byte[] IPAddressBytes = new byte[IPAddressLength];
                    dataInputStream.read(IPAddressBytes, 0, IPAddressLength);
                    String IPAddress = new String(IPAddressBytes, "UTF-8");

                    int Port = dataInputStream.readInt();

                    if (eventType == Event.EventType.Register) {
                        event = new Register(IPAddress, Port);
                    } else {
                        event = new Deregister(IPAddress, Port);
                    }
                    break;
                case LinkWeights:
                    int numberOfNodes = dataInputStream.readInt();
                    //System.out.println("Number of nodes: " + numberOfNodes);
                    int[][] linkWeights = new int[numberOfNodes][numberOfNodes];

                    //System.out.println("Link Weights:");
                    for (int i = 0; i < numberOfNodes; i++) {
                        for (int j = 0; j < numberOfNodes; j++) {
                            linkWeights[i][j] = dataInputStream.readInt();
                            //System.out.print(linkWeights[i][j]);
                        }

                        //System.out.println("");
                    }

                    String[] nodeList = new String[numberOfNodes];

                    for (int i = 0; i < numberOfNodes; i++) {
                        byte[] stringBytes = new byte[dataInputStream.readInt()];
                        dataInputStream.read(stringBytes, 0, stringBytes.length);
                        nodeList[i] = new String(stringBytes, "UTF-8");
                    }

                    event = new LinkWeights(linkWeights, nodeList);
                    break;
                case Message:
                    byte[] destinationBytes = new byte[dataInputStream.readInt()];
                    dataInputStream.read(destinationBytes, 0, destinationBytes.length);
                    String destinationKey = new String(destinationBytes, "UTF-8");

                    byte[] sourceBytes = new byte[dataInputStream.readInt()];
                    dataInputStream.read(sourceBytes, 0, sourceBytes.length);
                    sourceKey = new String(sourceBytes, "UTF-8");

                    int payload = dataInputStream.readInt();

                    event = new Message(payload, destinationKey, sourceKey);
                    //System.out.println("New message received, dest: " + ((Message) event).getDestinationKey());

                    break;
                case MessagingNodesList:

                    int numberOfKeys = dataInputStream.readInt();

                    String[] keys = new String[numberOfKeys];

                    for (int i = 0; i < numberOfKeys; i++) {
                        int keyLength = dataInputStream.readInt();
                        byte[] keyBytes = new byte[keyLength];
                        dataInputStream.read(keyBytes, 0, keyLength);

                        keys[i] = new String(keyBytes, "UTF-8");
                    }

                    event = new MessagingNodesList(keys);
                    break;
//                case Register:
//                    int IPAddressLength = dataInputStream.readInt();
//                    byte[] IPAddressBytes = new byte[IPAddressLength];
//                    dataInputStream.read(IPAddressBytes,0,IPAddressLength);
//                    String IPAddress = new String(IPAddressBytes,"UTF-8");
//
//                    int Port = dataInputStream.readInt();
//
//                    event = new Register(IPAddress,Port);
//                    break;
                case TaskComplete:
                    byte statusCode = dataInputStream.readByte();

                    byte[] additionalInfoBytes = new byte[dataInputStream.readInt()];
                    dataInputStream.read(additionalInfoBytes, 0, additionalInfoBytes.length);

                    String additionalInfo = new String(additionalInfoBytes, "UTF-8");
                    event = new TaskComplete(statusCode, additionalInfo);
                    break;
                case TaskInitiate:
                    event = new TaskInitiate(dataInputStream.readInt());
                    break;
                case TaskSummaryRequest:
                    event = new TaskSummaryRequest();
                    break;
                case TaskSummaryResponse:
                    byte[] sourceKeyBytes = new byte[dataInputStream.readInt()];
                    dataInputStream.read(sourceKeyBytes, 0, sourceKeyBytes.length);
                    sourceKey = new String(sourceKeyBytes, "UTF-8");

                    int sendTracker = dataInputStream.readInt();
                    int receiveTracker = dataInputStream.readInt();
                    int relayTracker = dataInputStream.readInt();
                    long sendSummation = dataInputStream.readLong();
                    long receiveSummation = dataInputStream.readLong();

                    event = new TaskSummaryResponse(sourceKey, sendTracker, receiveTracker, relayTracker, sendSummation, receiveSummation);
                    break;
                default:
                    System.out.println("Unknown event type: " + eventType);
                    break;
            }

        } catch (IOException e) {
            System.out.println("Error converting byte[] into Event. " + e.getMessage());
        }

        return event;
    }
}
