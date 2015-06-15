package by.kam32ar.server.logic;

import java.util.List;

public class ListObjects extends AbstractObject {

	protected List<Message> messages;
	protected List<Client> clients;
	protected List<Room> rooms;
	protected String currentRoom;

	public ListObjects(int id) {
		super(id);
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public String getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(String currentRoom) {
		this.currentRoom = currentRoom;
	}

}
