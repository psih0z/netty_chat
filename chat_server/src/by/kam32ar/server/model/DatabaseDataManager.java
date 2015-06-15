package by.kam32ar.server.model;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import by.kam32ar.server.helper.AdvancedConnection;
import by.kam32ar.server.helper.DriverDelegate;
import by.kam32ar.server.helper.NamedParameterStatement;
import by.kam32ar.server.logic.Message;

import com.mysql.jdbc.Driver;
import com.mysql.jdbc.Statement;

/**
 * Database abstraction class
 */
public class DatabaseDataManager implements DataManager {

	public DatabaseDataManager(Properties properties) throws Exception {
		initDatabase(properties);
	}

	private NamedParameterStatement queryInsertMessage;
	private NamedParameterStatement querySelectMessage;

	private void initDatabase(Properties properties) throws Exception {

		// Load driver
		String driver = properties.getProperty("database.driver");
		if (driver != null) {
			String driverFile = properties.getProperty("database.driverFile");

			if (driverFile != null) {
				URL url = new URL("jar:file:"
						+ new File(driverFile).getAbsolutePath() + "!/");
				URLClassLoader cl = new URLClassLoader(new URL[] { url });
				Driver d = (Driver) Class.forName(driver, true, cl)
						.newInstance();
				DriverManager.registerDriver(new DriverDelegate(d));
			} else {
				Class.forName(driver);
			}
		}

		// Connect database
		String url = properties.getProperty("database.url");
		String user = properties.getProperty("database.user");
		String password = properties.getProperty("database.password");
		AdvancedConnection connection = new AdvancedConnection(url, user,
				password);

		// Load statements from configuration
		String query;

		query = properties.getProperty("database.insertMessage");
		if (query != null) {
			queryInsertMessage = new NamedParameterStatement(connection, query);
		}

		query = properties.getProperty("database.selectMessages");
		if (query != null) {
			querySelectMessage = new NamedParameterStatement(connection, query);
		}
	}

	@Override
	public synchronized int insertMessage(Message message) throws Exception {
		if (queryInsertMessage != null) {
			queryInsertMessage.prepare(Statement.RETURN_GENERATED_KEYS);
			queryInsertMessage = assignBattleVariables(queryInsertMessage, message);
			queryInsertMessage.executeUpdate();
			
			ResultSet result = queryInsertMessage.getGeneratedKeys();
			if (result != null && result.next()) {
				return result.getInt(1);
			}
		}
		
		return 0;
	}

	@Override
	public List<Message> selectMessages(String room) throws Exception {
		List<Message> messages = new ArrayList<Message>();

		querySelectMessage.prepare();
		querySelectMessage.setString("room", room);
		ResultSet resultSet = querySelectMessage.executeQuery();
		
		while (resultSet.next()) {
			Message message = new Message();
			
			message.setId(resultSet.getInt("id"));
			message.setRoom(resultSet.getString("room"));
			message.setTime(resultSet.getInt("time"));
			message.setMessage(resultSet.getString("msg"));
			message.setNick(resultSet.getString("name"));
			
			messages.add(message);
		}

		return messages;
	}

	private NamedParameterStatement assignBattleVariables(
			NamedParameterStatement statement, Message message)
			throws SQLException {
		statement.setInt("time", message.getTime());
		statement.setString("name", message.getNick());
		statement.setString("msg", message.getMessage());
		statement.setString("room", message.getRoom());

		return statement;
	}

}
