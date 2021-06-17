package Model;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.TextbodyApi;
import io.swagger.client.model.ResultVal;
import io.swagger.client.model.TextLine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private final BlockingQueue<String> queue;
    private final DataBuffer dataBuffer;
    private final TextbodyApi apiInstance;
    private final TextLine body = new TextLine().message("");
    private final String function;

    public Consumer(DataBuffer dataBuffer, ApiClient apiClient, String function) {
        // set the buffer
        this.dataBuffer = dataBuffer;

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

        long startTime;
        long endTime;
        long latency;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            while (this.dataBuffer.producerComplete.get() > 0 || this.dataBuffer.getQueue().size() > 0) {
                // get data from queue
                incomingDataStream = queue.take();
//                if (incomingDataStream.equals("TERMINATEALLTHEADSCOMMAND)(*&^*^&$%&^%$^%$#^%$#^%$#&^*%*&^%&*^%$%^#%T$HSGDVBCSNBVCDFN")) {
//                    break;
//                }

                // print data stream on out.

                // set text to the body
                this.body.setMessage(incomingDataStream);

                // debug counter
                dataBuffer.textLineCounter.getAndIncrement();

                // send data to server
                startTime = System.nanoTime();
                date = new Date();
                result = apiInstance.analyzeNewLineWithHttpInfo(body, function);
                endTime = System.nanoTime();
                latency = (endTime - startTime);

                // check the status code
                if (result.getStatusCode() >= 200 && result.getStatusCode() < 300) {
                    // 200 increment counter
                    dataBuffer.successCounter.getAndIncrement();
//                    System.err.println("message: " + result.getData().getMessage() +
//                            "  | status: " + result.getStatusCode() + "  |");
                } else {
                    dataBuffer.failCounter.getAndIncrement();
                    // 400 write to a text file for logging.
                    System.err.println("message: " + result.getData().getMessage() +
                            "| status: " + result.getStatusCode() + "|");
                    System.out.println(result.getData().getMessage());
                }

                // add logging data to the array.
                dataBuffer.loggingDataList.add(new LoggingData(dateFormat.format(date), "POST", (int) (latency / 1000000), result.getStatusCode()));
            }

        } catch (InterruptedException | ApiException e) {
            e.printStackTrace();
        }
    }
}
