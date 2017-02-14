package cs455.overlay.dijkstra;

/**
 * Created by Alec on 1/23/2017.
 * Calculates the shortest path from a source node to all over nodes.
 */
public class ShortestPath {
    private int nodeCount;

    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in shortest path tree
    private int minDistance(int distances[], Boolean tree[]) {
        // Initialize min value
        int min = Integer.MAX_VALUE;
        int min_index = -1;

        for (int i = 0; i < nodeCount; i++)
            if (!tree[i] && distances[i] <= min) {
                min = distances[i];
                min_index = i;
            }

        return min_index;
    }

    // A utility function to print the constructed distance array
    void printSolution(int dist[]) {
        if (nodeCount != 0) {
            System.out.println("Vertex Distance from Source");
            for (int i = 0; i < nodeCount; i++)
                System.out.println(i + " \t\t " + dist[i]);
        }else{
            System.out.println("");
        }
    }

   public RoutingCache dijkstra(int overlay[][], int sourceNode, String[] nodeList) {
       // outputDistances[i] will hold the shortest path from sourceNode to i
        int outputDistances[] = new int[nodeCount];

        // shortestPathTree[i] is true if vertex i is included in shortest
        // path tree or shortest distance from the sourceNode to i is finalized
        Boolean shortestPathTree[] = new Boolean[nodeCount];

        // Initialize all distances as INFINITE and shortestPathTree[] as false
        for (int i = 0; i < nodeCount; i++) {
            outputDistances[i] = Integer.MAX_VALUE;
            shortestPathTree[i] = false;
        }
        outputDistances[sourceNode] = 0;

        RoutingCache routingCache = new RoutingCache(nodeList[sourceNode]);

        // Find shortest path for all vertices
        for (int count = 0; count < nodeCount - 1; count++) {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(outputDistances, shortestPathTree);

            // Mark the picked vertex as processed
            shortestPathTree[u] = true;

            // Update dist value of the adjacent vertices of the
            // picked vertex.

            for (int v = 0; v < nodeCount; v++) {

                // Update dist[v] only if is not in shortestPathTree, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!shortestPathTree[v] && overlay[u][v] != 0 && outputDistances[u] != Integer.MAX_VALUE && outputDistances[u] + overlay[u][v] < outputDistances[v]) {
                    outputDistances[v] = outputDistances[u] + overlay[u][v];
                    if (u != sourceNode) {
                        routingCache.addNodeToPath(nodeList[v], new EdgeNode(nodeList[u], overlay[u][v]));
                    } else {
                        routingCache.addNodeToPath(nodeList[v], new EdgeNode(nodeList[v], overlay[u][v]));
                    }
                }
            }
        }
        //routingCache.printPaths();

        return routingCache;
    }

    public ShortestPath(int numberOfNodes){
        this.nodeCount = numberOfNodes;
    }
}
