package Models;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Updates {
    private ConcurrentHashMap<String, Update> updates;

    public Updates() {
        this.updates = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Update> getUpdates() {
        return updates;
    }
}
