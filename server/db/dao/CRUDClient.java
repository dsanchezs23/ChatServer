package server.db.dao;

import server.db.AbstractCRUD;

/**
 *
 * @author Daniel Sánchez S. 
 * @version 2.0
 */
public class CRUDClient extends AbstractCRUD {

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
            = "SELECT id, name, password, lastTime FROM user "
            + "ORDER BY id, name; ";
    protected static final String CMD_ADD
            = "INSERT INTO user (id, name, password, lastTime) "
            + "VALUES (?, ?, ?, ?); ";
    protected static final String CMD_RETRIEVE
            = "SELECT id, name, password, lastTime FROM user "
            + "WHERE id = ?; ";
    protected static final String CMD_UPDATE
            = "UPDATE user SET name = ?, password = ?, lastTime = ? "
            + "WHERE id = ?; ";
    protected static final String CMD_DELETE
            = "DELETE FROM user "
            + "WHERE id = ?; ";
}
