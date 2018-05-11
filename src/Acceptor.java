import Models.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Acceptor extends Thread {
    private int port;
    private Map map;

    // ArrayList 모든 연산이 스레드 안전하게 동작하는 것을 보장한다.
    private List<Session> sessions;
    private Updates updates;

    Acceptor(int port) {
        this.port = port;
        map = new Map();
        sessions = new CopyOnWriteArrayList<>();
        updates = new Updates();
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
            Broadcaster broadcaster = new Broadcaster(sessions, updates);
            broadcaster.start();

            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getRemoteSocketAddress() + " connected");
                Session session = new Session(socket, broadcaster);
                session.sendMap(map);
                session.setSessionListener(new SessionListener() {
                    @Override
                    public void onJoin(Join join) {
                        Update update = new Update();
                        update.setUser(join.getUser());
                        updates.getUpdates().putIfAbsent(update.getUser(), update);
                        System.out.println("onJoin " + join.getUser());
                    }

                    @Override
                    public void onDisconnect(Session session) {
                        sessions.remove(session);
                    }

                    @Override
                    public void onMove(Session session, Move move) {
//                        System.out.println(move.getDirection());
//                        if (updates.getUpdates().get(move.getUser()) != null)
                        updates.getUpdates().get(session.getUser().getName()).setDirection(move.getDirection());
                        String direction = move.getDirection();
                        Update update = updates.getUpdates().get(session.getUser().getName());
                        float speed = 3;
                        switch (direction) {
                            case "UP":
                                update.setY(update.getY() - speed);
                                break;
                            case "LEFT":
                                update.setX(update.getX() - speed);
                                break;
                            case "RIGHT":
                                update.setX(update.getX() + speed);
                                break;
                            case "DOWN":
                                update.setY(update.getY() + speed);
                                break;
                        }
//                        System.out.println(move);
                    }

//                    @Override
//                    public void onAttack(Session session) {
//
//                    }
                });
                addSession(session);
                session.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
