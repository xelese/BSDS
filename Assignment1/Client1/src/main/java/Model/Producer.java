package Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import General.Commands;

public class Producer implements Runnable {
    BlockingQueue<String> queue;
    Integer numberOfConsumerThreads;
    File file;

    public Producer(File file, BlockingQueue<String> queue, Integer numberOfConsumerThreads) throws IllegalArgumentException {
        // validate queue
        if (queue == null) throw new IllegalArgumentException("queue cannot be null.");
        this.queue = queue;

        // validate threads
        if (numberOfConsumerThreads == null) throw new IllegalArgumentException("threads cannot be null.");
        if (numberOfConsumerThreads <= 0) throw new IllegalArgumentException("threads must be greater than 0.");
        this.numberOfConsumerThreads = numberOfConsumerThreads;

        // validate file
        if (file == null) throw new IllegalArgumentException("file cannot be null.");
        this.file = file;
    }

    @Override
    public void run() {
        try {
            // initialize a scanner to read text.
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()) {
                // read data line by line
                String textLine = scanner.nextLine();

                // put in blocking queue
                if (!textLine.isEmpty()) queue.put(textLine);
            }

            // based on the total number of consumers here send Termination data.
            for (int i = 0; i < numberOfConsumerThreads; i++) {
                // Send Termination call
                queue.put(Commands.TerminationDataSignature.toString());
            }
        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
