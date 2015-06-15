package by.kam32ar.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import by.kam32ar.server.common.Envelope;
import by.kam32ar.server.common.RequestType;
import by.kam32ar.server.common.Type;
import by.kam32ar.server.logic.AbstractObject;
import by.kam32ar.server.logic.Client;
import by.kam32ar.server.logic.Room;

public class RoomsManager {

	public final static String LOBBY_ROOM = "lobby";

	private Map<String, ChannelGroup> rooms;

	private ServerManager manager;

	public RoomsManager(ServerManager manager) {
		rooms = new HashMap<String, ChannelGroup>();

		this.manager = manager;
		/*
		 * Create lobby room
		 */
		this.addRoom(LOBBY_ROOM);
	}

	public int addRoom(String name) {
		if (!rooms.containsKey(name)) {
			rooms.put(name, new DefaultChannelGroup());
			return 1;
		}

		return 0;
	}

	public Client enterRoom(String name, Channel channel)
			throws UnsupportedEncodingException {
		if (rooms.containsKey(name)) {
			
			Client client = manager.getClientsManager().getClient(channel);
			exitRoom(client, channel);
			
			client = manager.getClientsManager().setRoom(name, channel);
			rooms.get(name).write(
					getResponse(RequestType.ENTER_ROOM, client));
			rooms.get(name).add(channel);
			return client;
		}

		return null;
	}

	protected Envelope getResponse(RequestType type, AbstractObject object)
			throws UnsupportedEncodingException {
		Envelope response = new Envelope(Type.RESPONSE);
		response.setRequestType(type);
		response.setPayloadFromString(object.toString());

		return response;
	}

	public String containsChannel(Channel channel) {
		for (Entry<String, ChannelGroup> entry : rooms.entrySet()) {
			ChannelGroup channelGroup = entry.getValue();
			if (channelGroup.contains(channel)) {
				return entry.getKey();
			}
		}

		return null;
	}

	public int exitRoom(Client client, Channel channel) throws UnsupportedEncodingException {
		if (rooms.containsKey(client.getRoom())) {
			rooms.get(client.getRoom()).write(getResponse(RequestType.EXIT_ROOM, client));
			rooms.get(client.getRoom()).remove(channel);
			return 1;
		}
			

		return 0;
	}

	public int writeRoom(String name, Envelope envelope) {
		if (rooms.containsKey(name)) {
			rooms.get(name).write(envelope);
			return 1;
		}

		return 0;
	}

	public List<Room> getListRoom() {
		List<Room> list = new ArrayList<Room>();

		int i = 0;
		for (Entry<String, ChannelGroup> entry : rooms.entrySet()) {
			list.add(new Room(i, entry.getKey()));
			i++;
		}

		return list;
	}

	public List<Channel> getChannels(Channel channel) {
		List<Channel> channels = new ArrayList<Channel>();

		for (Entry<String, ChannelGroup> entry : rooms.entrySet()) {
			ChannelGroup channelGroup = entry.getValue();
			if (channelGroup.contains(channel)) {
				for (Channel ch : channelGroup) {
					channels.add(ch);
				}

				break;
			}
		}

		return channels;
	}

}
