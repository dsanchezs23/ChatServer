package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocolchat.Client;
import protocolchat.Contact;
import protocolchat.Message;
import protocolchat.Protocol;

/**
 *
 * @author Daniel Sánchez S.
 * @version 2.0
 */
public class Server {

    public Server() {
        try {
            srv = new ServerSocket(Protocol.PORT);
            workers = Collections.synchronizedList(new ArrayList<>());
        } catch (IOException ex) {
        }
    }

    public void run() throws Exception {
        localService = (ServiceServer) (ServiceServer.instance());
        localService.setServerService(this);
        boolean flag = true;
        while (flag) {
            try {
                Socket skt = srv.accept();
                ObjectInputStream in = new ObjectInputStream(skt.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(skt.getOutputStream());
                try {
                    int method = in.readInt();
                    Client client = (Client) in.readObject();
                    switch (method) {
                        case 4:
                            signingUp(client, out);
                            break;
                        case 5:
                            gettingClient(client, out);
                            break;
                        case 6:
                            updatingClient(client, out);
                            break;
                        default:
                            login(client, out, skt, in);
                            break;
                    }
                } catch (IOException | ClassNotFoundException ex) {
                }
            } catch (IOException ex) {

            }
        }
    }

    public void login(Client client, ObjectOutputStream out, Socket skt, ObjectInputStream in) throws IOException {
        try {
            client = ServiceServer.instance().login(client);
            HashMap<Integer, Message> messageList = localService.getAllMessages(client.getId());
            client.setMessages(messageList);
            localService.setContactsToClient(client);
            Service.getInstance().checkStatusContacts(client, this);
            out.writeInt(Protocol.NO_ERROR);
            out.writeObject(client);
            out.writeObject(localService.getAllId(client.getId()));
            out.flush();
            //Service.getInstance().updateStatus(client, this);
            Worker worker = new Worker(skt, in, out, client);
            workers.add(worker);
            worker.start();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            out.writeInt(Protocol.ERROR_LOGIN);
            out.flush();
        }

    }

    public void signingUp(Client client, ObjectOutputStream out) throws IOException {
        try {
            if (ServiceServer.instance().signUp(client)) {
                out.writeInt(Protocol.NO_ERROR);
                out.flush();
                //update allId
                ServiceServer.addInAllId(client.getId());
            }
        } catch (Exception ex) {
            out.writeInt(Protocol.ERROR_LOGIN);
            out.flush();
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gettingClient(Client client, ObjectOutputStream out) throws IOException, Exception {
        for (int i = 0; i < workers.size(); i++) {
            if (client.getId() == null ? workers.get(i).client.getId() == null : client.getId().equals(workers.get(i).client.getId())) {
                client = workers.get(i).client;
            }
        }

        if ("".equals(client.getName())) {
            client = ServiceServer.instance().getClient(client);
        }
        try {
            if (client != null) {
                out.writeInt(Protocol.NO_ERROR);
                out.writeObject(client);
                out.flush();
            }
        } catch (IOException ex) {
            out.writeInt(Protocol.ERROR_LOGIN);
            out.flush();
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updatingClient(Client client, ObjectOutputStream out) throws IOException {
        try {
            ServiceServer.instance().updateClient(client);
            out.writeInt(Protocol.NO_ERROR);
            out.writeObject(client);
            out.flush();
        } catch (Exception ex) {
            out.writeInt(Protocol.ERROR_LOGIN);
            out.flush();
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deliver(Message message) {
        for (int i = 0; i < workers.size(); i++) {
            if (workers.get(i).client.getId().equals(message.getEmitter()) || workers.get(i).client.getId().equals(message.getReceiver())) {
                workers.get(i).deliver(message);
            }
        }

        //this method can be better
    }

    //message methods
    public void addMessage(Message message) {
        ServiceServer.instance().post(message);
    }

    public void remove(Client client) {
        for (Worker wk : workers) {
            if (wk.client.equals(client)) {

                workers.remove(wk);
                try {
                    ServiceServer.instance().updateClient(wk.client);
                    wk.socket.close();
                } catch (IOException ex) {
                } catch (Exception ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
    }

    public boolean contactStatus(Contact contact) {
        for (Worker worker : workers) {
            if (contact.getId().equals(worker.client.getId())) {
                return true;
            }
        }
        return false;
    }

    ServiceServer localService;
    private ServerSocket srv;
    List<Worker> workers;

}
