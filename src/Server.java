import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Serializable {
    public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
        try{
            while(true){
                Socket connectionSocket = welcomeSocket.accept();
                //System.out.println("Connection established");
        
                ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
                //System.out.println("In from Client created......");
        
                ObjectOutputStream outToClient = new ObjectOutputStream(
                            connectionSocket.getOutputStream());
               // System.out.println("Out To Client created.....");
        
                long[] timestamps = new long[]{(long)inFromClient.readObject(), System.currentTimeMillis()};
                //Instant t2 = Instant.now();
        
                //Instant timestamps[] = new Instant[2];
                //timestamps[0] = t1;
                //timestamps[1] = Instant.now();
        
                outToClient.writeObject(new long[]{timestamps[0],timestamps[1], System.currentTimeMillis()}); 
                outToClient.flush();
        
                outToClient.close();
                inFromClient.close();
                connectionSocket.close();
            }
            
        }
        finally{
            welcomeSocket.close();
        }
            
    }
}
