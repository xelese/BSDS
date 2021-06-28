package api;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import configuration.PooledChannel;
import configuration.PooledChannelFactory;
import model.Response;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "api.TextBodyServlet", urlPatterns = {"/textbody/*"})
public class TextBodyServlet extends HttpServlet {

    private final Gson gson = new Gson();

    PooledChannel pooledChannel;

    DynamoDB dynamoDB;

    @Override
    public void init() throws ServletException {
        super.init();
        GenericObjectPool<Channel> pool = new GenericObjectPool<>(new PooledChannelFactory());
        GenericObjectPoolConfig<Channel> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(1000);
        poolConfig.setMinIdle(128);
        poolConfig.setMaxIdle(256);
        poolConfig.setBlockWhenExhausted(true);
        pool.setConfig(poolConfig);
        pooledChannel = new PooledChannel(pool);
        try {
//            pooledChannel.getPool().addObjects(256);
            pooledChannel.getPool().preparePool();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AWSStaticCredentialsProvider credentials = new AWSStaticCredentialsProvider(new BasicSessionCredentials(
                "ASIAXQYKNMXAHUY3R47C",
                "eeZ2WnAFjVymLYRQ0TJlzMH+cDybp/ucthuByOso",
                "FwoGZXIvYXdzELH//////////wEaDJbu0T7IKKduU/T4pCLJAdYq7CTFYPpmR9dTHi0E1O1JssygfNWTXLZ3BQwIfyXKNOFpgNrl5ecsWqLhRKjWJ7F2kSz/KF+VVlV6z6zuQvVM06eNlgblgQ1d+gMW0hSBBKwOFHRfekwcUF8AnoMmk7DqsodXh9bmmSRI/HAmmkj6jEUZ/OyxRqWNyXmQQwztpdhFgoOtaQ8fLSkYF9boHKNKr5LbyP1KeqFVHzbQ1ru1XzbEjCXW/Palp9drfoGOEdlonIFnGmLPuazFsK9Zwn34f2A4F5ZeNyiU1eeGBjIt2L2sXczv0UOL1dcNqdqjwYenRie4aMNXLwIuSoy1AgHurJ42R1ZytYCYW3Dh")); //new ProfileCredentialsProvider("bsds");
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(credentials)
                .build();

        dynamoDB = new DynamoDB(client);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing function");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        switch (urlParts[1].toLowerCase()) {
            case "wordcount":
                Response<String> jsonData = gson.fromJson(request.getReader().lines().collect(Collectors.joining()), Response.class);
                Map<String, Integer> map = wordCount(jsonData.getMessage());
                try {
                    intResponseComposer(response, HttpServletResponse.SC_OK, map.size());
                    if (map.size() > 0) {
                        Channel c = pooledChannel.getPool().borrowObject();
                        c.basicPublish("", "TestRabbitMQ", null, map.toString().getBytes());
                        pooledChannel.getPool().returnObject(c);
                    }
                } catch (Exception e) {
                    intResponseComposer(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 0);
                    e.printStackTrace();
                }
                break;
            case "easteregg":
                intResponseComposer(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, 1);
                break;
            default:
                intResponseComposer(response, HttpServletResponse.SC_NOT_IMPLEMENTED, -1);
                break;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String urlPath = request.getPathInfo();
        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            StringResponseComposer(response, HttpServletResponse.SC_BAD_REQUEST, BigInteger.ZERO);
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        if ("wordcount".equals(urlParts[1].toLowerCase())) {
            try {
                String word = urlParts[2];
                Table table = dynamoDB.getTable("bsds");
                int count = 0;
                Map<String, Object> expressionAttributeValues = new HashMap<>();
                expressionAttributeValues.put(":w", word);

                ItemCollection<ScanOutcome> items = table.scan(
                        "word = :w",
                        "id, word, val",
                        null,
                        expressionAttributeValues
                );

                for (Item item : items) {
                    count += item.getInt("val");
                }

                if (count == 0)
                    StringResponseComposer(response, HttpServletResponse.SC_NOT_FOUND, BigInteger.ZERO);
                else
                    StringResponseComposer(response, HttpServletResponse.SC_OK, BigInteger.valueOf(count));
            } catch (Exception e) {
                StringResponseComposer(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, BigInteger.ZERO);
            }
        } else {
            StringResponseComposer(response, HttpServletResponse.SC_BAD_REQUEST, BigInteger.ZERO);
        }
    }

    private void StringResponseComposer(HttpServletResponse response, int status, BigInteger message) throws IOException {
        response.setStatus(status);
        response.getWriter().print(message);
    }

    private void intResponseComposer(HttpServletResponse response, int status, Integer message) throws IOException {
        response.setStatus(status);
        response.getWriter().print(message);
    }

    private Map<String, Integer> wordCount(String data) {
        Map<String, Integer> map = new HashMap<>();
        for (String s : data.split("\\s+")) {
            if (s.length() > 0)
                map.put(s, map.getOrDefault(s, 0) + 1);
        }
        return map;
    }
}
