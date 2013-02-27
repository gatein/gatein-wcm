package org.modeshape.tests.performance;

import java.util.concurrent.ThreadFactory;

public class ModeShapeThreadFactory implements ThreadFactory {

    public long number = 0;

    @Override
    public Thread newThread(Runnable r) {
        number++;
        String threadName = "wcm-worker-"+number;
        return new Thread(r, threadName);
    }
}
