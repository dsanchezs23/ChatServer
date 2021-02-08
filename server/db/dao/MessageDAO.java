package server.db.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import protocolchat.Message;
import server.db.DataBase;

/**
 *
 * @author Daniel Sánchez S.
 * @version 2.0
 */
public class MessageDAO extends AbstractDAO<Integer, Message> {

    public MessageDAO() {
        super(new CRUDMessage());
    }

    public HashMap<Integer, Message> listAll(String idClient) throws SQLException, IOException {
        //the emitter and receiver are the same person, this is to retrieve the message that they sended and received
        HashMap<Integer, Message> messageMap = new HashMap<Integer, Message>();
        try (Connection cnx = DataBase.getInstance().getConnection();
                PreparedStatement stm = cnx.prepareStatement(LIST_IDMESSAGE_CMD)) {
            stm.clearParameters();
            stm.setString(1, idClient);
            stm.setString(2, idClient);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    messageMap.put(getRecord(rs).getSequence(), getRecord(rs));
                }
            }
        }
        return messageMap;
    }

    @Override
    public Message getRecord(ResultSet rs)
            throws SQLException {
        return new Message(
                rs.getInt("sequence"),
                rs.getString("emitter"),
                rs.getString("receiver"),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getString("text")
        );
    }

    @Override
    public void setAddParameters(PreparedStatement stm,
            Integer sequence, Message message)
            throws SQLException {
        stm.setInt(1, sequence);
        stm.setString(2, message.getEmitter());
        stm.setString(3, message.getReceiver());
        stm.setTimestamp(4, Timestamp.valueOf(message.getDate()));
        stm.setString(5, message.getText());
    }

    @Override
    public void setUpdateParameters(PreparedStatement stm,
            Integer sequence, Message message)
            throws SQLException {
        stm.setString(1, message.getText());
        stm.setTimestamp(2, Timestamp.valueOf(message.getDate()));
    }

    protected static final String LIST_IDMESSAGE_CMD
            = "SELECT sequence, emitter, receiver, date, text  FROM db_chat.message "
            + "WHERE emitter = ? or receiver = ?"
            + "ORDER BY sequence; ";
}
