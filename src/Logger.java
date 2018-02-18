import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public enum Logger {
    instance;

    public BufferedWriter logFile;
    private String currentDirectory = System.getProperty("user.dir");
    private String fileSeparator = System.getProperty("file.separator");
    private String dataDirectory = currentDirectory + fileSeparator + "data" + fileSeparator;

    public void init() {
        try {
            logFile = new BufferedWriter(new FileWriter(dataDirectory +"IDS_using_ACO.log"));
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void write(String text) {
        try {
            logFile.write(text);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void writeNewLine(String text) {
        try {
            logFile.write(text);
            logFile.newLine();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void close() {
        try {
            logFile.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}