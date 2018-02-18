import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;

/**
 * Created by Apoorv on 06-Apr-17.
 */

public class Ant extends Thread {
    private AntColony antColony;
    private int indexOfStartPID;
    private static int _nextNr = 0;
    private int id = _nextNr++;
    private Vector<Integer> PidList;
    private IDS ids;

    public Ant(AntColony antColony, IDS ids, Vector<Integer> PidList, int indexOfStartPID) {
        this.antColony = antColony;
        this.ids = ids;
        this.PidList = PidList;
        this.indexOfStartPID = indexOfStartPID;
    }

    public void run() {
        for (int i = 0; i < antColony.getMaximumNumberOfIterations(); i++) {
            nextIteration();
            try {
                antColony.getCyclicBarrier().await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public void nextIteration() {

        final HashSet<Integer> pidsToVisit = new HashSet<>();

        for (int i = 0;i < ids.getNumberOfPIDs();i++)
            pidsToVisit.add(i);

        pidsToVisit.remove(indexOfStartPID);
        final int[] indexMap = new int[ids.getNumberOfPIDs()-1];
        final ArrayList<Integer> pidTour = new ArrayList<>();

        int actualIndexOfPID = indexOfStartPID;
        pidTour.add(actualIndexOfPID);

        for (int i = 0; i < ids.getNumberOfPIDs()-1; i++) {
            double sumFitness = 0;
            final double[] cumulatedProbabilities = new double[ids.getNumberOfPIDs()-i];
            cumulatedProbabilities[0] = 0;

            final Iterator<Integer> iteratorPidToVisit = pidsToVisit.iterator();
            int tempIndex;
            int tourLength = ids.getNumberOfPIDs()-i;

            for (int j = 1; j < tourLength; j++) {
                tempIndex = iteratorPidToVisit.next();

                double sumFitnessPerCol = 0;
                for(int k=0;k<ids.getX().getRowDimension();k++){
                    sumFitnessPerCol += ids.getHeuristics().get(k,tempIndex)+ids.getPheromone().get(k, tempIndex);
                }
                sumFitness+= sumFitnessPerCol;

                cumulatedProbabilities[j] = sumFitness;
                indexMap[j-1] = tempIndex;
            }

            final double randomValue = sumFitness * Configuration.instance.randomGenerator.nextDouble();

            int lowerValue = 0;
            int higherValue = (cumulatedProbabilities.length - 1);
            int meanValue;

            while (1 < higherValue - lowerValue) {
                meanValue = (lowerValue + higherValue) >>> 1;
                if (randomValue < cumulatedProbabilities[meanValue])
                    higherValue = meanValue;
                else
                    lowerValue = meanValue;
            }

            actualIndexOfPID = indexMap[lowerValue];
            pidsToVisit.remove(actualIndexOfPID);
            pidTour.add(actualIndexOfPID);

        }

        double sumOfDeviation = 0;
        int sizeOfTour = pidTour.size();
        Vector <Integer> SystemPids = new Vector<>();
        Integer[] indexes = new Integer[sizeOfTour];
        pidTour.toArray(indexes);
        for(int j=0; j< indexes.length; j++){
            SystemPids.add(PidList.get(indexes[j]));
        }

//        System.out.println("Anomaly = " + ids.getAnomalyIndexes().get(indexes[0]));

        for (int i = 0; i < sizeOfTour; i++){
//            System.out.println("Anomaly = " + Math.abs(1 - ids.getAnomalyIndexes().get(indexes[i])));
            sumOfDeviation+=Math.abs(1 - ids.getAnomalyIndexes().get(indexes[i]));
        }

        //System.out.println("SumOfDeviation at " + id + "= " + decimalFormatDistance.format(sumOfDeviation));

        if (sumOfDeviation > antColony.getMaxDeviation()) {
            antColony.setMaxDeviation(sumOfDeviation);
            antColony.setTourWithMaxDeviation(new ArrayList<>(pidTour));
            DecimalFormat decimalFormatDistance = new DecimalFormat("0000.00");
            String info = "ant " + id + " found a new tour : " + decimalFormatDistance.format(sumOfDeviation) + " \t " + pidTour + " SystemPids :" +SystemPids;
            System.out.println(info);
            Logger.instance.writeNewLine(info);
        }

        double pheromone = Math.pow(antColony.getIncreaseFactor() * ( Configuration.instance.maxDeviation/sumOfDeviation ),antColony.getAlpha());

        for (int i = 0; i < sizeOfTour; i++) {
            for (int j = 0; j < ids.getX().getRowDimension(); j++){
                double currentPheromoneValue = ids.getPheromone().get(j,indexes[i]);
                currentPheromoneValue+= pheromone;
                ids.setPheromone(j, indexes[i], currentPheromoneValue);
            }
        }

        //ids.logPheromoneMatrix();
    }

}
