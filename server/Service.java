package server;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocolchat.Client;
import protocolchat.Contact;

/**
 *
 * @author Daniel Sánchez S.
 * @version 2.0
 */
public class Service {

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    //serviceServer
    public Client getWorker(String id, Server server) {
        for (Worker wk : server.workers) {
            if (wk.client.getId().equals(id)) {
                return wk.client;
            }
        }
        return null;
    }

    //serviceServer
    public void checkContacts(String emitter, String receiver, Server server) throws Exception {
        boolean validation = false;
        Client emitterClient = getWorker(emitter, server);
        /*for (Contact ct : emitterClient.getContacts()) {
            if (ct.getId().equals(receiver)) {
                validation = true;
                break;
            }
        }*/
        if (emitterClient.getContacts().containsKey(receiver)){
            validation = true;
        }
        //is first message. They don't are contacts. They have to add them; 
        if (!validation) {
            Client receiverClient = ServiceServer.instance().getClient(receiver);
            Contact emitterContact = new Contact(emitterClient.getName(), emitterClient.getId(), emitterClient.getLastTime());
            emitterContact.setStatus(server.contactStatus(emitterContact));
            Contact receiverContact = new Contact(receiverClient.getName(), receiverClient.getId(), receiverClient.getLastTime());
            receiverContact.setStatus(server.contactStatus(receiverContact));
            receiverClient.getContacts().put(emitterContact.getId(), emitterContact);
            emitterClient.getContacts().put(receiverContact.getId(), receiverContact);
            updateContacts(emitterContact, receiverContact, server);
        }
    }

    //server
    public void checkStatusContacts(Client client, Server server) {
        for (Worker worker : server.workers){
            if(worker.client.getContacts().containsKey(client.getId())){
                Contact contact = worker.client.getContacts().get(client.getId());
                contact.setStatus(true);
                worker.client.getContacts().replace(client.getId(), contact);
            }
        }
    
    }

    //server
   /*public void updateStatus(Client client, Server server) {
        for (Worker wk : server.workers) {
            if (!wk.client.getId().equals(client.getId())) {
                try {
                    wk.updateStatus(client);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }*/

    //server
    public void updateContacts(Contact emitterContact, Contact receiverContact, Server server) {
        for (Worker wk : server.workers) {
            if (wk.client.getId().equals(emitterContact.getId())) {
                try {
                    receiverContact.setStatus(true);
                    wk.updateContact(receiverContact);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (wk.client.getId().equals(receiverContact.getId())) {
                try {
                    emitterContact.setStatus(true);
                    wk.updateContact(emitterContact);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static Service instance = null;
}
