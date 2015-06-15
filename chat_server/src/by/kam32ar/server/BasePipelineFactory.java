package by.kam32ar.server;

import java.net.InetSocketAddress;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;

import by.kam32ar.server.utils.Log;


/**
  * Base pipeline factory
  */
public abstract class BasePipelineFactory implements ChannelPipelineFactory {

    private final Server server;
//    private final DataManager dataManager;
//    private final Boolean loggerEnabled;
//    private FilterHandler filterHandler;
    private Integer resetDelay;

    /**
     * Open channel handler
     */
    protected class OpenChannelHandler extends SimpleChannelHandler {

        private final Server server;

        public OpenChannelHandler(Server server) {
            this.server = server;
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
            server.getChannelGroup().add(e.getChannel());
        }
    }

    /**
     * Logging using global logger
     */
    protected class StandardLoggingHandler extends LoggingHandler {

        @Override
        public void log(ChannelEvent e) {
            if (e instanceof MessageEvent) {
                MessageEvent event = (MessageEvent) e;
                StringBuilder msg = new StringBuilder();

                msg.append("[").append(((InetSocketAddress) e.getChannel().getLocalAddress()).getPort());
                msg.append((e instanceof DownstreamMessageEvent) ? " -> " : " <- ");

                msg.append(((InetSocketAddress) event.getRemoteAddress()).getAddress().getHostAddress()).append("]");

                // Append hex message
                if (event.getMessage() instanceof ChannelBuffer) {
                    msg.append(" - HEX: ");
                    msg.append(ChannelBuffers.hexDump((ChannelBuffer) event.getMessage()));
                }

                Log.debug(msg.toString());
            } else if (e instanceof ExceptionEvent) {
                ExceptionEvent event = (ExceptionEvent) e;
                Log.warning(event.getCause());
            }
        }

    }

    public BasePipelineFactory(ServerManager serverManager, Server server, int port) {
        this.server = server;
        /*dataManager = serverManager.getDataManager();
        loggerEnabled = serverManager.isLoggerEnabled();
        reverseGeocoder = serverManager.getReverseGeocoder();

        String resetDelayProperty = serverManager.getProperties().getProperty(protocol + ".resetDelay");
        if (resetDelayProperty != null) {
            resetDelay = Integer.valueOf(resetDelayProperty);
        }

        String enableFilter = serverManager.getProperties().getProperty("filter.enable");
        if (enableFilter != null && Boolean.valueOf(enableFilter)) {
            filterHandler = new FilterHandler(serverManager);
        }*/
    }

    /*protected DataManager getDataManager() {
        return dataManager;
    }*/

    protected abstract void addSpecificHandlers(ChannelPipeline pipeline);

    @Override
    public ChannelPipeline getPipeline() {
        ChannelPipeline pipeline = Channels.pipeline();
        if (resetDelay != null) {
            pipeline.addLast("idleHandler", new IdleStateHandler(GlobalTimer.getTimer(), resetDelay, 0, 0));
        }
        pipeline.addLast("openHandler", new OpenChannelHandler(server));
        /*if (loggerEnabled) {
            pipeline.addLast("logger", new StandardLoggingHandler());
        }*/
        addSpecificHandlers(pipeline);
        /*if (filterHandler != null) {
            pipeline.addLast("filter", filterHandler);
        }
        if (reverseGeocoder != null) {
            pipeline.addLast("geocoder", new ReverseGeocoderHandler(reverseGeocoder));
        }
        pipeline.addLast("handler", new TrackerEventHandler(dataManager));*/
        return pipeline;
    }

}
