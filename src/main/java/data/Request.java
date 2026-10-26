package data;



        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import java.io.Serializable;

/**
 *
 * @author mkhanwalkar
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class Request  implements Serializable {



  //  static final String service  = "/request";


    long requestId;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long id) {
        this.requestId = id;
    }

    String payLoad;

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", payLoad='" + payLoad + '\'' +
                '}';
    }


}



