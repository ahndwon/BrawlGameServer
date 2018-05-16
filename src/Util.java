import Models.Constants;

public class Util implements Constants {
    public static int getIndexByPos(int x, int y) {
        return x / BLOCK_WIDTH + (((y - MENU_HEIGHT)) / BLOCK_HEIGHT) * BLOCK_COUNT_X;
    }

    public static int getPosXByIndex(int index) {
        return index % BLOCK_COUNT_X * BLOCK_WIDTH;
    }

    public static int getPosYByIndex(int index)
    {
        return index / BLOCK_COUNT_X * BLOCK_HEIGHT + MENU_HEIGHT;
    }

    public static boolean isLeft(int index) {
        return index % BLOCK_COUNT_X == 0;
    }

    public static boolean isRight(int index) {
        return index % BLOCK_COUNT_X == BLOCK_COUNT_X - 1;
    }

    public static boolean isTop(int index) {
        return index / BLOCK_COUNT_X == 0;
    }

    public static boolean isBottom(int index) {
        return index / BLOCK_COUNT_X == BLOCK_COUNT_Y - 1;
    }

    public static boolean checkMousePos(int mouseX, int mouseY) {
        return 0 < mouseX && mouseX < WINDOW_WIDTH && MENU_HEIGHT < mouseY && mouseY < WINDOW_HEIGHT;
    }

 }
