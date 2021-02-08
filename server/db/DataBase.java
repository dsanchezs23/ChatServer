package server.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Daniel Sánchez S. 
 * @version 2.0
 */
public class DataBase extends MysqlDataSource {

    private DataBase() throws IOException {
        this.configuration = new Properties();
        configuration.load(getClass().getResourceAsStream(CONFIGURATION_PATH));

        setURL(String.format("%s//%s/%s",
                this.configuration.getProperty("protocol"),
                this.configuration.getProperty("server_url"),
                this.configuration.getProperty("database")
        ));
        setUser(this.configuration.getProperty("user"));
        setPassword(this.configuration.getProperty("password"));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }

    public static DataBase getInstance() throws IOException {
        if (instance == null) {
            try {
                instance = new DataBase();
            } catch (IOException ex) {
                System.err.printf("Excepción de: '%s'%n", ex.getMessage());
                throw ex;
            }
        }
        return instance;
    }

    private static final String CONFIGURATION_PATH = "db.properties";
    private static DataBase instance = null;
    private Properties configuration = null;
    
}
