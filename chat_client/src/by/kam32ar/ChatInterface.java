package by.kam32ar;

import java.util.List;

import by.kam32ar.server.logic.Client;
import by.kam32ar.server.logic.Message;
import by.kam32ar.server.logic.Room;

public interface ChatInterface {

	public void addMessages(List<Message> messages);
	
	public void addMessage(Message message);
	
	public void addClients(List<Client> clients);
	
	public void addClient(Client client);
	
	public void removeClient(Client client);
	
	public void addRooms(List<Room> rooms);
	
	public void addRoom(Room room);
	
	public void enterRoom(Room room);
	
	public void clearMessage();
	
	public void clearClients();
	
	public void clearRooms();
	
}
