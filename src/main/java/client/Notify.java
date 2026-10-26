package client;

import data.Response;

/**
 * Created by mkhanwalkar on 8/2/15.
 */
public interface Notify {

    public void callback(Response response);

    /** Called instead of {@link #callback} if the connection closes before the response arrives. */
    default void onFailure(Throwable cause) {
        System.err.println("Request failed: " + cause);
    }
}
