package by.kam32ar.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.channel.Channel;

import by.kam32ar.server.common.Envelope;
import by.kam32ar.server.logic.Client;

public class ClientsManager {

	private Map<Client, Channel> clients;
	
	public ClientsManager() {
		clients = new HashMap<Client, Channel>();
	}
	
	public int addClient(Client client, Channel channel) {
		if (getChannel(client) == null) {
			clients.put(client, channel);
			return 1;
		}
		
		return 0;
	}
	
	public int writeChannel(String name, Envelope envelope) {
		Channel channel = getChannel(new Client(0, name, null));
		if (channel != null) {
			channel.write(envelope);
			return 1;
		}

		return 0;
	}
	
	public Client removeClient(Channel channel) {
		Client key = null;
		if (clients.containsValue(channel)) {
			for (Entry<Client, Channel> entry : clients.entrySet()) {
				if (channel.equals(entry.getValue())) {
					key = entry.getKey();
				}
			}
			
			clients.remove(key);
		}
		
		return key;
	}
	
	public int sendPrivateMessage(Client client, Envelope envelope) {
		Channel channel = null;
		if ((channel = getChannel(client)) != null) {
			channel.write(envelope);
			return 1;
		}
		
		return 0;
	}
	
	public List<Client> getClients(List<Channel> channels) {
		List<Client> list = new ArrayList<Client>();
		
		for (Channel channel : channels) {
			for (Entry<Client, Channel> entry : clients.entrySet()) {
				if (channel.equals(entry.getValue())) {
					list.add(entry.getKey());
				}
			}
		}
		
		return list;
	}
	
	public Channel getChannel(Client client) {
		for (Entry<Client, Channel> entry : clients.entrySet()) {
			if (entry.getKey().getNick().equals(client.getNick())) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public Client getClient(Channel channel) {
		for (Entry<Client, Channel> entry : clients.entrySet()) {
			if (channel.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		
		return null;
	}

	public Client setRoom(String name, Channel channel) {
		Client client = getClient(channel);
		client.setRoom(name);
		
		return client;
	}
	
}
