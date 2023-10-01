import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Server {
    public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
        Socket connectionSocket = welcomeSocket.accept();
        System.out.println("Connection established");

        ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
        System.out.println("In from Client created......");

        ObjectOutputStream outToClient = new ObjectOutputStream(
                    connectionSocket.getOutputStream());
        System.out.println("Out To Client created.....");

        Instant t1 = (Instant)inFromClient.readObject();
        System.out.println(t1 + "t1 received");
        Instant t2 = Instant.now();

        Instant timestamps[] = new Instant[2];
        timestamps[0] = t1;
        timestamps[1] = Instant.now();

        outToClient.writeObject(timestamps); 
        outToClient.flush();

        outToClient.close();
        inFromClient.close();
        connectionSocket.close();
        welcomeSocket.close();
            
    }
}
