package server.db.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import protocolchat.Client;
import server.db.DataBase;

/**
 *
 * @author Daniel Sánchez S.
 * @version 2.0
 */

public class ClientDAO extends AbstractDAO<String, Client> {

    public ClientDAO() {
        super(new CRUDClient());
    }

    @Override
    public List<Client> listAll() throws SQLException, IOException {
        List<Client> listClient = new ArrayList<>();
        try (Connection cnx = DataBase.getInstance().getConnection();
                Statement stm = cnx.createStatement();
                ResultSet rs = stm.executeQuery(CRUDClient.CMD_LIST)) {
            while (rs.next()) {
                listClient.add(new Client(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getTimestamp("lastTime").toLocalDateTime()
                ));
            }
        } catch (SQLException ex) {
            System.err.printf("Exception of: '%s'%n", ex.getMessage());
        }
        return listClient;
    }

    @Override
    public Client getRecord(ResultSet rs)
            throws SQLException {
        return new Client(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("password"),
                rs.getTimestamp("lastTime").toLocalDateTime()
        );
    }

    @Override
    public void setAddParameters(PreparedStatement stm,
            String id, Client client)
            throws SQLException {
        stm.setString(1, id);
        stm.setString(2, client.getName());
        stm.setString(3, client.getPassword());
        stm.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void setUpdateParameters(PreparedStatement stm,
            String id, Client client)
            throws SQLException {
        stm.setString(1, client.getName());
        stm.setString(2, client.getPassword());
        stm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        stm.setString(4, id);
    }
}
