package by.kam32ar.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import by.kam32ar.ChatClient;
import by.kam32ar.server.common.Envelope;
import by.kam32ar.server.logic.Client;
import by.kam32ar.server.logic.ListObjects;
import by.kam32ar.server.logic.Message;
import by.kam32ar.server.logic.Room;

import com.google.gson.Gson;

public class ChatClientHandler extends SimpleChannelHandler {

	private ChatClient chatClient;
	
	public ChatClientHandler(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof Envelope) {
			Envelope envelope = (Envelope) e.getMessage();
			System.out.println(new String(envelope.getPayload()));
			switch (envelope.getRequestType()) {
			case HANDSHAKE:
				ListObjects listObjects = objectFromJSON(envelope.payloadToString(), ListObjects.class);
				this.chatClient.getChatUI().clearClients();
				this.chatClient.getChatUI().clearMessage();
				this.chatClient.getChatUI().clearRooms();
				this.chatClient.getChatUI().addMessages(listObjects.getMessages());
				this.chatClient.getChatUI().addRooms(listObjects.getRooms());
				this.chatClient.getChatUI().addClients(listObjects.getClients());
				this.chatClient.getChatUI().enterRoom(new Room(0, listObjects.getCurrentRoom()));
				return;
			case ENTER_ROOM:
				Client clientEnter = objectFromJSON(envelope.payloadToString(), Client.class);
				this.chatClient.getChatUI().addClient(clientEnter);
				this.chatClient.addSystemMessage("К нам присоединяется <"+clientEnter.getNick()+">");
				
				return;
			case EXIT_ROOM:
				Client clientExit = objectFromJSON(envelope.payloadToString(), Client.class);
				this.chatClient.getChatUI().removeClient(clientExit);
				this.chatClient.addSystemMessage("Нас покинул <"+clientExit.getNick()+">");
				return;
			case SEND_CHAT:
				Message message = objectFromJSON(envelope.payloadToString(), Message.class);
				this.chatClient.getChatUI().addMessage(message);
				return;
			case CREATE_ROOM:
				Room room = objectFromJSON(envelope.payloadToString(), Room.class);
				this.chatClient.addSystemMessage("Добавлена комната <"+room.getName()+">");
				this.chatClient.getChatUI().addRoom(room);
				return;
			default:
				throw new Exception("Unknown decoding state: "
						+ envelope.getRequestType());
			}

		} else {
			super.messageReceived(ctx, e);
		}
	}
	
	protected <T> T objectFromJSON(String json, Class<T> classOfT) {
		return new Gson().fromJson(json, classOfT);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		this.chatClient.addSystemMessage("Удаленный хост не отвечает!");
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		this.chatClient.addSystemMessage("Соединение прошло успешно!");
		this.chatClient.handshake();
		super.channelConnected(ctx, e);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		this.chatClient.addSystemMessage("Соединение разорвано!");
		super.channelDisconnected(ctx, e);
	}
	
}
