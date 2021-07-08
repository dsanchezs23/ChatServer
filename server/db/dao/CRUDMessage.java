package server.db.dao;

import server.db.AbstractCRUD;

/**
 *
 * @author Daniel Sánchez S.
 * @version 2.0
 */
public class CRUDMessage extends AbstractCRUD {

    @Override
    public String getListAllCmd() {
        return CMD_LIST;
    }

    @Override
    public String getAddCmd() {
        return CMD_ADD;
    }

    @Override
    public String getRetrieveCmd() {
        return CMD_RETRIEVE;
    }

    @Override
    public String getUpdateCmd() {
        return CMD_UPDATE;
    }

    @Override
    public String getDeleteCmd() {
        return CMD_DELETE;
    }

    protected static final String CMD_LIST
            = "SELECT sequence, emitter, receiver, date, text FROM message "
            + "ORDER BY sequence; ";
    protected static final String CMD_ADD
            = "INSERT INTO message (sequence, emitter, receiver, date, text) "
            + "VALUES (?, ?, ?, ?, ?); ";
    protected static final String CMD_RETRIEVE
            = "SELECT sequence, emitter, receiver, date, text FROM message "
            + "WHERE sequence = ?; ";
    protected static final String CMD_UPDATE
            = "UPDATE message SET text = ?, date = ? "
            + "WHERE sequence = ?; ";
    protected static final String CMD_DELETE
            = "DELETE FROM message "
            + "WHERE sequence = ?; ";
}
