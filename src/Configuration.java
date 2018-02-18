/**
 * Created by Apoorv on 04-Apr-17.
 */
public enum Configuration {
    instance;

    public double increaseFactor = 0.01;
    public double decreaseFactor = 0.6;
    public int numberOfAnts = 15;
    public double alpha = 10;
    public double beta = 0.8;
    public int Anomalyiterations = 5000;
    public double initialPheromone = 0.1;
    public int indexOfStartPID = 0;
    public MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());
    public double maxDeviation = 1000000.0000;
    public int iteration = 1;
}
