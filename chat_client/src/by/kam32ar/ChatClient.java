package by.kam32ar;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import by.kam32ar.handler.ChatClientHandler;
import by.kam32ar.server.common.Decoder;
import by.kam32ar.server.common.Encoder;
import by.kam32ar.server.common.Envelope;
import by.kam32ar.server.common.RequestType;
import by.kam32ar.server.common.Type;
import by.kam32ar.server.logic.AbstractObject;
import by.kam32ar.server.logic.Client;
import by.kam32ar.server.logic.Message;
import by.kam32ar.server.logic.Room;
import by.kam32ar.server.utils.Utilites;

public class ChatClient {

//	public static void main(String[] args) throws IOException {
//		
//		new ChatClient("localhost", 9092).run();
//	}
	
	private final String host;
	private final int port;
	private String nickname;
	
	private Channel channel;
	
	private ChatInterface chatUI;

	public ChatClient(String host, int port) {
		this.port = port;
		this.host = host;
	}
	
	public ChatClient(String host, int port, String nickname) {
		this(host, port);
		this.nickname = nickname;		
	}

	public ChatInterface getChatUI() {
		return chatUI;
	}

	public void setChatUI(ChatInterface chatUI) {
		this.chatUI = chatUI;
	}
	
	public ChatClient run() {
		try {
			ChannelFactory factory = new NioClientSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool());
			ClientBootstrap bootstrap = new ClientBootstrap(factory);
			
			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				
				@Override
				public ChannelPipeline getPipeline() throws Exception {
					ChannelPipeline pipeline = Channels.pipeline();
					pipeline.addLast("encoder", new Encoder());
					pipeline.addLast("decoder", new Decoder());
					pipeline.addLast("handler", new ChatClientHandler(ChatClient.this));

					return pipeline;
				}
			});
			
			channel = bootstrap.connect(new InetSocketAddress(host, port)).getChannel();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		
		return this;
	}
	
	public void disconnect() {
		channel.disconnect();
	}
	
	private void sendAbstractMessage(AbstractObject object, RequestType requestType) {
		try {
			Envelope response = new Envelope(Type.REQUEST);
			response.setRequestType(requestType);
			response.setPayloadFromString((object == null) ? "" : object.toString());
			
			channel.write(response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
	}
	
	public void handshake() {
		Client client = new Client(0, nickname, null);
		sendAbstractMessage(client, RequestType.HANDSHAKE);
	}
	
	public void sendMsg(String room, String msg) {
		if (room == null) {
			room = "lobby";
		}
		Message message = new Message(0, room, nickname, 0, msg);
		sendAbstractMessage(message, RequestType.SEND_CHAT);
	}
	
	public void sendPrivateMsg(String room, String msg) {
		if (room == null) {
			room = "lobby";
		}
		Message message = new Message(0, room, nickname, 0, msg);
		sendAbstractMessage(message, RequestType.SEND_PRIVATE_MSG);
	}
	
	public void createRoom(Room room) {
		sendAbstractMessage(room, RequestType.CREATE_ROOM);
	}
	
	public void enterRoom(Room room) {
		sendAbstractMessage(room, RequestType.ENTER_ROOM);
	}
	
	public void addSystemMessage(String msg) {
		Message message = new Message(0, "lobby", "Система", Utilites.currentTime(), msg);
		List<Message> messages = new ArrayList<Message>();
		messages.add(message);
		this.chatUI.addMessages(messages);
	}
	
	public void addMessages(List<Message> messages) {
		this.chatUI.addMessages(messages);
	}

}
