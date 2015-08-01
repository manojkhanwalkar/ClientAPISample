package util;

import java.util.concurrent.atomic.AtomicLong;

public class IdCreator {


    static AtomicLong idgenerator = new AtomicLong();

    static
    {
        idgenerator.set(System.nanoTime());
    }


    public static long getId()
    {
        return idgenerator.incrementAndGet();
    }

}
