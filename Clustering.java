import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;

public class Clustering {

    private int numClusters; // number of clusters created
    private int[] idArray; // stores which cluster each index belongs to

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        if (locations == null) {
            throw new IllegalArgumentException("null argument");
        }

        this.numClusters = k;
        int locationsLength = locations.length;

        // check that arguments are valid
        if (k > locationsLength || k < 1) {
            throw new IllegalArgumentException("invalid argument");
        }

        // check that no locations are null
        for (int i = 0; i < locations.length; i++) {
            if (locations[i] == null) throw new
                    IllegalArgumentException("null location found");
        }

        // Create a complete edge undirected weighted graph with m
        //  vertices (one per location on the map)

        EdgeWeightedGraph edgeWeightedGraph = new
                EdgeWeightedGraph(locationsLength);

        // add the edges to the edge undirected weighted digraph, using the
        // distance between each pair of vertices

        // iterate through every pair of vertices
        for (int i = 0; i < locationsLength; i++) {
            for (int j = i; j < locationsLength; j++) {
                // don't compute distance between vertex and itself
                if (i != j) {
                    // Edge(int v, int w, double weight)
                    Edge edge = new Edge(i, j, locations[i].distanceTo(locations[j]));
                    edgeWeightedGraph.addEdge(edge);
                }
            }
        }

        // compute the minimum spanning tree of the graph
        KruskalMST minimumSpanningTree = new KruskalMST(edgeWeightedGraph);

        // create the new cluster graph, using only the m−k
        //  edges with lowest weight of the spanning tree
        Edge[] edges = new Edge[locationsLength - 1];

        int currentEdge = 0;

        // go through every edge in the minimum spanning tree
        for (Edge edge : minimumSpanningTree.edges()) {
            edges[currentEdge] = edge;
            // iterate through every edge
            currentEdge++;
        }

        // sort the edges in order of increasing distance
        Arrays.sort(edges);

        EdgeWeightedGraph clusterGraph = new
                EdgeWeightedGraph(locationsLength);

        // add edges to the new edge weighted graph
        // until m - k edges

        for (int i = 0; i < locationsLength - k; i++) {
            clusterGraph.addEdge(edges[i]);
        }

        // create clusters
        CC clusters = new CC(clusterGraph);

        // populate the idArray -- gives
        // the cluster assignment for each location

        idArray = new int[locationsLength];

        for (int i = 0; i < locationsLength; i++) {
            idArray[i] = clusters.id(i);
        }


    }

    // return the cluster of the ith location
    public int clusterOf(int i) {
        if (i < 0 || i >= idArray.length) throw new
                IllegalArgumentException("Invalid input length");
        return idArray[i];
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {

        if (input == null) {
            throw new IllegalArgumentException("null argument");
        }

        if (input.length != idArray.length) throw new
                IllegalArgumentException("Invalid input length");
        // reduce the dimension of transaction summaries by summing all of
        // the entries corresponding to the same cluster

        int[] reducedDimensionArray = new int[numClusters];

        // iterate through, adding the input and reducing the dimension
        // to populate the new reduced dimension array

        for (int i = 0; i < idArray.length; i++) {
            reducedDimensionArray[idArray[i]] += input[i];
        }
        return reducedDimensionArray;
    }

    // unit testing (required)
    public static void main(String[] args) {

        int c = Integer.parseInt(args[0]); // number of point clouds
        int p = Integer.parseInt(args[1]); // number of points in each cloud
        Point2D[] centers = new Point2D[c]; // array to represent the centers

        double randomXFirstIndex = StdRandom.uniformDouble(0.0, 1000.0);
        double randomYFirstIndex = StdRandom.uniformDouble(0.0, 1000.0);
        Point2D firstIndex = new Point2D(randomXFirstIndex, randomYFirstIndex);
        centers[0] = firstIndex;
        int counter = 1;

        // while loop to populate centers[]

        while (counter < c) {
            double randomDouble1 = StdRandom.uniformDouble(0.0, 1000.0);
            double randomDouble2 = StdRandom.uniformDouble(0.0, 1000.0);
            Point2D centerPointCandidate = new Point2D(randomDouble1, randomDouble2);
            boolean isValid = true;

            // check the validity of this point by checking  distance against
            // every previously added point in the centers array
            for (int i = 0; i < counter; i++) {
                if (centerPointCandidate.distanceTo(centers[i]) < 4.0) {
                    isValid = false;
                }
            }

            // if the point is valid, add it to the centers array
            if (isValid) {
                centers[counter] = centerPointCandidate;
                counter++;
            }
        }

        // create and populate locations array
        Point2D[] locations = new Point2D[c * p];

        // populate this array with the first p indices containing random
        // points of distance at most 1 to centers[0], next p indices
        // with random points of distance at post to centers[1], etc.
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < p; j++) {

                Point2D newPoint;

                while (true) {
                    double x = StdRandom.uniformDouble(centers[i].x() - 1,
                                                       centers[i].x() + 1);
                    double y = StdRandom.uniformDouble(centers[i].y() - 1,
                                                       centers[i].y() + 1);
                    newPoint = new Point2D(x, y);

                    if (newPoint.distanceTo(centers[i]) <= 1.0) {
                        break; // the point is valid
                    }
                }
                // populate the locations array with valid points
                locations[p * i + j] = newPoint;


            }
        } // end loops

        // create a new clustering based on the locations
        Clustering clustering = new Clustering(locations, c);

        // check that the first p points are in the same cluster, the next p
        // points are in the same cluster, etc.
        // output "Error!" if this is not the case

        for (int i = 0; i < c; i++) {

            // first index of the new cloud

            int currentClusterId = clustering.clusterOf(p * i);

            // iterate through p points, making sure that every
            // point has the same cluster ID

            for (int j = 0; j < p; j++) {
                if (clustering.clusterOf(p * i + j) != currentClusterId)
                    System.out.println("Error!");
            }
        }

    }
}
