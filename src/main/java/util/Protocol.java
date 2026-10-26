package util;

/** Limits shared by the client and the server, so the two sides can't drift apart. */
public final class Protocol {

    /** Largest text message (bytes) either side accepts; larger ones close the connection with 1009. */
    public static final int MAX_MESSAGE_SIZE = 64 * 1024;

    private Protocol() {
    }
}
