import Controller.CliController;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * input: java -jar foo.java input-text-file number-of-threads
 */
public class Client {

    public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException {
        // validate params
        if (args.length != 2)
            throw new IllegalArgumentException("Expected two parameters: <input-text-file> <number-of-threads> received " + args.length);

        // get data from the arguments
        String filePath = args[0];
        Integer threadCount = Integer.parseInt(args[1]);

        // validate file path
        if (filePath.isEmpty() || filePath == null)
            throw new IllegalArgumentException("File path cannot be empty or null");

        // validate thread count
        if (threadCount == null) throw new IllegalArgumentException("number of threads cannot be null");
        if (threadCount <= 0) throw new IllegalArgumentException("Thread count must be greater than 0");

        // throw error if file does not exist.
        File myFile = new File(filePath);
        if (!myFile.exists()) throw new FileNotFoundException("file " + filePath + " not found!");

        // Delegate execution to controller
        CliController controller = new CliController();
        controller.run(myFile, threadCount, 100);
    }
}
