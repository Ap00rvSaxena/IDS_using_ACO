import Jama.Matrix;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Apoorv on 04-Apr-17.
 */
public class IDS {
    private AntColony antColony;
    private Matrix W;
    private Matrix WTW;
    private Matrix X;
    private Matrix pheromone;
    private Matrix heuristics;
    private ArrayList<Double> AnomalyIndexes;
    private Vector<Integer> PidList;
    private FrequencyMapper frequencyMapper;
    private double initialPheromone;
    private double maxDeviation;
    private final int r;
    private final int c;
    private int maxiterations;

    public IDS(AntColony antColony, Matrix W, Matrix WTW, Matrix X, FrequencyMapper frequencyMapper, double initialPheromone, double alpha, double beta) {
        this.antColony = antColony;
        this.WTW = WTW;
        this.W = W;
        this.X = X;
        this.frequencyMapper = frequencyMapper;
        this.initialPheromone = initialPheromone;
        r = W.getRowDimension();
        c = W.getColumnDimension();
        calculateHeuristics(beta);
        initializePheromoneMatrix(alpha);
        maxiterations = Configuration.instance.Anomalyiterations;
        maxDeviation = Configuration.instance.maxDeviation;
    }

    public Matrix getPheromone() {
        return pheromone;
    }

    public ArrayList<Double> getAnomalyIndexes() {
        return AnomalyIndexes;
    }

    public Vector<Integer> getPidList(Integer[] indexes) {
        PidList = new Vector<>();
        for(int i =0; i<indexes.length;i++)
            PidList.add(frequencyMapper.getPIDList().get(indexes[i]));
        return PidList;
    }

    public void setPheromone(int i, int j, double value) {
        pheromone.set(i, j, value);
    }

    public Matrix getX() {
        return X;
    }

    public Matrix getHeuristics() {
        return heuristics;
    }

    public FrequencyMapper getFrequencyMapper() {
        return frequencyMapper;
    }

    private void calculateHeuristics(double beta) {
        AnomalyIndexes = new ArrayList<>();
        heuristics = new Matrix(X.getRowDimension(), X.getColumnDimension());
        for (int i = 0; i < X.getColumnDimension(); i++) {
            double[] frequency = new double[X.getRowDimension()];
            for (int j = 0; j < X.getRowDimension(); j++)
                frequency[j] = X.get(j,i);
            AnomalyIndexes.add(getAnomalyIndex(frequency));
//            System.out.println("Anomaly at IDS of " +i+" is " + (Math.abs(1.0 -AnomalyIndexes.get(i))));
            for (int j = 0; j < X.getRowDimension(); j++){
                heuristics.set(j, i, Math.pow((maxDeviation/(Math.abs(1.0 -AnomalyIndexes.get(i)))), beta));
            }
        }
    }

    private void initializePheromoneMatrix(double alpha) {
        pheromone = new Matrix(X.getRowDimension(), X.getColumnDimension());
        for (int i = 0; i < X.getRowDimension(); i++)
            for (int j = 0; j < X.getColumnDimension(); j++)
                pheromone.set(i, j, Math.pow(initialPheromone,alpha));
    }

    public void decayPheromone() {
        for (int i = 0; i < X.getRowDimension(); i++)
            for (int j = 0; j < X.getColumnDimension(); j++)
                pheromone.set(i, j, pheromone.get(i, j)*antColony.getDecreaseFactor());
    }

    public void logPheromoneMatrix() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00000");

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < X.getRowDimension();i++) {
            for (int j = 0; j < X.getColumnDimension();j++)
                stringBuilder.append(decimalFormat.format(pheromone.get(i,j))).append(" ");
            stringBuilder.append("\n");
        }

        Logger.instance.write(stringBuilder.toString());
    }

    public int getNumberOfPIDs() {
        return X.getColumnDimension();
    }

    public double getAnomalyIndex(double[] X) {

        double[] WTX = new double[c];
        for (int i = 0; i < c; i++) {
            WTX[i] = 0.0;
            for (int j = 0; j < r; j++) {
                WTX[i] += W.get(j, i) * X[j];
            }
        }

        double[] H = new double[c];
        for (int i = 0; i < c; i++) {
            H[i] = Configuration.instance.randomGenerator.nextDouble();
        }

//        System.out.println("H at 12 = "+H[12]);

        int itr = 0;
        while (itr < maxiterations){
            for (int i=0; i<c; i++){
                double denominator = 0.0;
                for (int j=0; j<c; j++){
                    denominator += WTW.get(i,j)*H[j];
                }

                H[i] = H[i] * WTX[i] / denominator;
            }

            itr++;
        }

//        System.out.println("WTW at 6,6 = "+WTW.get(6,6));
//        System.out.println("WTX at 6 = "+WTX[6]);
//        System.out.println("H at 12 = "+H[12]);

        double d = 0.0;
        for (int i = 0; i < c; i++) {
            d += H[i];
//            System.out.println("H at "+ i +" = "+H[i]);
        }
//        System.out.println("D = "+ d);
        return Math.abs(d - 1.0);
    }
}
