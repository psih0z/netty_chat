package by.kam32ar.server.logic;

public class Message extends AbstractObject {

	protected String room;
	
	protected String nick;
	
	protected int time;

	protected String message;
	
	public Message() {
		super(0);
	}
	
	public Message(int id, String room, String nick, int time, String msg) {
		super(id);
		this.nick = nick;
		this.time = time;
		this.message = msg;
		this.room = room;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

}
