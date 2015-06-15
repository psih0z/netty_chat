package by.kam32ar.server.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdvancedConnection {

    /**
     * Database connection
     */
    private Connection connection;
    
    /**
     * Connection attributes
     */
    private String url;
    private String user;
    private String password;
    
    public AdvancedConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    public final void reset() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
        if (user != null && password != null) {
            connection = DriverManager.getConnection(url, user, password);
        } else {
            connection = DriverManager.getConnection(url);
        }
    }
    
    public Connection getInstance() throws SQLException {
        if (connection == null) {
            reset();
        }
        return connection;
    }
    
    public CallableStatement prepareCall(String query) throws SQLException {
    	return connection.prepareCall(query);
    }

}
