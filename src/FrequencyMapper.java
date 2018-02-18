import Jama.Matrix;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 * Created by Apoorv on 04-Apr-17.
 */
public class FrequencyMapper {

    public Map <Integer, Vector<Integer>> frequencyTbl;
    public static int MAX_CALLS = 400;

    /*
    The constructor takes in input.dat file and generates a
    frequencyTable which maps PID to frequency
    there exits 8 different PID in frequencyTable
    and with each corresponding PID there exits a Vector of size 400
    each row of the vector contains number of time that system call has been called in input.dat
     */

    public FrequencyMapper(String processLog) throws IOException {
        BufferedReader processLogFin = new BufferedReader(new FileReader(processLog));
        frequencyTbl = new HashMap<>();

        String line;
        StringTokenizer tokenizer;

        while ((line = processLogFin.readLine()) != null){
            tokenizer = new StringTokenizer(line);

            int PID = Integer.parseInt(tokenizer.nextToken());
            int SystemCallID = Integer.parseInt(tokenizer.nextToken());

            if (!frequencyTbl.containsKey(PID)){
                Vector<Integer> frequencyList = new Vector<Integer>();
                for (int i=0; i<MAX_CALLS; i++){
                    frequencyList.add(0);
                }
                frequencyTbl.put(PID,frequencyList);
            }

            Vector<Integer> SysCallFreq = frequencyTbl.get(PID);
            SysCallFreq.set(SystemCallID, SysCallFreq.get(SystemCallID)+1);
        }
/*
        System.out.println("Frequency Table Total distinct PID:- " + frequencyTbl.size());
        System.out.println("Frequency Table Total Rows in each PID:- " + frequencyTbl.get(2867).size());
        System.out.println("Frequency Table PID=2867 :- " + frequencyTbl.get(2867));
  */  }

    /*
    GetX() method generates a Matrix X  (400 x 8)
    where 8 columns depicts the 8 distinct PID
    [ 5553, 3106, 2867, 3946, 3036, 5372, 5421, 3918]
    with each corresponding column there are 400 rows
    where each row depicts 400 system call id
    and each element of row shows percentage contribution ie

        Frequency(of respective systemcall)
         ----------------------------------
         Total Frequency of whole PID
     */

    public Matrix getX(){
        int n = MAX_CALLS;
        int m = frequencyTbl.size();
        Matrix matrix = new Matrix(n,m);

        Iterator<Map.Entry<Integer, Vector<Integer> >> iterator = frequencyTbl.entrySet().iterator();
        int j=0;
        while (iterator.hasNext()){
            Map.Entry<Integer, Vector<Integer> > pairs = (Map.Entry<Integer, Vector<Integer>>) iterator.next();
            Vector<Integer> SystemCallFreq = pairs.getValue();

            double totalfreq = 0.0;
            for (int i=0; i<n; i++){
                totalfreq += SystemCallFreq.get(i);
            }
            for (int i=0; i<n; i++){
                matrix.set(i, j, (double)SystemCallFreq.get(i) / totalfreq);
            }
            j++;
/*

            System.out.println("Total Frequency :- [" + pairs.getKey() +"] "+ totalfreq);
*/

        }
/*

        System.out.println("Matrix X[3(2867)][..]  :- [");
        for (int k=0; k<n;k++){
            System.out.print(matrix.get(k,2)+ ", ");
        }System.out.println(" ]");
*/

        return matrix;
    }

    /*
    getPIDList() method simply returns all the 8 distinct PID
    from the frequencyTable to the Application as a Vector List
     */
    public Vector<Integer> getPIDList() {
        Vector<Integer> pidlist = new Vector<>();

        Iterator<Map.Entry<Integer, Vector<Integer> >> iter = frequencyTbl.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<Integer, Vector<Integer>> pairs = (Map.Entry<Integer, Vector<Integer>>) iter.next();
            pidlist.add(pairs.getKey());
        }

        /*System.out.println("PID List:- " + pidlist);*/
        return pidlist;
    }
}

