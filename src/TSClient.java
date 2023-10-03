import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class TSClient implements Serializable{
    private static long synchronizedTime; //Separate variable maintaining time synchronized with server

    private static long calculateOffset(long t1, long t2, long t3, long t4){
        return Math.round(((t4-t3) - (t2-t1)) * 0.5);
    }
    private static long calculateDelay(long t1, long t2, long t3, long t4){
        return (t4-t1) - (t3-t2);
    }
    public static void main(String[] args) throws Exception {
        String hostIp;
        if(args.length == 0) {
            hostIp = "localhost";
        }
        else {
            hostIp = args[0];
        }
		
        long curr_offset, curr_delay, min_delay = Integer.MAX_VALUE;
        long[][] timestamps;
        long t1,t2,t3,t4,currentTimeMillis = System.currentTimeMillis();
        for(int i=0;i<8;i++){
            Socket clientSocket = new Socket(hostIp, 14886);
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
            outToServer.writeObject(System.currentTimeMillis());
            outToServer.flush();
            timestamps = new long[][]{(long[])inFromServer.readObject(), new long[]{System.currentTimeMillis()}};
            t1 = timestamps[0][0];
            t2 = timestamps[0][1];
            t3 = timestamps[0][2];
            t4 = timestamps[1][0];

            curr_offset = calculateOffset(t1, t2, t3, t4);
            curr_delay = calculateDelay(t1, t2, t3, t4);
            if(curr_delay < min_delay){
                min_delay = curr_delay;
                currentTimeMillis = t4;
                synchronizedTime = t3 + curr_offset;
            }
            outToServer.close();
            inFromServer.close();
            clientSocket.close();
        }
        
        System.out.println("REMOTE_TIME "+ synchronizedTime);
        System.out.println("LOCAL_TIME "+ currentTimeMillis);
        System.out.println("RTT_ESTIMATE "+ min_delay);
    }
}
