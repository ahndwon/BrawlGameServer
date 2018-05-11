import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Broadcaster extends Thread {
    private List<Session> sessions;

    Broadcaster(List<Session> sessions) {
        this.sessions = sessions;
    }

    void sendPosition(String message) {
        broadcast(message);
    }

    private void broadcast(String message) {
        for (Session session : sessions) {
            try {
                OutputStream os = session.getOutputStream();
                byte[] bytes = message.getBytes();
                ByteBuffer buffer = ByteBuffer.allocate(2);
                buffer.putShort((short) bytes.length);

                System.out.println("buffer.array : " + Arrays.toString(buffer.array()));
                System.out.println("message : " + message);
                os.write(buffer.array());
                os.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                // TODO...
//                int index = random.nextInt(words.size());


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
