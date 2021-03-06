package by.kam32ar.server.utils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jboss.netty.logging.AbstractInternalLogger;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class Log {
    
    private static final String LOGGER_NAME = "traccar";
    
    private static Logger logger = null;
    
    public static void setupLogger(Properties properties) throws IOException {

        Layout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %5p: %m%n");

        Appender appenderConsole = new ConsoleAppender(layout);
        Appender appenderFile = new DailyRollingFileAppender(
                layout, properties.getProperty("logger.file"), "'.'yyyyMMdd");

        LogManager.resetConfiguration();
        logger = Logger.getLogger(LOGGER_NAME);
        logger.addAppender(appenderConsole);
        logger.addAppender(appenderFile);
        logger.setLevel(Level.ALL);

        // Workaround for "Bug 745866 - (EDG-45) Possible netty logging config problem"
        InternalLoggerFactory.setDefaultFactory(new InternalLoggerFactory() {
            @Override
            public InternalLogger newInstance(String string) {
                return new NettyInternalLogger();
            }
        });
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(LOGGER_NAME);
            logger.setLevel(Level.OFF);
        }
        return logger;
    }
    
    public static void logSystemInfo() {
        try {
            OperatingSystemMXBean operatingSystemBean = ManagementFactory.getOperatingSystemMXBean();
            Log.info("Operating System" +
                " name: " + operatingSystemBean.getName() +
                " version: " + operatingSystemBean.getVersion() +
                " architecture: " + operatingSystemBean.getArch());

            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            Log.info("Java Runtime" +
                " name: " + runtimeBean.getVmName() +
                " vendor: " + runtimeBean.getVmVendor() +
                " version: " + runtimeBean.getVmVersion());

            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            Log.info("Memory Limit" +
                " heap: " + memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024) + "mb" +
                " non-heap: " + memoryBean.getNonHeapMemoryUsage().getMax() / (1024 * 1024) + "mb");
        } catch (Exception e) {
            Log.warning("Failed to get system info");
        }
    }
    
    public static void error(String msg) {
        getLogger().error(msg);
    }

    public static void warning(String msg) {
        getLogger().warn(msg);
    }

    public static void warning(Throwable exception) {
        warning(null, exception);
    }

    public static void warning(String msg, Throwable exception) {
        StringBuilder s = new StringBuilder();
        if (msg != null) {
            s.append(msg);
            s.append(" - ");
        }
        if (exception != null) {
            String exceptionMsg = exception.getMessage();
            if (exceptionMsg != null) {
                s.append(exceptionMsg);
                s.append(" - ");
            }
            s.append(exception.getClass().getName());
            StackTraceElement[] stack = exception.getStackTrace();
            if (stack.length > 0) {
                s.append(" (");
                s.append(stack[0].getFileName());
                s.append(":");
                s.append(stack[0].getLineNumber());
                s.append(")");
            }
        }
        getLogger().warn(s.toString());
    }

    public static void info(String msg) {
        getLogger().info(msg);
    }

    public static void debug(String msg) {
        getLogger().debug(msg);
    }

    /**
     * Netty logger implementation
     */
    private static class NettyInternalLogger extends AbstractInternalLogger {

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void debug(String string) {
            debug(string, null);
        }

        @Override
        public void debug(String string, Throwable thrwbl) {
        }

        @Override
        public void info(String string) {
            info(string, null);
        }

        @Override
        public void info(String string, Throwable thrwbl) {
        }

        @Override
        public void warn(String string) {
            warn(string, null);
        }

        @Override
        public void warn(String string, Throwable thrwbl) {
            getLogger().warn("netty warning: " + string);
        }

        @Override
        public void error(String string) {
            error(string, null);
        }

        @Override
        public void error(String string, Throwable thrwbl) {
            getLogger().error("netty error: " + string);
        }

    }

}
