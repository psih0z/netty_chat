package by.kam32ar.server;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public abstract class Server {

    private final Bootstrap bootstrap;
    
    private String address;
    private int port = 9092;
    
    public Server(ServerManager serverManager, Bootstrap bootstrap, int port) {
    	this.bootstrap = bootstrap;
    	this.port = port;
    	
    	if (bootstrap instanceof ServerBootstrap) {
            bootstrap.setFactory(GlobalChannelFactory.getFactory());
        } else if (bootstrap instanceof ConnectionlessBootstrap) {
            bootstrap.setFactory(GlobalChannelFactory.getDatagramFactory());
        }
    	
    	bootstrap.setPipelineFactory(new BasePipelineFactory(serverManager, this, port) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                Server.this.addSpecificHandlers(pipeline);
            }
        });
	}
    
    protected abstract void addSpecificHandlers(ChannelPipeline pipeline);
    
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Opened channels
     */
    private final ChannelGroup allChannels = new DefaultChannelGroup();

    public ChannelGroup getChannelGroup() {
        return allChannels;
    }

    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
        bootstrap.setPipelineFactory(pipelineFactory);
    }
    
    /**
     * Start server
     */
    public void start() {
        InetSocketAddress endpoint;
        if (address == null) {
            endpoint = new InetSocketAddress(port);
        } else {
            endpoint = new InetSocketAddress(address, port);
        }

        Channel channel = null;
        if (bootstrap instanceof ServerBootstrap) {
            channel = ((ServerBootstrap) bootstrap).bind(endpoint);
        } else if (bootstrap instanceof ConnectionlessBootstrap) {
            channel = ((ConnectionlessBootstrap) bootstrap).bind(endpoint);
        }

        if (channel != null) {
            getChannelGroup().add(channel);
        }
    }
    
    /**
     * Stop server
     */
    public void stop() {
        ChannelGroupFuture future = getChannelGroup().close();
        future.awaitUninterruptibly();
    }
    
}
