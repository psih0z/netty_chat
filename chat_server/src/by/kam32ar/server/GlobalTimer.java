package by.kam32ar.server;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

/**
 * Global idle timer
 */
public class GlobalTimer {

    private static Timer instance = null;

    private GlobalTimer() {
    }
    
    public static void release() {
        if (instance != null) {
            instance.stop();
        }
        instance = null;
    }
    
    public static Timer getTimer() {
        if(instance == null) {
            instance = new HashedWheelTimer();
        }
        return instance;
    }
}
