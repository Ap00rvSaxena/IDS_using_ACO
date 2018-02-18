import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by Apoorv on 05-Apr-17.
 */
public class AntColony {

    private double increaseFactor;
    private double decreaseFactor;
    /*private Map<Vector<Integer>, Vector<Integer>> AnomalyIndexes;*/
    private ArrayList<Integer> tourWithMaxDeviation;
    private double MaxDeviation = 1000000.0000;
    private int maximumNumberOfIterations;
    private int numberOfAnts;
    private double alpha;
    private int startIndex;
    private IDS ids;
    private Ant[] ants;
    private CyclicBarrier cyclicBarrier;
    private double initialPheromone;
    private double beta;

    public AntColony() {
        increaseFactor = Configuration.instance.increaseFactor;
        decreaseFactor = Configuration.instance.decreaseFactor;
        numberOfAnts = Configuration.instance.numberOfAnts;
        alpha = Configuration.instance.alpha;
        beta = Configuration.instance.beta;
        maximumNumberOfIterations = Configuration.instance.iteration;
        initialPheromone = Configuration.instance.initialPheromone;
        cyclicBarrier = new CyclicBarrier(numberOfAnts + 1);
        startIndex = Configuration.instance.indexOfStartPID;
    }

    public int getMaximumNumberOfIterations() {
        return maximumNumberOfIterations;
    }

    public void setMaxDeviation(double maxDeviation) {
        MaxDeviation = maxDeviation;
    }

    public void setTourWithMaxDeviation(ArrayList<Integer> tourWithMaxDeviation) {
        this.tourWithMaxDeviation = tourWithMaxDeviation;
    }

    public double getMaxDeviation() { return MaxDeviation; }

    public double getIncreaseFactor() {
        return increaseFactor;
    }

    public double getDecreaseFactor() {
        return decreaseFactor;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getInitialPheromone() {
        return initialPheromone;
    }

    public double getBeta() { return beta; }

    public CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }

    public void solveIDS(IDS ids) {
        this.ids = ids;

        ants = new Ant[numberOfAnts];

        for (int i = 0; i < numberOfAnts; i++)
            ants[i] = new Ant(this,ids,ids.getFrequencyMapper().getPIDList(),startIndex);

        tourWithMaxDeviation = new ArrayList<>();
        MaxDeviation = Double.MIN_VALUE;

        solve();
    }

    public void solve() {
        for (Ant ant : ants)
            ant.start();

        for (int i = 0; i < maximumNumberOfIterations; i++)
            try {
                cyclicBarrier.await();
                ids.decayPheromone();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

        for (Ant ant : ants)
            try {
                ant.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        System.out.println("Maximum Anomaly  : " + MaxDeviation);
        Logger.instance.writeNewLine("Maximum Anomaly  : " + MaxDeviation);
    }

}
