package Model;

import io.swagger.client.ApiClient;
import io.swagger.client.api.TextbodyApi;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import static java.lang.Thread.sleep;

public class GetWordCount implements Runnable {
    HashSet<String> words;
    DataBuffer dataBuffer;
    private final TextbodyApi apiInstance;

    public GetWordCount(DataBuffer dataBuffer, ApiClient apiClient) {
        this.dataBuffer = dataBuffer;
        this.apiInstance = new TextbodyApi(apiClient);
        words = new HashSet<>();
        words.add("the");
        words.add("for");
        words.add("Building");
        words.add("Scalable");
        words.add("Distributed");
        words.add("Insert");
        words.add("text");
        words.add("future");
        words.add("requests");
        words.add("dimensions");
    }

    @Override
    public void run() {
        long startTime;
        long endTime;
        long latency;
        long count = 0;
        long totalLatency = 0;
        long maxLatency = Long.MIN_VALUE;
        try {
            while (!this.dataBuffer.allComplete) {
                sleep(1000);
                for (String word : words) {
                    URL url = new URL("http://test-927377083.us-east-1.elb.amazonaws.com/Server_war/textbody/wordcount/" + word);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    startTime = System.nanoTime();
                    connection.setRequestProperty("accept", "application/json");
                    if (connection.getResponseCode() == 200)
                        connection.getInputStream();
//                    apiInstance.getWordCountWithHttpInfo(word);
                    endTime = System.nanoTime();
                    latency = (endTime - startTime);
                    maxLatency = Math.max(maxLatency, latency);
                    count += 1;
                    totalLatency += latency;
                }
            }

            System.out.println("------------------ GET STATS -----------------------");
            System.out.println("Total requests:  " + count);
            System.out.println("Average latency: " + totalLatency / (1000000 * count) + "s");
            System.out.println("Maximum latency: " + maxLatency / 1000000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
