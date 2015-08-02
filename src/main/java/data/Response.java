package data;

        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import com.fasterxml.jackson.annotation.JsonProperty;
        import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {



    long requestId;

    String payLoad;

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long id) {
        this.requestId = id;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestId=" + requestId +
                ", payLoad='" + payLoad + '\'' +
                '}';
    }
}


/*

{"Database Health Check":{"healthy":true,"message":"Datebase: AEROSPIKE, Connection: SUCCESS"},"Queue Health Check":{"healthy":true,"message":"Queue: RABBITMQ, Connection: SUCCESS"},"deadlocks":{"healthy":true}}
*/