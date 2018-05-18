package Models;


public class User implements Constants {
    private float x;
    private float y;
    private String name;
    private String direction;
    private int hp;
    private int score;
    private String state;
    private int speed;
    private int characterImage ;

    public User() {
        speed = Constants.PLAYER_SPEED;
    }

    public User(float x, float y, String name, String direction, int hp, int score, String state) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.direction = direction;
        this.hp = hp;
        this.score = score;
        this.state = state;
        this.speed = Constants.PLAYER_SPEED;
        this.characterImage = 0;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void attack() {
        score += HIT_SCORE;
    }

    public void damaged() {
        hp -= 10;
    }

    public int getHp() {
        return hp;
    }

    public int getScore() {
        return score;
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
