package node;

import util.OverlayCreator;
import wireformats.Event;

import java.util.ArrayList;

/**
 * Created by Alec on 1/23/2017.
 */
public class Registry implements Node {
    private ArrayList<MessagingNode> MessagingNodeList;
    public void onEvent(Event event){

    }

    private void createMessagingNodes(int NumberOfNodes){
        for (int i = 0; i < NumberOfNodes; i++){
            MessagingNodeList.add(new MessagingNode(this,i));
        }
    }

    Registry(int NumberOfNodes){
        this.MessagingNodeList = new ArrayList<>(NumberOfNodes);

        createMessagingNodes(NumberOfNodes);

        OverlayCreator OverlayCreater = new OverlayCreator(MessagingNodeList);



    }

}
