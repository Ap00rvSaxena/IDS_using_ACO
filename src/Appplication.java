import java.io.IOException;

/**
 * Created by Apoorv on 04-Apr-17.
 */
public class Appplication {

    public static void main(String args[]) throws IOException {
        Logger.instance.init();

        AntColony antColony = new AntColony();

        IDSLoader idsLoader = new IDSLoader(antColony);

        double sumOfMaximumAnomalyIndexes = 0;

        for (int i = 0;i < 10; i++) {
            antColony.solveIDS(idsLoader.getTrainedData(antColony.getInitialPheromone(),
                    antColony.getAlpha(),
                    antColony.getBeta()));
            sumOfMaximumAnomalyIndexes += antColony.getMaxDeviation();
        }

        String avg = "Average : " +  sumOfMaximumAnomalyIndexes/ 10;
        System.out.println(avg);
        Logger.instance.writeNewLine(avg);
        Logger.instance.close();
    }
}
