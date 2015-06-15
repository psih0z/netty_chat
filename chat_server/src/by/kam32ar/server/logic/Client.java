package by.kam32ar.server.logic;

public class Client extends AbstractObject {

	protected String nick;
	
	protected String room;
	
	public Client(int id, String nick, String room) {
		super(id);
		this.nick = nick;
		this.room = room;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}
	
}
