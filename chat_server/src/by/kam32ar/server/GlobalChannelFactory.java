package by.kam32ar.server;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.DatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class GlobalChannelFactory {

    private static ChannelFactory channelFactory = null;
    private static DatagramChannelFactory datagramChannelFactory = null;

    public static void release() {
        if (channelFactory != null) {
            channelFactory.releaseExternalResources();
        }
        if (datagramChannelFactory != null) {
            datagramChannelFactory.releaseExternalResources();
        }
        channelFactory = null;
        datagramChannelFactory = null;
    }

    public static ChannelFactory getFactory() {
        if (channelFactory == null) {
            channelFactory = new NioServerSocketChannelFactory();
        }
        return channelFactory;
    }
    
    public static DatagramChannelFactory getDatagramFactory() {
        if (datagramChannelFactory == null) {
            datagramChannelFactory = new NioDatagramChannelFactory();
        }
        return datagramChannelFactory;
    }

}
