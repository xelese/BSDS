import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    private final Map<String, Integer> map;

    public Data() {
        this.map = new ConcurrentHashMap<>();

    }

    public Map<String, Integer> getMap() {
        return map;
    }
}
