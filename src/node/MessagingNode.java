package node;

import wireformats.Event;

import javax.sound.sampled.Port;

/**
 * Created by Alec on 1/23/2017.
 */
public class MessagingNode implements Node {
    private Registry registry;
    private int Port;
    private String Host;
    public int NodeID;

    public void onEvent(Event event){

    }

    private void Register(){

    }

   public MessagingNode(Registry registry, int NodeID){
        this.registry = registry;
        this.NodeID = NodeID;



   }
}
