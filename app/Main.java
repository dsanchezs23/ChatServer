package app;

import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

/**
 *
 * @author Daniel Sánchez S. 
 * @version 2.0
 */
public class Main {
        public static void main(String[] args) {
        Server server = new Server();
            try {
                server.run();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
