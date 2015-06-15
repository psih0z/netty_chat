package by.kam32ar.server.handlers;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import by.kam32ar.server.RoomsManager;
import by.kam32ar.server.ServerManager;
import by.kam32ar.server.common.Envelope;
import by.kam32ar.server.common.RequestType;
import by.kam32ar.server.common.Type;
import by.kam32ar.server.logic.Client;
import by.kam32ar.server.logic.ListObjects;
import by.kam32ar.server.logic.Message;
import by.kam32ar.server.logic.Room;
import by.kam32ar.server.utils.Log;
import by.kam32ar.server.utils.Utilites;

import com.google.gson.Gson;

public class EchoServerHandler extends SimpleChannelHandler {

	protected ServerManager serverManager;

	public EchoServerHandler(ServerManager serverManager) {
		this.serverManager = serverManager;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {

		if (e.getMessage() instanceof Envelope) {
			Envelope envelope = (Envelope) e.getMessage();

			Log.debug("Тип запроса: " + envelope.getRequestType().toString());
			Log.debug("Данные: " + envelope.payloadToString());

			Envelope response = new Envelope(Type.RESPONSE);

			switch (envelope.getRequestType()) {
			case HANDSHAKE:
				Client client = objectFromJSON(envelope.payloadToString(),
						Client.class);
				identify(client, ctx.getChannel());

				ListObjects listObjects = calculateListObjects(client,
						ctx.getChannel());

				response.setRequestType(RequestType.HANDSHAKE);
				response.setPayloadFromString(listObjects.toString());
				
//				serverManager.getRoomsManager().writeRoom(RoomsManager.LOBBY_ROOM, responce);
				ctx.getChannel().write(response);

				return;
			case SEND_CHAT:
				Message message = objectFromJSON(envelope.payloadToString(), Message.class);
				
				Client client2 = serverManager.getClientsManager().getClient(ctx.getChannel());
				message.setRoom(client2.getRoom());
				message.setTime(Utilites.currentTime());
				
				serverManager.getDataManager().insertMessage(message);
				
				response.setRequestType(RequestType.SEND_CHAT);
				response.setPayloadFromString(message.toString());
				
				serverManager.getRoomsManager().writeRoom(message.getRoom(), response);

				return;
			case CREATE_ROOM:
				Room room = objectFromJSON(envelope.payloadToString(), Room.class);
				
				serverManager.getRoomsManager().addRoom(room.getName());
				
				response.setRequestType(RequestType.CREATE_ROOM);
				response.setPayloadFromString(room.toString());
				
				serverManager.writeGlobalChat(response);
				
				return;
			case ENTER_ROOM:
				Room room2 = objectFromJSON(envelope.payloadToString(), Room.class);
				Client client3 = serverManager.getRoomsManager().enterRoom(room2.getName(), ctx.getChannel());
				
				ListObjects listObjects2 = calculateListObjects(client3,
						ctx.getChannel());

				response.setRequestType(RequestType.HANDSHAKE);
				response.setPayloadFromString(listObjects2.toString());
				
				ctx.getChannel().write(response);
				return;
			case SEND_PRIVATE_MSG:
				Message message1 = objectFromJSON(envelope.payloadToString(), Message.class);
				message1.setTime(Utilites.currentTime());
				
				serverManager.getDataManager().insertMessage(message1);
				
				response.setRequestType(RequestType.SEND_CHAT);
				response.setPayloadFromString(message1.toString());
				
				serverManager.getClientsManager().writeChannel(message1.getRoom(), response);

				return;
			default:
				throw new Exception("Unknown decoding state: "
						+ envelope.getRequestType());
			}

//			ctx.getChannel().write(responce);

		} else {
			super.messageReceived(ctx, e);
		}
	}

	protected void identify(Client client, Channel channel) throws UnsupportedEncodingException {
		serverManager.getClientsManager().addClient(client, channel);
		serverManager.getRoomsManager().enterRoom(RoomsManager.LOBBY_ROOM,
				channel);
	}

	protected ListObjects calculateListObjects(Client client, Channel channel)
			throws Exception {
		ListObjects listObjects = new ListObjects(0);
		listObjects.setMessages(serverManager.getDataManager().selectMessages(
				serverManager.getRoomsManager().containsChannel(channel)));
		listObjects.setRooms(serverManager.getRoomsManager().getListRoom());
		listObjects.setClients(serverManager.getClientsManager().getClients(
				serverManager.getRoomsManager().getChannels(channel)));
		listObjects.setCurrentRoom(client.getRoom());

		return listObjects;
	}

	protected <T> T objectFromJSON(String json, Class<T> classOfT) {
		return new Gson().fromJson(json, classOfT);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
		 serverManager.addChannelInGlobalChat(ctx.getChannel());
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		
		serverManager.removeChannelInGlobalChat(ctx.getChannel());
		clientDisconnected(ctx.getChannel());
		
		super.channelDisconnected(ctx, e);
	}
	
	protected void clientDisconnected(Channel channel) throws UnsupportedEncodingException {
		Client client = serverManager.getClientsManager().getClient(channel);
		serverManager.getClientsManager().removeClient(channel);
		serverManager.getRoomsManager().exitRoom(client, channel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();

		Channel ch = (Channel) e.getChannel();
		ch.close();
	}

}
