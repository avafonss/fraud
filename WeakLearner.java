public class WeakLearner {

    private int numDimensions; // number of dimensions, k
    private int dimensionPredictionChampion; // axis the decision stump
    // is perpendicular to
    // x-axis = 0, y-axis = 1, etc.
    private int valuePredictionChampion; // the coordinate that partitions the space
    private int signPredictionChampion; // which side of the decision stump
    // should be labeled as zero
    // values less than vp are zero when sp is zero


    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {

        if (input == null || weights == null || labels == null) throw new
                IllegalArgumentException("Null Argument");

        // check that lengths are valid
        if (weights.length != input.length || labels.length != input.length) throw new
                IllegalArgumentException("Invalid lengths");

        // n = number of inputs
        int n = input.length;

        // k = number of dimensions
        this.numDimensions = input[0].length;


        // check that the weights are all non-negative
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] < 0) throw new
                    IllegalArgumentException("Weights must be non-negative");
        }

        // check that labels only contains binary values
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != 0 && labels[i] != 1) throw new
                    IllegalArgumentException("Labels[] must only contain"
                                                     + "binary values");
        }

        // declare variables to store winner
        dimensionPredictionChampion = 0;
        signPredictionChampion = 0;
        valuePredictionChampion = 0;
        double maximumWeight = Double.NEGATIVE_INFINITY;

        // INEFFICIENT SOLUTION -- try all possible values of dp, all possible
        // values of sp, all possible values of vp

        // iterate through every possible dimension
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            // iterate through every possible value prediction
            for (int value = 0; value < n; value++) {
                int vp = input[value][dimension];

                // iterate through every possible sign (only two)
                for (int sign = 0; sign <= 1; sign++) {
                    double currentAccuracy = 0.0;

                    // iterate through every input

                    for (int i = 0; i < n; i++) {
                        boolean correctPrediction;

                        // if signPrediction = 0
                        if (sign == 0) {
                            //  values less than vp are zero
                            correctPrediction = (((input[i][dimension] <= vp)
                                    && (labels[i] == 0)) || ((input[i][dimension] > vp)
                                    && (labels[i] == 1)));
                        }
                        else {
                            correctPrediction = (((input[i][dimension] <= vp)
                                    && (labels[i] == 1)) || ((input[i][dimension] > vp)
                                    && (labels[i] == 0)));
                        }

                        // add the weight to the accuracy only if this
                        // decision stump was correct
                        if (correctPrediction) {
                            currentAccuracy += weights[i];
                        }
                    }

                    // is this decision stump better than
                    // what we have seen before?

                    if (currentAccuracy > maximumWeight) {
                        // if so, update everything
                        maximumWeight = currentAccuracy;
                        this.dimensionPredictionChampion = dimension;
                        this.signPredictionChampion = sign;
                        this.valuePredictionChampion = input[value][dimension];
                    }


                }
            }

        }
    }


    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        // check for null sample
        if (sample == null) throw new IllegalArgumentException("Null Argument");

        // check that sample is the correct length
        if ((sample.length) != numDimensions) throw new
                IllegalArgumentException("Invalid sample length");


        return predictionHelper(sample, valuePredictionChampion,
                                signPredictionChampion, dimensionPredictionChampion);
    }

    // prediction helper method
    private int predictionHelper(int[] sample, int value, int sign, int dimension) {
        if (sample[dimension] <= value) return sign;
        else {
            return 1 - sign; // otherwise return the opposite
            // if 0 return 1, if 1, return 0 (logic used above)
        }
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dimensionPredictionChampion;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return valuePredictionChampion;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return signPredictionChampion;
    }

    // unit testing
    public static void main(String[] args) {
        // not required
    }

}
