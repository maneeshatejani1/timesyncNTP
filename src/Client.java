import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Client implements Serializable{
    private static long synchronizedTime;

    private static long calculateOffset(Instant t1, Instant t2, Instant t3, Instant t4, long currentTimeMillis){
        return currentTimeMillis + Math.round((ChronoUnit.MILLIS.between(t3, t4) 
                            - ChronoUnit.MILLIS.between(t1, t2))
                            *0.5);
    }
    private static long calculateDelay(Instant t1, Instant t2, Instant t3, Instant t4){
        return ChronoUnit.MILLIS.between(t1, t4) 
                            - ChronoUnit.MILLIS.between(t2, t3);
    }
    public static void main(String[] args) throws Exception {
		Socket clientSocket = new Socket("localhost", 6789);
        System.out.println("Socket created.....");
		ObjectOutputStream outToServer = new ObjectOutputStream(
				clientSocket.getOutputStream());
        System.out.println("Out To Server created.....");        
        Instant t1 = Instant.now();
		outToServer.writeObject(t1);
        outToServer.flush();
        
        System.out.println("Object written to server.....");
        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        
        Instant timestamps[] = (Instant[])inFromServer.readObject();
        Instant t2 = timestamps[0];
        Instant t3 = timestamps[1];
        Instant t4 = Instant.now();

        outToServer.close();
        inFromServer.close();
        long currentTimeMillis = System.currentTimeMillis();
        synchronizedTime = calculateOffset(t1, t2, t3, t4, currentTimeMillis);
        System.out.println("REMOTE_TIME "+ synchronizedTime);
        System.out.println("LOCAL_TIME "+ currentTimeMillis);
        System.out.println("RTT_ESTIMATE "+ calculateDelay(t1, t2, t3, t4));
        clientSocket.close();
    }
}
