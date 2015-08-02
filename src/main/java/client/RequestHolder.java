package client;

import data.Request;
import data.Response;

import java.util.concurrent.Callable;

/**
 * Created by mkhanwalkar on 8/2/15.
 */
public class RequestHolder implements Callable<Response> {

    Request request ;
    Notify notify;

    public RequestHolder(Request request, Notify notify) {
        this.request = request;
        this.notify = notify;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
    }


    public void setResponse(Response response)
    {
        this.response = response;
    }
    Response response;

    @Override
    public Response call() throws Exception {


        return response;


    }


}
