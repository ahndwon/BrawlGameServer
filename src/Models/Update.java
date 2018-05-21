package Models;

public class Update {
    private String user;
    private float x;
    private float y;
    private int hp;
    private String direction;
    private int score;
    private String state;
    private int speed;
    private int characterImage = 10;

    public Update() {
//        user = null;
//        x = 0;
//        y = 0;
//        hp = 0;
        direction = "DOWN";
//        score = 0;
//        state = null;
        speed = Constants.PLAYER_SPEED;
    }

    public Update(String user, float x, float y, int hp,
                  String direction, int score, String state) {
        this.user = user;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.direction = direction;
        this.score = score;
        this.state = state;
        this.speed = Constants.PLAYER_SPEED;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void killed() {
        this.score /= 2;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCharacterImage() {
        return characterImage;
    }

    public void setCharacterImage(int characterImage) {
        this.characterImage = characterImage;
    }
}
