import Controller.CliController;
import General.Config;

import java.io.FileNotFoundException;

/**
 * input: java -jar foo.java input-text-file number-of-threads
 */
public class Client {

    public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException {
        // validate params
        if (args.length != 3)
            throw new IllegalArgumentException("Expected three parameters: <input-text-file> <number-of-consumer-threads> <number-of-producer-threads>\n Received: " + args.length);

        // get data from the arguments
        String filePath = args[0];
        Integer consumerThreads = Integer.parseInt(args[1]);
        Integer producerThreads = Integer.parseInt(args[2]);

        // create a config from the extracted data.
        Config config = new Config(filePath, consumerThreads, producerThreads);

        // Delegate execution to controller
        CliController controller = new CliController();
        controller.run(config);
    }
}
