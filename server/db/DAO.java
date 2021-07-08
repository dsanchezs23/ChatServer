package server.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Daniel Sánchez S. 
 * @version 2.0
 * @param <K>
 * @param <V>
 */
public interface DAO<K, V> {

    public List<V> listAll() throws SQLException, IOException;

    public void add(K id, V value) throws SQLException, IOException;

    public V retrieve(K id) throws SQLException, IOException;

    public void update(K id, V value) throws SQLException, IOException;

    public void delete(K id) throws SQLException, IOException;
}
