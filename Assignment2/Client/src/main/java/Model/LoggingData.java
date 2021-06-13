package Model;

public class LoggingData implements Comparable<LoggingData> {
    private final String startTime;
    private final String requestType;
    private final Integer latency;
    private final Integer responseCode;

    public LoggingData(String startTime, String requestType, Integer latency, Integer responseCode) {
        this.startTime = startTime;
        this.requestType = requestType;
        this.latency = latency;
        this.responseCode = responseCode;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public Integer getLatency() {
        return latency;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    @Override
    public int compareTo(LoggingData o) {
        return this.latency - o.latency;
    }
}