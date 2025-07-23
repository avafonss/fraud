import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.LinkedList;

public class BoostingAlgorithm {

    private LinkedList<WeakLearner> weakLearnerList; // linked list to store
    // the weak learners used to train the model
    private Clustering clustering; // clustering
    private int[][] reducedInput; // input after dimensionality reduction
    private double[] weights; // weights -- array updated after each iteration
    private int[] labels; // binary labels

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels, Point2D[] locations, int k) {
        // check for null arguments
        if (input == null || labels == null || locations == null) throw new
                IllegalArgumentException("Null argument");


        if (input.length != labels.length)
            throw new IllegalArgumentException("invalid lengths");


        // n = number of inputs
        int n = input.length;
        this.labels = labels;


        // check for valid k value
        if (k < 1 || k > locations.length) throw new
                IllegalArgumentException("Invalid k value");

        // check that labels array only has binary values
        for (int i = 0; i < labels.length; i++) {
            if ((labels[i] != 0) && (labels[i] != 1)) throw new
                    IllegalArgumentException("Labels should only have binary values");
        }

        // check that lengths are valid

        //        if (labels.length != n || locations.length != n) throw new
        //        IllegalArgumentException("Invalid lengths");


        this.weights = new double[n];
        double startingValue = (double) 1 / n;
        reducedInput = new int[n][k];

        // reduce dimensions into k clusters
        clustering = new Clustering(locations, k);

        // populate the weights array, starting with every index at 1/n
        for (int i = 0; i < n; i++) {
            weights[i] = startingValue;
            this.reducedInput[i] = clustering.reduceDimensions(input[i]);
        }


        // create the linked list to store the weak learners
        weakLearnerList = new LinkedList<WeakLearner>();

    }

    // return the current weight of the ith point
    public double weightOf(int i) {
        return weights[i];
    }

    // apply one step of the boosting algorithm
    // each iteration will boost the quality of the model
    public void iterate() {

        // create weak learning using current weights and the input
        WeakLearner weakLearner = new WeakLearner(reducedInput, weights, labels);

        // store the weak learners in a linked list
        weakLearnerList.add(weakLearner);

        // For each input point, double weight if the new weak learner
        // mislabels the input point
        for (int i = 0; i < reducedInput.length; i++) {
            if (weakLearner.predict(reducedInput[i]) != labels[i]) {
                weights[i] = weights[i] * 2;
            }
        }

        // Re-normalize the weight array
        // all weights should sum to 1
        double sum = 0.0;

        // calculate the new sum
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
        }

        // divide each current weight value by the sum
        for (int j = 0; j < weights.length; j++) {
            weights[j] /= sum;
        }

    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        if (sample == null) throw new IllegalArgumentException("Invalid sample");

        int[] reducedSample = clustering.reduceDimensions(sample);


        int numZeros = 0;
        int numOnes = 0;

        for (WeakLearner weakLearner : weakLearnerList) {
            int weakLearnerPrediction = weakLearner.predict(reducedSample);
            if (weakLearnerPrediction == 0) {
                numZeros++;
            }
            else numOnes++;
        }

        // the boosted learner should predict the majority vote
        // use each weak learner created in each iteration to label the sample
        // point, and then output the label that was predicted most often
        // if there is a tie, predict 0
        if (numOnes > numZeros) {
            return 1;
        }
        else return 0;
    }

    // unit testing
    public static void main(String[] args) {

        Stopwatch stopwatch = new Stopwatch();
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet testing = new DataSet(args[1]);
        // number of clusters
        int k = Integer.parseInt(args[2]);
        // number of iterations
        int T = Integer.parseInt(args[3]);

        int[][] trainingInput = training.getInput();
        int[][] testingInput = testing.getInput();
        int[] trainingLabels = training.getLabels();
        int[] testingLabels = testing.getLabels();
        Point2D[] trainingLocations = training.getLocations();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(trainingInput, trainingLabels,
                                                        trainingLocations, k);
        for (int t = 0; t < T; t++)
            model.iterate();

        // calculate the training data set accuracy
        double training_accuracy = 0;
        for (int i = 0; i < training.getN(); i++)
            if (model.predict(trainingInput[i]) == trainingLabels[i])
                training_accuracy += 1;
        training_accuracy /= training.getN();

        // calculate the test data set accuracy
        double test_accuracy = 0;
        for (int i = 0; i < testing.getN(); i++)
            if (model.predict(testingInput[i]) == testingLabels[i])
                test_accuracy += 1;
        test_accuracy /= testing.getN();

        StdOut.println("Training accuracy of model: " + training_accuracy);
        StdOut.println("Test accuracy of model: " + test_accuracy);
        double time = stopwatch.elapsedTime();
        StdOut.println("Time elapsed: " + time + " seconds");
    }
}
