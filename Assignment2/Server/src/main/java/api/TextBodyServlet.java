package api;

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
import java.util.HashMap;
import java.util.Map;

import java.util.stream.Collectors;

@WebServlet(name = "api.TextBodyServlet", urlPatterns = {"/textbody/*"})
public class TextBodyServlet extends HttpServlet {

    private final Gson gson = new Gson();

    PooledChannel pooledChannel;

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
//                        for (String s : map.keySet()) {
                        Channel c = pooledChannel.getPool().borrowObject();
//                            c.basicPublish("", "TestRabbitMQ", null, ("{" + s + ":" + map.get(s) + "}").getBytes());
                        c.basicPublish("", "TestRabbitMQ", null, consumerString(map).getBytes());
                        pooledChannel.getPool().returnObject(c);
//                        }
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
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("GET works!");
    }

    private void intResponseComposer(HttpServletResponse response, int status, Integer message) throws IOException {
        response.setStatus(status);
        response.getWriter().print(gson.toJson(new Response<>(message)));
    }

    private Map<String, Integer> wordCount(String data) {
        Map<String, Integer> map = new HashMap<>();
        for (String s : data.split("\\s+")) {
            if (s.length() > 0)
                map.put(s, map.getOrDefault(s, 0) + 1);
        }
        return map;
    }

    private String consumerString(Map<String, Integer> map) {
        StringBuilder stringBuilder = new StringBuilder("{");
        for (String s : map.keySet()) {
            stringBuilder.append(s).append(":").append(map.get(s)).append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()).append("}");
        return stringBuilder.toString();
    }
}
