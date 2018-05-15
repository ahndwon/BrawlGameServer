import Models.*;

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

                                int[] m = map.getMap();

                                s.getUser().setSpeed(PLAYER_SPEED);
                                session.getUser().setSpeed(PLAYER_SPEED);
                                updates.getUpdates().get(s.getUser().getName()).setSpeed(PLAYER_SPEED);

                                for (int i = 0; i < m.length; i++) {
//                                        Update update = updates.getUpdates().get(name);
//                System.out.println("index " + Util.getIndexByPos((int) update.getX(), (int) update.getY()));
//                                    s.getUser().setSpeed(PLAYER_SPEED);
//                                    session.getUser().setSpeed(PLAYER_SPEED);
//                                    updates.getUpdates().get(s.getUser().getName()).setSpeed(PLAYER_SPEED);

                                    if (Util.getIndexByPos((int) s.getUser().getX(), (int) s.getUser().getY()) == i) {
//                    System.out.println("same, m[i] : " + m[i]);
                                        switch (m[i]) {
                                            case 1:
                                                s.getUser().setSpeed(PLAYER_SPEEDSLOW);
                                                session.getUser().setSpeed(PLAYER_SPEEDSLOW);
                                                updates.getUpdates().get(s.getUser().getName()).setSpeed(PLAYER_SPEEDSLOW);
                                                break;

                                            case 2:
                                                if (s.getUser().getHp() >= FULL_HP - HEAL) {
                                                    s.getUser().setHp(100);
                                                    session.getUser().setHp(100);
                                                    updates.getUpdates().get(s.getUser().getName()).setHp(100);
                                                } else {
                                                    s.getUser().setHp(s.getUser().getHp() + HEAL);
                                                    session.getUser().setHp(s.getUser().getHp() + HEAL);
                                                    updates.getUpdates().get(s.getUser().getName()).setHp(s.getUser().getHp() + HEAL);
                                                }
                                                break;
                                        }
                                    }
                                }
                                moveUsers(direction, s);
                            }
                        }
                    }

                    @Override
                    public void onAttack(Session session) {
                        System.out.println("onAttack");
                        User user = session.getUser();
                        user.setState("ATTACK");
                        updates.getUpdates().get(session.getUser().getName()).setState("ATTACK");
                        Vector2D userPos = new Vector2D(user.getX(), user.getY());
                        Boolean isAttacked = false;
                        List<Session> others = new ArrayList<>(sessions);
                        others.remove(session);

                        for (Session s : others) {
                            User other = s.getUser();
                            Vector2D otherPos = new Vector2D(other.getX(), other.getY());

                            float diffX = userPos.x - otherPos.x;
                            float diffY = userPos.y - otherPos.y;

                            if (user.getDirection().equals(PLAYER_DOWN)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffY < 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_UP)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffY > 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_RIGHT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffX < 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_LEFT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2
                                    && diffX > 0) {
                                isAttacked = true;
                            }

                            if (isAttacked) {
                                other.damaged();
                                user.attack();
                                updates.getUpdates().get(other.getName()).setHp(other.getHp() - Constants.DAMAGE);
                                updates.getUpdates().get(user.getName()).setScore(user.getScore() + Constants.HIT_SCORE);
                                System.out.println("hit");
                                broadcaster.sendHit(user.getName(), other.getName());
                                isAttacked = false;
                            }

                            if (other.getHp() <= DAMAGE) {
                                System.out.println("Kill");

                                user.killed();
                                broadcaster.sendKill(user.getName(), other.getName());
                                updates.getUpdates().get(other.getName()).killed();
                                respawn(other, s);
                            }
                        }
                    }

                    @Override
                    public void onStop(Session session) {
                        session.getUser().setState("STOP");
                        updates.getUpdates().get(session.getUser().getName()).setState("STOP");
                    }
                });
                addSession(session);
                session.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respawn(User user, Session session) {
        user = new User((float) (Math.random() * 600), (float) (Math.random() * 600), user.getName(),
                Constants.PLAYER_DOWN, 100, USER_STOP);
        Update update = new Update(user.getName(), user.getX(), user.getY(),
                user.getHp(), user.getDirection(), user.getScore(), user.getState());
        updates.getUpdates().replace(user.getName(), update);
        session.setUser(user);
    }

    private void moveUsers(String direction, Session s) {
        switch (direction) {
            case "UP":
                s.getUser().setY(s.getUser().getY() - s.getUser().getSpeed());
                break;
            case "LEFT":
                s.getUser().setX(s.getUser().getX() - s.getUser().getSpeed());
                break;
            case "RIGHT":
                s.getUser().setX(s.getUser().getX() + s.getUser().getSpeed());
                break;
            case "DOWN":
                s.getUser().setY(s.getUser().getY() + s.getUser().getSpeed());
                break;
        }

        updates.getUpdates().get(s.getUser().getName()).setDirection(direction);
        Update update = updates.getUpdates().get(s.getUser().getName());
        update.setState(USER_MOVE);
        update.setDirection(direction);

        switch (direction) {
            case "UP":
                update.setY(update.getY() - update.getSpeed());
                break;
            case "LEFT":
                update.setX(update.getX() - update.getSpeed());
                break;
            case "RIGHT":
                update.setX(update.getX() + update.getSpeed());
                break;
            case "DOWN":
                update.setY(update.getY() + update.getSpeed());
                break;
        }
    }
}
