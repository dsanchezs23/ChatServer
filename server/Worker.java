package server;

import protocolchat.Protocol;
import protocolchat.Client;
import protocolchat.Contact;
import protocolchat.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 *
 * @author Daniel Sánchez S.
 * @version 2.0
 */
public class Worker {

    public Worker(Socket skt, ObjectInputStream in, ObjectOutputStream out, Client client) {
        this.socket = skt;
        this.instream = in;
        this.outstream = out;
        this.client = client;
    }

    public void start() {
        try {
            System.out.println(client.getId() + " attending...");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    listen();
                }
            });
            flag = true;
            t.start();
        } catch (Exception ex) {
        }
    }

    public void stop() {
        flag = false;
    }

    public void listen() {
        int method;
        while (flag) {
            try {
                System.out.println("listening");
                method = instream.readInt();
                System.out.println("something is here");
                switch (method) {
                    //case Protocol.LOGIN: done on accept
                    case Protocol.LOGOUT:
                    try {
                        ServiceServer.instance().logout(client);
                    } catch (Exception ex) {
                    }
                    stop();
                    break;
                    case Protocol.POST:
                        try {
                            Message message = (Message) instream.readObject();
                            ServiceServer.instance().post(message);
                        } catch (ClassNotFoundException ex) {
                        }
                        break;
                }
                outstream.flush();
            } catch (IOException ex) {
                flag = false;
            }
        }
    }

    public void deliver(Message message) {
        try {
            outstream.writeInt(Protocol.DELIVER);
            outstream.writeObject(message);
            outstream.flush();
        } catch (IOException ex) {
        }
    }

    /*public void updateStatus(Client client) throws IOException {
        outstream.writeInt(Protocol.UPDATE);
        outstream.writeObject(client);
        outstream.flush();
    }*/

    public void updateContact(Contact contact) throws IOException{
        outstream.writeInt(Protocol.UPDATECONTACT);
        outstream.writeObject(contact);
        outstream.flush();
    }
        
    final Socket socket;
    private final ObjectInputStream instream;
    private final ObjectOutputStream outstream;
    final Client client;
    private boolean flag;
}
