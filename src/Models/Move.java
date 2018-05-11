package Models;

import java.io.Serializable;

public class Move implements Serializable {
    private String direction;

    public Move() { }

    public Move(String user, String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
