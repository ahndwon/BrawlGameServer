package Models;

public class Map {
    private int[] map;

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
//
//        int shuffle = map.length;
//
//        for (int i = 0; i < shuffle; i++) {
//            int src = (int) (Math.random() * map.length);
//            int dst = (int) (Math.random() * map.length);
//
//            int temp = items[src];
//            items[src] = items[dst];
//            items[dst] = temp;
//        }
//        insertItem(items);
        for (int i = 0; i < 64; i++) {
            map[items[i]] = 2;
        }
    }

    private void shuffle(int[] data) {
        int shuffle = map.length;

        for (int i = 0; i < shuffle; i++) {
            int src = (int) (Math.random() * map.length);
            int dst = (int) (Math.random() * map.length);

            int temp = data[src];
            data[src] = data[dst];
            data[dst] = temp;
        }
    }

    private void shuffleSwamp() {
        int[] swamps = new int[map.length];

        for (int i = 0; i < map.length; i++) {
            swamps[i] = i;
        }

//        shuffle(swamps);
        int shuffle = map.length;

        for (int i = 0; i < shuffle; i++) {
            int src = (int) (Math.random() * map.length);
            int dst = (int) (Math.random() * map.length);

            int temp = swamps[src];
            swamps[src] = swamps[dst];
            swamps[dst] = temp;
        }
//        insertItem(swamps);
        for (int i = 0; i < 129; i++) {
            map[swamps[i]] = 1;
        }
    }

    private void insertItem(int[] items) {
        for (int i = 0; i < items.length; i++) {
            map[i] = items[i];
        }
    }

    public int[] getMap() {
        return map;
    }
}