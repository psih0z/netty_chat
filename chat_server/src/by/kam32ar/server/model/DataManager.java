package by.kam32ar.server.model;

import java.util.List;

import by.kam32ar.server.logic.Message;


/**
 * Data manager
 */
public interface DataManager {

	public List<Message> selectMessages(String room) throws Exception;
	
	public int insertMessage(Message  message) throws Exception;

}
