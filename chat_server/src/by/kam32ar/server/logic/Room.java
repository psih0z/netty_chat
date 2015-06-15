package by.kam32ar.server.logic;

public class Room extends AbstractObject {

	protected String name;
	
	public Room(int id, String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
