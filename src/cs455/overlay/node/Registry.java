package cs455.overlay.node;

import cs455.overlay.transport.ConsoleThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Alec on 1/23/2017.
 * Registry Node
 */

public class Registry extends NetworkNode {
    private ArrayList<String> messagingNodes;
    private int[][] overlayWeights;
    private nodeStatus[] status;
    private final HashMap<String, TaskSummaryResponse> taskSummaries = new HashMap<>();
    private boolean weightsSent = false;

    private enum nodeStatus{
        runningTask,
        done,
        waitingForSummary,
        receivedSummary
    }

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

           //System.out.println("Connecting " + nodeKey + " to " + Arrays.toString(keys));

           sender.sendData(list.getBytes());

       }

        System.out.println("Overlay created.");
    }

    private void sendLinkWeights(){
        String[] nodeList = messagingNodes.toArray(new String[messagingNodes.size()]);

        //send link weight information
        LinkWeights weightsMessage = new LinkWeights(overlayWeights ,nodeList);

        sendToAll(weightsMessage.getBytes());

        this.weightsSent = true;
        System.out.println("Link weights sent.");
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
                    senders.get(key).close();
                    senders.remove(key);
                }

                if(receivers.containsKey(key)){
                    receivers.get(key).close();
                    receivers.remove(key);
                }

                System.out.println("Messaging node " + key + " deregistered.");

                break;
            case Register:
                Register registerEvent = (Register) event;

                key = registerEvent.IPAddress + ":" + registerEvent.Port;

                if (!messagingNodes.contains(key)){
                    updateKey(socketKey, key);

                    messagingNodes.add(key);

                    System.out.println("New messaging node registered, " + key);
                } else {
                    System.out.println("Messaging node " + key + " is already registered.");
                }

                break;
            case TaskComplete:
                TaskComplete taskComplete = (TaskComplete) event;

                //update status of messagingNode to done
                for (int i = 0; i < status.length; i++) {
                    if (taskComplete.AdditionalInfo.equals(messagingNodes.get(i))) {
                        status[i] = nodeStatus.done;
                        break;
                    }
                }

                //check if all messagingNodes are done
                boolean done = true;

                for (int i = 0; i < status.length; i++){
                    if(status[i].equals(nodeStatus.runningTask)){
                        done = false;
                    }
                }

                if (done){
                    requestTaskSummary();
                }

                break;
            case TaskSummaryResponse:
                collectNetworkSummary((TaskSummaryResponse) event);
                break;
            default:
                System.out.println("Invalid event type: " + event.getType());
                break;
        }
    }

    private void requestTaskSummary(){
        //set all status to waitingForSummary
        for (int i = 0; i < status.length; i++){
            status[i] = nodeStatus.waitingForSummary;
        }
        try {
            Thread.sleep(15000);
        }catch (InterruptedException e){

        }

        System.out.println("Requesting task summary.");

        //send task summary request

        sendToAll(new TaskSummaryRequest().getBytes());
    }

    private void collectNetworkSummary(TaskSummaryResponse response){
        if (!taskSummaries.containsKey(response.getSourceKey())){
            taskSummaries.put(response.getSourceKey(),response);
            //System.out.print("Received task summary for " + response.getSourceKey());
        }else{
            System.out.println("Network summary for " + response.getSourceKey() + " has already been collected.");
            return;
        }

        //update status to summary received
        for (int i = 0; i < status.length; i++){
            if (response.getSourceKey().equals(messagingNodes.get(i))){
                status[i] = nodeStatus.receivedSummary;
                break;
            }
        }

        //check if all summaries received
        boolean done = true;
        for (int i = 0; i < status.length; i++){
            if (status[i] == nodeStatus.waitingForSummary){
                done = false;
                break;
            }
        }

        //if done, print network summaries
        if (done){
            printTaskSummaries();
        }
    }

    private void printTaskSummaries(){
        int sentTotal = 0;
        int receivedTotal = 0;
        int relayTotal = 0;
        long sentSummationTotal = 0;
        long receivedSummationTotal = 0;

        System.out.format("| %-19s | Number of messages sent | Number of messages received | Summation of sent messages | Summation of received messages | Number of messages relayed |","Node");
        System.out.println("");

        for (TaskSummaryResponse response : taskSummaries.values()){
            System.out.print("| " + response.getSourceKey());
            System.out.format(" | %-,23d", response.getSendTracker());
            System.out.format(" | %-,27d", response.getReceiveTracker());
            System.out.format(" | %-,26d", response.getSendSummation());
            System.out.format(" | %-,30d", response.getReceiveSummation());
            System.out.format(" | %-,26d |", response.getRelayTracker());
            System.out.println("");

            sentTotal += response.getSendTracker();
            receivedTotal += response.getReceiveTracker();
            relayTotal += response.getRelayTracker();
            sentSummationTotal += response.getSendSummation();
            receivedSummationTotal += response.getReceiveSummation();
        }

        System.out.format("| %-19s", "Sum");
        System.out.format(" | %-,23d", sentTotal);
        System.out.format(" | %-,27d", receivedTotal);
        System.out.format(" | %-,26d", sentSummationTotal);
        System.out.format(" | %-,30d", receivedSummationTotal);
        System.out.format(" | %-,26d |", relayTotal);
        System.out.println("");

        taskSummaries.clear();

    }

    public void handleConsoleInput(String string){
        String[] inputArray = string.split(" ");
        switch (inputArray[0]){
            case "runall":
                createOverlay(4);
                sendLinkWeights();
                startRounds(Integer.parseInt(inputArray[1]));
                break;
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
                    System.out.println("USAGE: setup-overlay <number-of-connections>");
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
                sendLinkWeights();
                break;

            case "start":
                if (inputArray.length < 2){
                    System.out.println("You must specify the number of rounds.");
                    System.out.println("USAGE: start <number-of-rounds>");
                }else{
                    if (weightsSent) {
                        try {
                            int numberOfRounds = Integer.parseInt(inputArray[1]);

                            startRounds(numberOfRounds);

                        } catch (NumberFormatException nfe) {
                            System.out.println("<number-of-rounds> must be an integer.");
                        }
                    }else{
                        System.out.println("You must send the link weights before starting the rounds.");
                        System.out.println("COMMAND: send-overlay-link-weights");
                    }
                }
                break;
            default:
                if (string != ""){
                    System.out.println("Unknown command: " + string);
                }
        }
    }

    private void startRounds(int numberOfRounds){
        status = new nodeStatus[messagingNodes.size()];
        for (int i = 0; i < status.length; i++) {
            status[i] = nodeStatus.runningTask;
        }

        sendToAll(new TaskInitiate(numberOfRounds).getBytes());
        System.out.println("Rounds started, count: " + numberOfRounds);
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
