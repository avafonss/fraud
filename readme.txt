Programming Assignment 7: Fraud Detection

/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */

In the clustering constructor, I first create an undirected weighted graph with
a vertex for every location on the map. I then add the edges to this graph
using the euclidian distance between each pair of vertices. Then, I use a
Kruskal MST to compute the minimum spanning tree of the graph, and create a new
cluster graph using only the m-k edges with the lowest weight on my spanning
tree by sorting the edges in order of increasing distance and adding edges to
my cluster graph until m - k times (which, in my loop, is represented by
locationsLength - k). I then compute the clusters from the cluster graph,
and populate my idArray with the id for each cluster. Therefore, this
idArray allows us to track which cluster each vertex is located in and we
can reference that idArray.

/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */

In my WeakLearner constructor, I first set my dimensionPredictionChampion,
signPredictionChampion, and valuePrediction Champion variables equal to zero,
and declare a double MaximumWeight equal to Double.NEGATIVE_INFINITY. Then I
iterate through every possible dimension, sign (0 or 1) as well as possible
threshold value up to n — calculating the accuracy for every possibility (because
I iterate through every possibility, however, this is not an implementation
that maximizes efficiency). This allows my to find the decision stump that
maximizes the weight, once I find that, I set my values of
dimensionPredictionChampion, signPredictionChampion, and valuePredictionChampion
to their optimal values.

/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the small_training.txt and small_test.txt datasets instead,
 *  otherwise this will take too long)
 **************************************************************************** */

      k          T         test accuracy       time (seconds)
   --------------------------------------------------------------------------
       5        80              0.9                 .243
       10       160            0.9125               .777
       2        80              0.7                 .175
       4        50              .8875               .156
       5        160             0.9                 .46
       5        5000            0.825                8.738
       30       160             0.925               1.723
       30       500             0.95                5.293
       25       500             0.9875              5.027
       20       500             0.9625              3.884
       24       500             0.9875              4.386
       24       1000            0.9875              9.286

/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */

*I used small_training.txt and small_test.txt as my implementation does
not run in nlogn time

1. My strategy to find the optimal k and T was to keep T constant and find the
number of clusters that optimized the accuracy. You can see this in
the chart above, as I held T constant at 500 and tried different values of k
until I found the optimal value, which --- for my specific implementation ---
I determined to be 24. I then tested different values of T, and I
found that with k = 24 and T = 1000, I can achieve an accuracy of 0.9875
with a running time of 9.286 seconds, which is under 10 seconds.

2. A small value of T leads to low test accuracy because there are fewer
iterations of the boosting algorithm, and the iterations allow
us to refine the weak learner by updating the weights as needed. With
fewer iterations, there is less training, and therefore a lower
accuracy score.

3. a k that is too small leads to low test accuracy because points that are
far away are grouped into the same cluster, and therefore the clusters
are not a very accurate representation of which points are truly near each other.
If k is too large, points that are close together are grouped into too many
clusters, and the grouping is no longer meaningful/helpful.

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */

N/A
