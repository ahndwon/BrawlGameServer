package Models;

public class Map {
    private int[] map;
    private final int SWAMP = 1;
    private final int HEAL = 2;

    public Map() {
        map = new int[1024];
        for (int i = 0; i < map.length; i++) {
            map[i] = 0;
        }
        shuffleHP();
        shuffleSwamp();
    }

    private void shuffleHP() {
        int[] items = new int[map.length];

        for (int i = 0; i < map.length; i++) {
            items[i] = i;
        }

        shuffle(items);
        insertItem(items, HEAL, 64);
    }

    private void shuffleSwamp() {
        int[] swamps = new int[map.length];

        for (int i = 0; i < map.length; i++) {
            swamps[i] = i;
        }

        shuffle(swamps);
        insertItem(swamps, SWAMP, 129);
    }

    private void shuffle(int[] data) {
        for (int i = 0; i < map.length; i++) {
            int src = (int) (Math.random() * map.length);
            int dst = (int) (Math.random() * map.length);

            int temp = data[src];
            data[src] = data[dst];
            data[dst] = temp;
        }
    }

    private void insertItem(int[] items, int item, int number) {
        for (int i = 0; i < number; i++) {
            map[items[i]] = item;
        }
    }

    public int[] getMap() {
        return map;
    }
}