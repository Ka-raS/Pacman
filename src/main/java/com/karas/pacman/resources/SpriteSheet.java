package com.karas.pacman.resources;

public enum SpriteSheet {
        
    PACMAN(0, 0, 8), DEAD_PACMAN(1, 0, 8), 
    BLINKY(2, 0, 8), PREY_GHOST( 6, 0, 4),
    PINKY( 3, 0, 8), DEAD_GHOST( 6, 4, 4),
    INKY(  4, 0, 8), SCORES(     7, 0, 4),
    CLYDE( 5, 0, 8);

    public int getRow() { return _row; }
    public int getCol() { return _col; }
    public int getLen() { return _len; }


    private SpriteSheet(int row, int col, int len) {
        _row = row; _col = col; _len = len;
    }

    private final int _row, _col, _len;

}