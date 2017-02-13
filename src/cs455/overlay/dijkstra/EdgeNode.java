package cs455.overlay.dijkstra;

/**
 * Created by Alec on 2/11/2017.
 * contains the nodeKey and edge weight for a connection
 * used in the RoutingCache
 */

public class EdgeNode {
    public String nodeKey;
    public int edgeWeight;

    public String toString(){
        return this.nodeKey + " " + this.edgeWeight;
    }

    public EdgeNode(String nodeKey, int edgeWeight){
        this.nodeKey = nodeKey;
        this.edgeWeight = edgeWeight;
    }
}
