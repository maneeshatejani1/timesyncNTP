import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Client implements Serializable{
    private static long synchronizedTime;
    public static class Payload{
        private Instant t1; 
        private Instant t2; 
        private Instant t3; 
        private Instant t4; 
        public Payload(Payload payload, Instant t4){
            this.t1 = payload.t1;
            this.t2 = payload.t2;
            this.t3 = payload.t3;
            this.t4 = t4;
        }
        public Instant getT1(){
            return this.t1;
        }
        public Instant getT2(){
            return this.t2;
        }
        public Instant getT3(){
            return this.t3;
        }
        public Instant getT4(){
            return this.t4;
        }
    }

    private static long calculateOffset(Payload finalResponse, long currentTimeMillis){
        return currentTimeMillis + Math.round((ChronoUnit.MILLIS.between(finalResponse.t3, finalResponse.t4) 
                            - ChronoUnit.MILLIS.between(finalResponse.t1, finalResponse.t2))
                            *0.5);
    }
    private static long calculateDelay(Payload finalResponse){
        return ChronoUnit.MILLIS.between(finalResponse.t1, finalResponse.t4) 
                            - ChronoUnit.MILLIS.between(finalResponse.t2, finalResponse.t3);
    }
    public static void main(String[] args) throws Exception {
		Socket clientSocket = new Socket("localhost", 6789);
        System.out.println("Socket created.....");
		ObjectOutputStream outToServer = new ObjectOutputStream(
				clientSocket.getOutputStream());
        System.out.println("Out To Server created.....");

        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());

		outToServer.writeObject(Instant.now());
        outToServer.flush();
        //outToServer.close();
        System.out.println("Object written to server.....");
        Payload x = (Payload) inFromServer.readObject();
		Payload finalResponse = new Payload(x, Instant.now());
        System.out.println("response obtained from server......");

        //inFromServer.close();
        long currentTimeMillis = System.currentTimeMillis();
        synchronizedTime = calculateOffset(finalResponse, currentTimeMillis);
        System.out.println("REMOTE_TIME "+synchronizedTime);
        System.out.println("LOCAL_TIME "+currentTimeMillis);
        System.out.println("RTT_ESTIMATE "+calculateDelay(finalResponse));
        clientSocket.close();
    }
}
