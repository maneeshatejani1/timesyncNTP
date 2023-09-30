import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Server {
    public static class Payload implements Serializable{
        private Instant t1; 
        private Instant t2; 
        private Instant t3; 
        private Instant t4; 

        public Payload(Instant t1, Instant t2){
            this.t1 = t1;
        }

        public Payload(Payload payload, Instant t3){
            this.t1 = payload.t1;
            this.t2 = payload.t2;
            this.t3 = t3;
        }
    }
    public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
        Socket connectionSocket = welcomeSocket.accept();
        System.out.println("Connection established");
        ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
        System.out.println("In from Client created......");
        Instant x = (Instant)inFromClient.readObject();
        Payload request = new Payload(x, Instant.now());
        System.out.println("Request obtained from client......");
        ///inFromClient.close();
        ObjectOutputStream outToClient = new ObjectOutputStream(
                    connectionSocket.getOutputStream());
        outToClient.writeObject(new Payload(request, Instant.now())); 
        outToClient.flush();
       // outToClient.close();
       connectionSocket.close();
       welcomeSocket.close();
            
    }
}
