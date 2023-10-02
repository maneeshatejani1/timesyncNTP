import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Client implements Serializable{
    private static long synchronizedTime;

    private static long calculateOffset(long t1, long t2, long t3, long t4, long currentTimeMillis){
        return currentTimeMillis + Math.round(((t4-t3) - (t2-t1)) * 0.5);
    }
    private static long calculateDelay(long t1, long t2, long t3, long t4){
        return (t4-t1) - (t3-t2);
    }
    public static void main(String[] args) throws Exception {
        String hostip = args[0];
		Socket clientSocket = new Socket(hostip, 14886);
        //System.out.println("Socket created.....");
		ObjectOutputStream outToServer = new ObjectOutputStream(
				clientSocket.getOutputStream());
        //System.out.println("Out To Server created.....");        
		outToServer.writeObject(System.currentTimeMillis());
        outToServer.flush();
        
        //System.out.println("Object written to server.....");
        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        
        long[][] timestamps = new long[][]{(long[])inFromServer.readObject(), new long[]{System.currentTimeMillis()}};
        long t1 = timestamps[0][0];
        long t2 = timestamps[0][1];
        long t3 = timestamps[0][2];
        long t4 = timestamps[1][0];

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
