package data;

        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import com.fasterxml.jackson.annotation.JsonProperty;
        import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {



    long requestId;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long id) {
        this.requestId = id;
    }



}


/*

{"Database Health Check":{"healthy":true,"message":"Datebase: AEROSPIKE, Connection: SUCCESS"},"Queue Health Check":{"healthy":true,"message":"Queue: RABBITMQ, Connection: SUCCESS"},"deadlocks":{"healthy":true}}
*/