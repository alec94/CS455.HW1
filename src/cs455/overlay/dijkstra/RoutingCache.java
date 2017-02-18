package cs455.overlay.dijkstra;

import cs455.overlay.transport.TCPSender;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alec on 1/23/2017.
 * holds the paths from each destination node
 * one instance per messaging node
 */
public class RoutingCache {
    private final HashMap<String, ArrayList<EdgeNode>> pathCache;
    private final String nodeKey;

    public RoutingCache(String nodeKey) {
        pathCache = new HashMap<>();
        this.nodeKey = nodeKey;
    }

// --Commented out by Inspection START (2/14/2017 8:19 PM):
//    public void addPath(String destinationKey, ArrayList<EdgeNode> path) {
//        pathCache.put(destinationKey, path);
//    }
// --Commented out by Inspection STOP (2/14/2017 8:19 PM)

// --Commented out by Inspection START (2/14/2017 8:19 PM):
//    public ArrayList<EdgeNode> getPath(String destinationKey) {
//        return pathCache.get(destinationKey);
//    }
// --Commented out by Inspection STOP (2/14/2017 8:19 PM)

    public String getNextNodeKey(String destinationKey, HashMap<String, TCPSender> senders) {
        if (pathCache.containsKey(destinationKey)) {
            //System.out.println("Getting next node for " + destinationKey);
            String nextNode = pathCache.get(destinationKey).get(0).nodeKey;

            if (nextNode.equals(destinationKey)) {
                //System.out.println("node: " + nextNode);
                return nextNode;
            } else {
                return getNextNodeKey(nextNode, senders);
            }

        } else {
            System.out.println("No cache exits for " + destinationKey);
            System.out.println("Available caches: " + pathCache.keySet().toString());
            return "";
        }
    }

    void addNodeToPath(String destKey, EdgeNode node) {
        if (pathCache.containsKey(destKey)) {
            pathCache.get(destKey).add(node);
        } else {
            ArrayList<EdgeNode> path = new ArrayList<>();
            path.add(node);
            pathCache.put(destKey, path);
            //System.out.println("Created new path for " + destKey);
        }

        //System.out.println("Added " + node.toString() + " to path for " + destKey);
    }

    public void printPaths() {
        for (String key : pathCache.keySet()) {
            //System.out.println("Path from " + nodeKey + " to " + key + ":");
            System.out.print(nodeKey);
            for (Object node : pathCache.get(key).toArray()) {
                System.out.print("--" + ((EdgeNode) node).edgeWeight + "--" + ((EdgeNode) node).nodeKey);
            }

            System.out.println("");
        }
    }
}
