package client;

import data.Request;
import data.Response;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class RequestHolder implements Callable<Response> {

    Request request ;
    Notify notify;
    FutureTask futureTask;

    public RequestHolder(Request request, Notify notify) {
        this.request = request;
        this.notify = notify;
    }

    public RequestHolder(Request request)
    {
        this.request = request;
    }

    public void setFutureTask(FutureTask futureTask) {
        this.futureTask = futureTask;
    }

    public FutureTask getFutureTask() {
        return futureTask;
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
