import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArtificialBeeColony {

    private int foodSource;
    private int dimension;
    private int populationSize;
    private int limit;

    private List<Double> lowerBound;
    private List<Double> upperBound;

    private long[] trail;

    private TargetFunction function;
    private boolean maximize;
    //nazwa?
    private List<List<Double>> foodSourcesLocation;
    private List<Double> fx;
    private List<Double> fit;

    private double bestFoodSourceValue;
    private List<Double> bestFoodSourceValuePosition;

    public ArtificialBeeColony(int foodSource,
                               int problemDimension,
                               int swarmPopulationSize,
                               int limit,
                               TargetFunction function,
                               List<Double> lowerBound,
                               List<Double> upperBound,
                               boolean maximize) {
        //Check dimensions
        if (problemDimension != lowerBound.size()) {
            throw new IllegalArgumentException("Dimension of problem and lower bound don't match");
        }
        if (problemDimension != upperBound.size()) {
            throw new IllegalArgumentException("Dimension of problem and upper bound don't match");
        }

        //Assign a value
        this.foodSource = foodSource;
        this.dimension = problemDimension;
        this.populationSize = swarmPopulationSize;
        this.limit = limit;
        this.function = function;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.maximize = maximize;

        //Initialize variables
        this.trail = new long[foodSource];
        this.foodSourcesLocation = new ArrayList<>(populationSize);
        this.fx = new ArrayList<>(populationSize);
        this.fit = new ArrayList<>(populationSize);
    }

    public void generateSolution() {
        phaseEmployed();
        phaseOnlooker();
        phaseScout();
        findBest();
    }

    public double getResultValue() {
        return bestFoodSourceValue;
    }

    public List<Double> getResultVariables() {
        return cloneFoodSource(bestFoodSourceValuePosition);
    }

    public void initialize() {
        generateNewFoodSourceLocations();
        calculateInitialFunctionValues();
        calculateInitialFitnessValues();
    }

    private void phaseEmployed() {
        //Generate a new solution
        for (int i = 0; i < foodSource; i++) {
            generateNewSolution(i);
        }
    }

    private void phaseOnlooker() {

        Double sumOfFitValues = fit.stream().mapToDouble(Double::doubleValue).sum();

        int counter = 0;
        int i = 0;

        // For each bee in population check with probability if we should look for new solution
        // if food source is skipped and there is other food source go back to the front
        while (counter < populationSize) {
            //TODO check minimize
            if (Math.random() < fit.get(i) / sumOfFitValues) {
                generateNewSolution(i);
                counter++;
            }
            i++;
            if (i == foodSourcesLocation.size()) i = 0;
        }
    }

    private void phaseScout() {
        for (int i = 0; i < trail.length; i++) {
            if(trail[i] > limit){
                foodSourcesLocation.set(i, generateNewFoodSourceLocation());
            }
        }
    }

    private void generateNewFoodSourceLocations() {
        for (int i = 0; i < foodSource; i++) {
            foodSourcesLocation.add(i, generateNewFoodSourceLocation());
        }
    }

    private List<Double> generateNewFoodSourceLocation() {
        List<Double> newFoodSource = new ArrayList<>(dimension);
        for (int j = 0; j < dimension; j++) {
            newFoodSource.add(j, generateDoubleInBounds(lowerBound.get(j), upperBound.get(j)));
        }
        return newFoodSource;
    }

    private double generateDoubleInBounds(double lowerBound, double upperBound) {
        return Math.random() * (upperBound - lowerBound) + lowerBound;
    }

    private int generateIntInBounds(int lowerBound, int upperBound) {

        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((upperBound - lowerBound) + 1) + lowerBound;
    }

    private void calculateInitialFunctionValues() {
        for (int i = 0; i < foodSourcesLocation.size(); i++) {
            fx.add(i, function.f(foodSourcesLocation.get(i)));
        }
    }

    private void calculateInitialFitnessValues() {
        for (int i = 0; i < fx.size(); i++) {
            fit.add(i, calculateFitnessValue(fx.get(i)));
        }
    }

    private double calculateFitnessValue(double value) {
        if (value < 0) {
            return 1 + Math.abs(value);
        } else {
            return 1 / (1 + value);
        }
    }

    private boolean performGreedySelection(double oldFitValue, double newFitValue) {
        if (maximize) {
            return newFitValue < oldFitValue;
        } else {
            return newFitValue > oldFitValue;
        }
    }

    private List<Double> cloneFoodSource(List<Double> listToClone) {
        List<Double> clone = new ArrayList<>(listToClone.size());
        for (int i = 0; i < listToClone.size(); i++) {
            clone.add(i, listToClone.get(i));
        }
        return clone;
    }

    private void generateNewSolution(int i) {
        //Select the random variable to change
        int variableId = generateIntInBounds(0, dimension - 1);

        //Select the random partner
        int randomPartner;
        do {
            randomPartner = generateIntInBounds(0, foodSourcesLocation.size() - 1);
        } while (randomPartner == i);

        //Create a new food location
        double X = foodSourcesLocation.get(i).get(variableId);
        double Xp = foodSourcesLocation.get(randomPartner).get(variableId);
        double tmpValue = generateDoubleInBounds(-1, 1) * (X - Xp) + X;

        //If crated value is not within acceptable range swap it to the edge of the range
        if (tmpValue < lowerBound.get(variableId)) tmpValue = lowerBound.get(variableId);
        if (tmpValue > upperBound.get(variableId)) tmpValue = upperBound.get(variableId);

        //copy old value and change that one variable we were modifying
        List<Double> tmpFoodSource = cloneFoodSource(foodSourcesLocation.get(i));
        tmpFoodSource.set(variableId, tmpValue);

        //Calculate fitness of new element
        double newElementFunctionValue = function.f(tmpFoodSource);
        double newElementFitness = calculateFitnessValue(newElementFunctionValue);

        //Replace old elements if new elements are better and reset trail else increase trail
        if (performGreedySelection(fit.get(i), newElementFitness)) {
            foodSourcesLocation.set(i, tmpFoodSource);
            fx.set(i, newElementFunctionValue);
            fit.set(i, newElementFitness);
            trail[i] = 0;
        } else {
            trail[i]++;
        }
    }

    private void findBest() {
        double bestValue = fx.get(0);
        int bestValueIndex = 0;
        for (int i = 1; i < fx.size(); i++) {
            if (maximize) {
                if (bestValue < fx.get(i)) {
                    bestValue = fx.get(i);
                    bestValueIndex = i;
                }
            } else {
                if (bestValue > fx.get(i)) {
                    bestValue = fx.get(i);
                    bestValueIndex = i;
                }
            }
        }
        if(maximize){
            if(bestValue > bestFoodSourceValue){
                bestFoodSourceValuePosition = cloneFoodSource(foodSourcesLocation.get(bestValueIndex));
                bestFoodSourceValue = bestValue;
            }
        }else{
            if(bestValue < bestFoodSourceValue){
                bestFoodSourceValuePosition = cloneFoodSource(foodSourcesLocation.get(bestValueIndex));
                bestFoodSourceValue = bestValue;
            }
        }
    }

}
