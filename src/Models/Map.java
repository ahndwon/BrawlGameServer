package Models;

public class Map implements Constants {
    private int[] map;

    public Map() {
        map = new int[1024];
        for (int i = 0; i < map.length; i++) {
            map[i] = 0;
        }
        shuffleHP();
        shuffleSwamp();
        shuffleMana();
    }

    private void shuffleHP() {
        int[] items = new int[map.length];

        for (int i = 0; i < map.length; i++) {
            items[i] = i;
        }

        shuffle(items);
        insertItem(items, TILE_HEAL, 64);
    }

    private void shuffleMana() {
        int[] items = new int[map.length];

        for (int i = 0; i < map.length; i++) {
            items[i] = i;
        }

        shuffle(items);
        insertItem(items, TILE_MANA, 16);
    }

    private void shuffleSwamp() {
        int[] swamps = new int[map.length];

        for (int i = 0; i < map.length; i++) {
            swamps[i] = i;
        }

        shuffle(swamps);
        insertItem(swamps, TILE_SWAMP, 129);
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