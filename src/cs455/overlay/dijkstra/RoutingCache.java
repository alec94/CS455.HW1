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
    private HashMap<String, ArrayList<EdgeNode>> pathCache;
    private String nodeKey;

    public void addPath(String destinationKey, ArrayList<EdgeNode> path){
            pathCache.put(destinationKey, path);
    }

    public ArrayList<EdgeNode> getPath(String destinationKey){
        return pathCache.get(destinationKey);
    }

    public String getNextNodeKey(String destinationKey, HashMap<String,TCPSender> senders){
        String nextNode = pathCache.get(destinationKey).get(0).nodeKey;

        if (senders.keySet().contains(nextNode)) {
            return nextNode;
        }{
            return getNextNodeKey(nextNode,senders);
        }
    }

    void addNodeToPath(String destKey, EdgeNode node){
        if (pathCache.containsKey(destKey)){
            pathCache.get(destKey).add(node);
        } else {
            ArrayList<EdgeNode> path = new ArrayList<>();
            path.add(node);
            pathCache.put(destKey,path);
            System.out.println("Created new path for " + destKey);
        }

        System.out.println("Added " + node.toString() + " to path for " + destKey);
    }

    void printPaths(){
        for (String key : pathCache.keySet()){
            System.out.println("Path from " + nodeKey + " to " + key + ":");
            System.out.print(nodeKey);
            for (Object node : pathCache.get(key).toArray()){
                System.out.print("--" + ((EdgeNode) node).edgeWeight + "--" + ((EdgeNode) node).nodeKey);
            }

            System.out.println("");
        }
    }

    public RoutingCache(String nodeKey){
        pathCache = new HashMap<>();
        this.nodeKey = nodeKey;
    }
}
