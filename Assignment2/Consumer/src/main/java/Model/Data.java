package Model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private final Map<String, Integer> concurrentHashmap;

    public Data() {
        this.concurrentHashmap = new ConcurrentHashMap<>();
    }

    public Map<String, Integer> getConcurrentHashmap() {
        return concurrentHashmap;
    }
}
