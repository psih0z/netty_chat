package by.kam32ar.server;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import by.kam32ar.server.common.Decoder;
import by.kam32ar.server.common.Encoder;
import by.kam32ar.server.handlers.EchoServerHandler;
import by.kam32ar.server.handlers.FlashPolicyServerDecoder;
import by.kam32ar.server.handlers.FlashPolicyServerHandler;
import by.kam32ar.server.model.DataManager;
import by.kam32ar.server.model.DatabaseDataManager;
import by.kam32ar.server.utils.Log;


public class ServerManager {

	private final List<Server> servers = new LinkedList<Server>();
	
	private DataManager dataManager;
	
	private RoomsManager roomsManager;
	
	private ClientsManager clientsManager;
	
	private boolean loggerEnabled;

    public boolean isLoggerEnabled() {
        return loggerEnabled;
    }
	
    public DataManager getDataManager() {
    	return dataManager;
    }
    
    public RoomsManager getRoomsManager() {
		return roomsManager;
	}

	public ClientsManager getClientsManager() {
		return clientsManager;
	}
	
	private static final ChannelGroup globalChat = new DefaultChannelGroup();
	
	public boolean addChannelInGlobalChat(Channel channel) {
		return globalChat.add(channel);
	}
	
	public boolean removeChannelInGlobalChat(Channel channel) {
		return globalChat.remove(channel);
	}
	
	public ChannelGroupFuture writeGlobalChat(Object msg) {
		return globalChat.write(msg);
	}

	private Properties properties;

    public Properties getProperties() {
        return  properties;
    }
	
	public void init(String[] args) throws Exception {
		
		// Load properties
        properties = new Properties();
        if (args.length > 0) {
            properties.loadFromXML(new FileInputStream(args[0]));
        }
		
        loggerEnabled = Boolean.valueOf(properties.getProperty("logger.enable"));
        if (loggerEnabled) {
            Log.setupLogger(properties);
        }
        
        dataManager = new DatabaseDataManager(properties);
        clientsManager = new ClientsManager();
        roomsManager = new RoomsManager(this);
        
		initGameServer(Integer.valueOf(properties.getProperty("game.port")));
		initSecureServer(Integer.valueOf(properties.getProperty("secure.port")));
	}
	
	protected void initGameServer(final int gamePort) {
		this.servers.add(new Server(this, new ServerBootstrap(), gamePort) {
			@Override
			protected void addSpecificHandlers(ChannelPipeline pipeline) {
//				pipeline.addFirst("buffer", new AutoFlush());
				pipeline.addLast("encoder", new Encoder());
				pipeline.addLast("decoder", new Decoder());
				pipeline.addLast("handler", new EchoServerHandler(ServerManager.this));
			}
		});
	}
	
	protected void initSecureServer(final int securePort) {
		this.servers.add(new Server(this, new ServerBootstrap(), securePort) {
			@Override
			protected void addSpecificHandlers(ChannelPipeline pipeline) {
				pipeline.addLast("decoder", new FlashPolicyServerDecoder());
				pipeline.addLast("handler", new FlashPolicyServerHandler(securePort));
			}
		});
	}
	
	public void start() {
		for (Server server : servers) {
			server.start();
		}
	}
	
	public void stop() {
		for (Server server : servers) {
			server.stop();
		}
	}
	
}
