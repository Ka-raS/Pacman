package com.karas.pacman.resources;

public enum SpriteSheet {
        
    PACMAN(0, 0, 8), DEAD_PACMAN(1, 0, 8), 
    BLINKY(2, 0, 8), PREY_GHOST( 6, 0, 4),
    PINKY( 3, 0, 8), DEAD_GHOST( 6, 4, 4),
    INKY(  4, 0, 8), SCORES(     7, 0, 4),
    CLYDE( 5, 0, 8);

    public int getRow() { return _Row; }
    public int getCol() { return _Col; }
    public int getLen() { return _Len; }


    private SpriteSheet(int row, int col, int len) {
        _Row = row; _Col = col; _Len = len;
    }

    private final int _Row, _Col, _Len;

}