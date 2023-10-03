import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class TSServer implements Serializable {
    public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(14886); // Opening server socket on port 14886
        System.out.println("Server started. Accepting Connections");
        try{
            while(true){
                new ClientHandler(welcomeSocket.accept()).start(); // Accepting incoming client connection, parallely
                System.out.println("Client Connected");
            }
            
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally{
            welcomeSocket.close();
        }
            
    }

    private static class ClientHandler extends Thread { // Multi-threaded implementation
        private Socket connectionSocket;
        private ObjectInputStream inFromClient;
        private ObjectOutputStream outToClient; 
        private long[] timestamps;

        public ClientHandler(Socket socket) {
            this.connectionSocket = socket;
            try{
                inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
                outToClient = new ObjectOutputStream(
                            connectionSocket.getOutputStream());
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try{
                //Capturing T1 from client and recording T2 at server
                timestamps = new long[]{(long)inFromClient.readObject(), System.currentTimeMillis()};

                //Sending T1, T2, T3 after recording T3 at server
                outToClient.writeObject(new long[]{timestamps[0],timestamps[1], System.currentTimeMillis()});
                outToClient.flush();
                outToClient.close();
                inFromClient.close();
                connectionSocket.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            catch(ClassNotFoundException e2) {
                e2.printStackTrace();
            }

        }
    }
}    

