package cs455.overlay.node;

import cs455.overlay.transport.ConsoleThread;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Alec on 1/23/2017.
 * Registry Node
 */

public class Registry extends BaseNode{
    private ArrayList<String> messagingNodes;
    private int[][] overlayWeights;

    private void printOverlayWeights(){
        for (int i = 0; i < messagingNodes.size(); i++){
            for (int j = 0; j < messagingNodes.size(); j++){
                if (overlayWeights[i][j] != 0){
                    System.out.println(messagingNodes.get(i) + " " + messagingNodes.get(j) + " " + overlayWeights[i][j]);
                }
            }
        }
    }

    private void createOverlay(int numberOfConnections){
        int numberOfNodes = messagingNodes.size();

        overlayWeights = new int[numberOfNodes][numberOfNodes];

        //intilize all weights to 0, if 0 then no connection exits
        for (int i = 0; i < numberOfConnections; i++){
            for (int j = 0; j < numberOfConnections; j++){
                overlayWeights[i][j] = 0;
            }
        }

       for(int i = 0; i < numberOfNodes; i++){
           String[] keys = new String[2];
           String nodeKey = messagingNodes.get(i);
           int destNode0 = (i+1) % numberOfNodes;
           int destNode1 = (i+2) % numberOfNodes;

           keys[0] = messagingNodes.get(destNode0);
           keys[1] = messagingNodes.get(destNode1);

           //set link weights between 1-10
           overlayWeights[i][destNode0] =  ThreadLocalRandom.current().nextInt(1, 11);
           overlayWeights[i][destNode1] =  ThreadLocalRandom.current().nextInt(1, 11);

           MessagingNodesList list = new MessagingNodesList(keys);

           TCPSender sender = senders.get(nodeKey);

           System.out.println("Connecting " + nodeKey + " to " + Arrays.toString(keys));

           sender.sendData(list.getBytes());
       }

       String[] nodeList = messagingNodes.toArray(new String[messagingNodes.size()]);

       //send link weight information
        LinkWeights weightsMessage = new LinkWeights(overlayWeights,nodeList);

        sendToAll(weightsMessage.getBytes());
    }

    public synchronized void onEvent(Event event, String socketKey){
        String key;

        switch (event.getType()) {
            case Deregister:
                Deregister deregisterEvent = (Deregister) event;

                key = deregisterEvent.IPAddress + ":" + deregisterEvent.Port;

                if (messagingNodes.contains(key)){
                    messagingNodes.remove(key);
                }

                if(senders.containsKey(key)){
                    senders.remove(key);
                }

                if(receivers.containsKey(key)){
                    senders.remove(key);
                }

                break;
            case LinkWeights:
                break;
            case Message:
                break;
            case MessagingNodesList:
                break;
            case Register:
                Register registerEvent = (Register) event;

                key = registerEvent.IPAddress + ":" + registerEvent.Port;

                if (!messagingNodes.contains(key)){
                    updateKey(socketKey, key);

                    messagingNodes.add(key);

                    System.out.println("New messaging node registered, " + key);
                } else {
                    System.out.println("Messaging node is already registered, " + key);
                }

                break;
            case TaskComplete:
                break;
            case TaskInitiate:
                break;
            case TaskSummaryRequest:
                break;
            case TaskSummaryResponse:
                break;
        }
    }

    public void handleConsoleInput(String string){
        String[] inputArray = string.split(" ");
        switch (inputArray[0]){
            case "list-messaging-nodes":
                if (messagingNodes.isEmpty()){
                    System.out.println("Not connected to any MessagingNodes.");
                }else {
                    for (Object messagingNode : messagingNodes.toArray()) {
                        System.out.println(messagingNode);
                    }
                }
                break;
            case "list-weights":
                printOverlayWeights();
                break;
            case "setup-overlay":
                if (inputArray.length < 2){
                    System.out.println("You must specify the number of connections.");
                    System.out.println("USAGE: setup-overlay <number-of-connections.");
                }else {
                    try{
                        int numberOfConnections = Integer.parseInt(inputArray[1]);
                        createOverlay(numberOfConnections);
                    }catch (NumberFormatException nfe){
                        System.out.println("<number-of-connections> must be an integer.");
                    }
                }
                break;
            case "send-overlay-link-weights":
                break;
            case "start":
                break;
            default:
                if (string != ""){
                    System.out.println("Unknown command: " + string);
                }
        }
    }

    public Registry(int PortNumber) throws IOException{

        startServerThread(PortNumber);

        ConsoleThread terminal = new ConsoleThread(this);

        Thread thread = new Thread(terminal);
        thread.start();

        messagingNodes = new ArrayList<String>();

        System.out.println("New registry node started, key: " + this.nodeKey);
    }

    public static void main(String [] args){
        if (args.length < 1){
            System.out.println("You must specify the port number.");
            System.exit(-1);
        }

        int Port = 0;

        try{
            Port = Integer.parseInt(args[0]);
        }catch (NumberFormatException fe){
            System.out.println("Error: Port number must be an integer.");
            System.exit(-1);
        }

        try {
            Registry registry = new Registry(Port);
        }catch (IOException ioe){
            System.out.println("Registry Error: " + ioe.getMessage());
        }
    }

}
