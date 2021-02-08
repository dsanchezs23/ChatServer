package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocolchat.Client;
import protocolchat.Contact;
import protocolchat.IServiceProtocol;
import protocolchat.Message;
import server.db.dao.ClientDAO;
import server.db.dao.MessageDAO;

/**
 *
 * @author Daniel Daniel Sánchez S.
 * @version 2.0
 */
public class ServiceServer implements IServiceProtocol {

    public static IServiceProtocol instance() {
        if (theInstance == null) {
            theInstance = new ServiceServer();
        }
        return theInstance;
    }

    public ServiceServer() {
        clients = new ClientDAO();
        messages = new MessageDAO();
    }

    public void setServerService(Server server) {
        this.server = server;
    }

    @Override
    public void post(Message message) {
        try {
            messages.add(messages.listAll().size() + 1, message);
            Service.getInstance().checkContacts(message.getEmitter(), message.getReceiver(), server);
            server.deliver(message);
        } catch (SQLException | IOException ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServiceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Client login(Client client) throws Exception {
        Client result = clients.retrieve(client.getId());
        if (result == null) {
            throw new Exception("This client doesn't exist");
        }
        if (!result.getPassword().equals(client.getPassword())) {
            throw new Exception("Wrong password");
        } else {
        }
        return result;
    }

    @Override
    public boolean signUp(Client client) throws Exception {
        Client result = clients.retrieve(client.getId());
        if (result == null) {
            clients.add(client.getId(), client);
            return true;
        }
        return false;
    }

    @Override
    public void logout(Client client) throws Exception {
        //Service.getInstance().updateStatus(client, server);
        server.remove(client);
    }

    @Override
    public Client getClient(Client client) throws Exception {
        Client result = clients.retrieve(client.getId());
        return result;
    }

    @Override
    public Client getClient(String id) throws Exception {
        return getClient(new Client(id));
    }

    @Override
    public void updateClient(Client client) throws Exception {
        clients.update(client.getId(), client);
    }

    public List<String> getAllId() throws SQLException, IOException {
        allId = new ArrayList<>();
        allClients = clients.listAll();
        for (int i = 0; i < allClients.size(); i++) {
            allId.add(allClients.get(i).getId());
        }
        return allId;
    }

    public List<String> getAllId(String id) throws SQLException, IOException {
        allId = new ArrayList<>();
        allClients = clients.listAll();
        for (int i = 0; i < allClients.size(); i++) {
            if (!allClients.get(i).getId().equals(id)) {
                allId.add(allClients.get(i).getId());
            }
        }
        return allId;
    }

    public HashMap<Integer, Message> getAllMessages(String idClient) throws SQLException, IOException {
        HashMap<Integer, Message> messagesMap = messages.listAll(idClient);
        return messagesMap;
    }

    public int getSeqMessgaes() throws SQLException, IOException {
        return messages.listAll().size() + 1;
    }

    public void setContactsToClient(Client client) {
        Client clientcontact = new Client();
        HashMap<String, Contact> contactsMap = new HashMap<String, Contact>();
        //getting all the messages from the client
        getMessagesFromClient(clientcontact, contactsMap, client);
        //setting contacts
        client.setContacts(contactsMap);
    }

    private void getMessagesFromClient(Client clientcontact, HashMap<String, Contact> contactsMap, Client client) {        
        for (Message messageValue : client.getMessages().values()){          
            if (!client.getId().equals(messageValue.getReceiver())) {
                try {
                    clientcontact.setId(messageValue.getReceiver());
                    clientcontact = getClient(clientcontact);
                    Contact contact = new Contact(clientcontact.getName(), clientcontact.getId(), clientcontact.getLastTime());
                    contact.setStatus(server.contactStatus(contact));
                    if (!contactsMap.containsKey(contact.getId())) {
                        contactsMap.put(contact.getId(), contact);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    clientcontact.setId(messageValue.getEmitter());
                    clientcontact = getClient(clientcontact);
                    Contact contact = new Contact(clientcontact.getName(), clientcontact.getId(), clientcontact.getLastTime());
                    contact.setStatus(server.contactStatus(contact));
                    if (!contactsMap.containsKey(contact.getId())) {
                        contactsMap.put(contact.getId(), contact);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void addInAllId(String id) {
        allId.add(id);
    }

    private static IServiceProtocol theInstance;
    private Server server;
    private final ClientDAO clients;
    private final MessageDAO messages;
    private static List<String> allId;
    private List<Client> allClients;

}
