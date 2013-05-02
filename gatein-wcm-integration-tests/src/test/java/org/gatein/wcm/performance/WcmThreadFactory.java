package org.gatein.wcm.performance;

import java.util.concurrent.ThreadFactory;

public class WcmThreadFactory implements ThreadFactory {

    public long number = 0;

    @Override
    public Thread newThread(Runnable r) {
        number++;
        String threadName = "wcm-worker-"+number;
        return new Thread(r, threadName);
    }
}
