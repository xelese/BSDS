package Model;

import General.Commands;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.TextbodyApi;
import io.swagger.client.model.ResultVal;
import io.swagger.client.model.TextLine;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private BlockingQueue<String> queue;
    private final String threadName;
    private DataBuffer dataBuffer;
    private TextbodyApi apiInstance;
    private TextLine body = new TextLine().message("");
    private final String function;

    public Consumer(String threadName, DataBuffer dataBuffer, ApiClient apiClient, String function) {
        // set the buffer
        this.dataBuffer = dataBuffer;

        // set ThreadName
        if (threadName == null || threadName.isEmpty())
            throw new IllegalArgumentException("thread name cannot be empty.");
        this.threadName = threadName;

        // set Function
        if (function == null || function.isEmpty())
            throw new IllegalArgumentException("thread name cannot be empty.");
        this.function = function;

        // validate queue
        if (dataBuffer.getQueue() == null) throw new IllegalArgumentException("queue cannot be null.");
        this.queue = dataBuffer.getQueue();

        // initializr TextBodyApi class.
        if (apiClient == null || apiClient.getBasePath() == null || apiClient.getBasePath().isEmpty())
            throw new IllegalArgumentException("base path URL cannot be empty.");
        this.apiInstance = new TextbodyApi(apiClient);


    }

    @Override
    public void run() {
        // initialize incoming data stream
        String incomingDataStream;

        ApiResponse<ResultVal> result;

        try {
            do {
                // get data from queue
                incomingDataStream = queue.take();

                // print data stream on out.
                if (!incomingDataStream.equals(Commands.TerminationDataSignature.toString())) {
//                    System.out.println(incomingDataStream);

                    // set text to the body
                    this.body.setMessage(incomingDataStream);

                    // debug counter
                    dataBuffer.textLineCounter.getAndIncrement();

                    // send data to server
                    result = apiInstance.analyzeNewLineWithHttpInfo(body, function);

                    // check the status code

                    if (result.getStatusCode() == 200) {
                        // 200 increment counter
                        dataBuffer.successCounter.getAndIncrement();
                    } else {
                        // 400 write to a text file for logging.
                        System.out.println(result.getData().getMessage());
                    }
                }

            } while (!incomingDataStream.equals(Commands.TerminationDataSignature.toString()));

            // Indicate thread termination
            System.out.println(this.threadName + ": " + "Terminated");

        } catch (InterruptedException | ApiException e) {
            e.printStackTrace();
        }
    }
}
