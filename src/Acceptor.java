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
            Broadcaster broadcaster = new Broadcaster(sessions, updates, map);
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
                        if (updates.getUpdates().containsKey(user.getName())) {
                            session.send(broadcaster.sendReject());
                            sessions.remove(session);

                        } else {
                            Update update = new Update(user.getName(), user.getX(), user.getY(),
                                    user.getHp(), user.getMana(), user.getStamina(),
                                    user.getDirection(), user.getScore(), user.getState());
                            updates.getUpdates().put(update.getUser(), update);
                        }
                        System.out.println("onJoin " + user.getName());
                    }

                    @Override
                    public void onDisconnect(Session session) {
                        updates.getUpdates().remove(session.getUser().getName());
                        removeSession(session);
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

                                s.getUser().setSpeed(PLAYER_SPEED);
                                session.getUser().setSpeed(PLAYER_SPEED);
                                updates.getUpdates().get(s.getUser().getName()).setSpeed(PLAYER_SPEED);
                                updates.getUpdates().get(s.getUser().getName()).setDirection(direction);
                                Update update = updates.getUpdates().get(s.getUser().getName());
                                update.setState(USER_MOVE);
                                update.setDirection(direction);
//                                moveUsers(direction, s);
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
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 1.5f
                                    && diffY < 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_UP)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 1.5f
                                    && diffY > 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_RIGHT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 1.5f
                                    && diffX < 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_LEFT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 1.5f
                                    && diffX > 0) {
                                isAttacked = true;
                            }


                            if (isAttacked) {
                                updates.getUpdates().get(other.getName()).setHp(other.getHp() - Constants.DAMAGE);
                                updates.getUpdates().get(user.getName()).setScore(user.getScore() + Constants.HIT_SCORE);
                                other.hit();
                                user.attack();
                                System.out.println("hit");
                                broadcaster.sendHit(user.getName(), other.getName(), DAMAGE);
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
                    public void onSpecial(Session session) {
                        if (session.getUser().getMana() < 30) {
                            return;
                        }
                        System.out.println("onSpecial");
                        User user = session.getUser();
                        user.setState("SPECIAL");
                        user.setMana(user.getMana() - SPECIAL_MANA);
                        updates.getUpdates().get(session.getUser().getName()).setState("SPECIAL");
                        updates.getUpdates().get(session.getUser().getName()).setMana(session.getUser().getMana());
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
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2.5f
                                    && diffY < 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_UP)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2.5f
                                    && diffY > 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_RIGHT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2.5f
                                    && diffX < 0) {
                                isAttacked = true;
                            } else if (user.getDirection().equals(PLAYER_LEFT)
                                    && Vector2D.getDistance(userPos, otherPos) < BLOCK_SIZE * 2.5f
                                    && diffX > 0) {
                                isAttacked = true;
                            }

                            if (isAttacked) {
                                other.specialHit();
                                user.attack();
                                updates.getUpdates().get(other.getName()).setHp(other.getHp() - Constants.SPECIAL_DAMAGE);
                                updates.getUpdates().get(user.getName()).setScore(user.getScore() + Constants.HIT_SCORE);
                                System.out.println("hit");
                                broadcaster.sendHit(user.getName(), other.getName(), SPECIAL_DAMAGE);
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

                    @Override
                    public void onSetImage(Session session1, Image image) {
                        updates.getUpdates().get(session.getUser().getName()).setCharacterImage(image.getCharacterImage());

                    }

                    @Override
                    public void onSwift(Session session) {
                        session.getUser().setSpeed(Constants.PLAYER_SPEED_SWIFT);
                        session.getUser().setState("SWIFT");
                        updates.getUpdates().get(session.getUser().getName()).setSpeed(PLAYER_SPEED_SWIFT);
                        updates.getUpdates().get(session.getUser().getName()).setState("SWIFT");
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
        int characterImage = user.getCharacterImage();
        user = new User((float) (Math.random() * 600), (float) (Math.random() * 600),
                user.getName(), Constants.PLAYER_DOWN, 100, 100, 1000,
                user.getScore(), USER_STOP);
        user.setCharacterImage(characterImage);
        Update update = new Update(user.getName(), user.getX(), user.getY(),
                user.getHp(), user.getMana(), user.getStamina(),
                user.getDirection(), user.getScore(), user.getState());
        update.setCharacterImage(characterImage);
        updates.getUpdates().replace(user.getName(), update);
        session.setUser(user);
    }
}
