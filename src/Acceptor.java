import Models.*;
import Models.Map;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Acceptor extends Thread implements Constants {
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
                Session session = new Session(socket);
                session.sendMap(map);
                session.setSessionListener(new SessionListener() {
                    @Override
                    public void onJoin(User user) {
                        Update update = new Update(user.getName(), user.getX(), user.getY(),
                                user.getHp(), user.getDirection(), user.getScore(), user.getState());
                        updates.getUpdates().putIfAbsent(update.getUser(), update);
                        System.out.println("onJoin " + user.getName());
                    }

                    @Override
                    public void onDisconnect(Session session) {
                        updates.getUpdates().remove(session.getName());
                        sessions.remove(session);
                        broadcaster.update(sessions);
                    }

                    @Override
                    public void onMove(Session session, Move move) {
                        for (Session s : sessions) {
                            if (session.getUser().equals(s.getUser())) {
                                s.getUser().setX(session.getUser().getX());
                                s.getUser().setY(session.getUser().getY());
                                s.getUser().setState(Constants.USER_MOVE);
                                s.getUser().setDirection(move.getDirection());
                                String direction = move.getDirection();

                                switch (direction) {
                                    case "UP":
                                        System.out.println("up");
                                        s.getUser().setY(session.getUser().getY() - PLAYER_SPEED);
                                        break;
                                    case "LEFT":
                                        System.out.println("left");
                                        s.getUser().setX(session.getUser().getX() - PLAYER_SPEED);
                                        break;
                                    case "RIGHT":
                                        System.out.println("right");
                                        s.getUser().setX(session.getUser().getX() + PLAYER_SPEED);
                                        break;
                                    case "DOWN":
                                        System.out.println("down");
                                        s.getUser().setY(session.getUser().getY() + PLAYER_SPEED);
                                        break;
                                }

                                s.getUser().setState(USER_STOP);
                            }
                        }

                        updates.getUpdates().get(session.getUser().getName()).setDirection(move.getDirection());
                        String direction = move.getDirection();
                        Update update = updates.getUpdates().get(session.getUser().getName());
                        update.setState(USER_MOVE);
                        update.setDirection(direction);

                        switch (direction) {
                            case "UP":
                                System.out.println("up2");
                                update.setY(update.getY() - PLAYER_SPEED);
                                break;
                            case "LEFT":
                                System.out.println("left2");
                                update.setX(update.getX() - PLAYER_SPEED);
                                break;
                            case "RIGHT":
                                System.out.println("right2");
                                update.setX(update.getX() + PLAYER_SPEED);
                                break;
                            case "DOWN":
                                System.out.println("down2");
                                update.setY(update.getY() + PLAYER_SPEED);
                                break;
                        }
                        update.setState(USER_STOP);
                    }

                    @Override
                    public void onAttack(Session session) {
                        System.out.println("onAttack");
                        User user = session.getUser();
                        Vector2D userPos = new Vector2D(user.getX(), user.getY());
                        Boolean isAttacked = false;
                        List<Session> others = new ArrayList<>(sessions);
                        others.remove(session);

                        for (Session s : others) {
                            User other = s.getUser();
                            Vector2D otherPos = new Vector2D(other.getX(), other.getY());
                            System.out.println("user : " + user.getX() + ", " + user.getY());
                            System.out.println("other : " + other.getX() + ", " + other.getY());
                            float diffX = userPos.x - otherPos.x;
                            float diffY = userPos.y - otherPos.y;
                            if (user.getDirection().equals(PLAYER_DOWN)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffY < 0) {
                                other.damaged();
                                user.attack();
                                isAttacked = true;
                                System.out.println("hit");
                            } else if (user.getDirection().equals(PLAYER_UP)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffY > 0) {
                                other.damaged();
                                user.attack();
                                isAttacked = true;
                                System.out.println("hit");
                            } else if (user.getDirection().equals(PLAYER_RIGHT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffX < 0) {
                                other.damaged();
                                user.attack();
                                isAttacked = true;
                                System.out.println("hit");
                            } else if (user.getDirection().equals(PLAYER_LEFT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffX > 0) {
                                other.damaged();
                                user.attack();
                                isAttacked = true;
                                System.out.println("hit");
                            }

                            if (other.getHp() < 1) {
                                broadcaster.sendKill(user.getName(), other.getName());
                                updates.getUpdates().remove(other.getName());
                                sessions.remove(s);
                            }
                        }
                    }
                });
                addSession(session);
                session.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
