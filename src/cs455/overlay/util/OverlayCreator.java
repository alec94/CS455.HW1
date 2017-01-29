package cs455.overlay.util;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.node.MessagingNode;

import java.util.ArrayList;

/**
 * Created by Alec on 1/23/2017.
 */
public class OverlayCreator {
    private RoutingCache RoutingCache;

    private boolean ComputeOverlay(ArrayList<MessagingNode> MessagingNodeList){
        RoutingCache = new RoutingCache();

        return true;
    }

    public RoutingCache getRoutingCache(){
        return this.RoutingCache;
    }

    public OverlayCreator(ArrayList<MessagingNode> MessagingNodeList){
        if (MessagingNodeList.size() > 0){
            ComputeOverlay(MessagingNodeList);
        }
    }

}
