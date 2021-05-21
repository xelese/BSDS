package Model;

import General.Commands;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    BlockingQueue<String> queue;
    String threadName;

    public Consumer(String threadName, BlockingQueue<String> queue) {

        // set ThreadName
        if (threadName == null || threadName.isEmpty())
            throw new IllegalArgumentException("thread name cannot be empty.");
        this.threadName = threadName;

        // validate queue
        if (queue == null) throw new IllegalArgumentException("queue cannot be null.");
        this.queue = queue;

        // initializr TextBodyApi class.
    }

    @Override
    public void run() {
        // initialize incoming data stream
        String incomingDataStream;

        try {
            do {
                // get data from queue
                incomingDataStream = queue.take();

                // print data stream on out.
                if (!incomingDataStream.equals(Commands.TerminationDataSignature.toString()))
                    System.out.println(incomingDataStream);

                // send data to server
                // try calling the analyzeNewLine(body, function);

            } while (!incomingDataStream.equals(Commands.TerminationDataSignature.toString()));

            // Indicate thread termination
            System.out.println(this.threadName + ": " + "Terminated");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
