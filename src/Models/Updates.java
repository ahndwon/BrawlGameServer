package Models;

import java.util.HashMap;

public class Updates {
    private HashMap<String, Update> updates;

    public Updates() {
        this.updates = new HashMap<>();
    }

    public HashMap<String, Update> getUpdates() {
        return updates;
    }
}
