package by.kam32ar.server.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractObject {

	protected int id;
	
	public AbstractObject(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.disableHtmlEscaping().create();
		return gson.toJson(this);
	}
	
}
