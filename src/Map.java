public class Map {

    private int[] map;

    public Map() {

        map = new int[1024];
        for (int i = 0; i < map.length; i++) {
            map[i] = 0;
        }

        shuffle(map);
    }

    private void shuffle(int[] map) {

        for (int i = 0; i < 129 + 64; i++) {
            map[i] = (129 + i) % 129;
        }

        for (int i = 0; i < map.length * 3; i++) {
            int src = (int) (Math.random() * map.length);
            int dst = (int) (Math.random() * map.length);

            int temp = map[src];
            map[src] = map[dst];
            map[dst] = temp;
        }

    }

    public int[] getMap(){
        return map;
    }

}
