import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Acceptor extends Thread {
    private int port;

    // ArrayList 모든 연산이 스레드 안전하게 동작하는 것을 보장한다.
    private List<Session> sessions = new CopyOnWriteArrayList<>();

    Acceptor(int port) {
        this.port = port;
    }

    private void addSession(Session s) {
        sessions.add(s);
    }

    private void removeSession(Session s) {
        sessions.remove(s);
    }

    @Override
    public void run() {
        try {
            Broadcaster broadcaster = new Broadcaster(sessions);
            broadcaster.start();

            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getRemoteSocketAddress() + " connected");
                Session session = new Session(socket, broadcaster);
                session.setSessionListener(s -> {
                    System.out.println("onDisconnect");
                    removeSession(s);
                });

                addSession(session);
                session.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
