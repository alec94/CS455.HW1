package cs455.overlay.dijkstra;

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

    public String getNextNodeKey(String destinationKey){
        return pathCache.get(destinationKey).get(0).nodeKey;
    }

    public void printPaths(){
        for (String key : pathCache.keySet()){
            System.out.print(nodeKey);
            for (Object node : pathCache.get(key).toArray()){
                System.out.print("--" + ((EdgeNode) node).edgeWeight + "--" + ((EdgeNode) node).nodeKey);
            }

            System.out.println("");
        }
    }

    public void RoutingCache(String nodeKey){
        pathCache = new HashMap<>();
        this.nodeKey = nodeKey;
    }
}
