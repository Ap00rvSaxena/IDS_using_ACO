import Jama.Matrix;
import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by Apoorv on 04-Apr-17.
 */
public class IDSLoader {
    private String currentDirectory = System.getProperty("user.dir");
    private String fileSeparator = System.getProperty("file.separator");
    private String dataDirectory = currentDirectory + fileSeparator + "data" + fileSeparator;
    private AntColony antColony;

    public IDSLoader(AntColony antColony) {
        this.antColony = antColony;
    }

    public IDS getTrainedData(double initialPheromone,double alpha,double beta) throws IOException {
        BufferedReader fin = new BufferedReader(new FileReader(dataDirectory + "train.dat"));
        String line;
        StringTokenizer linetoken;

        line = fin.readLine();
        linetoken = new StringTokenizer(line);
        int row = Integer.parseInt(linetoken.nextToken());
        int col = Integer.parseInt(linetoken.nextToken());

        Matrix W = new Matrix(row,col);

        for(int i=0;i<row;i++){
            line = fin.readLine();
            linetoken = new StringTokenizer(line);
            for(int j=0;j<col; j++){
                W.set(i,j,Double.parseDouble(linetoken.nextToken()));
            }
        }

        int c = W.getColumnDimension();
        int r = W.getRowDimension();

        Matrix WTW = new Matrix(c,c);
        for (int i=0;i<c;i++){
            for (int j=0;j<c;j++){
                double sum = 0.0;
                for (int k=0;k<r;k++){
                    sum += W.get(k,i)* W.get(k,j);
                }
                WTW.set(i, j, sum);
            }
        }

        FrequencyMapper frequencyMapper = new FrequencyMapper(dataDirectory + "input.dat");

        Matrix X = frequencyMapper.getX();

        return new IDS(antColony,W,WTW,X, frequencyMapper,initialPheromone,alpha,beta);
    }
}
