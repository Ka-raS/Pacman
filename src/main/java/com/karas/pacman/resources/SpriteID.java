package com.karas.pacman.resources;

public enum SpriteID {
        
    PACMAN(0, 0, 8),
    BLINKY(2, 0, 8),
    PINKY( 3, 0, 8),
    INKY(  4, 0, 8),
    CLYDE( 5, 0, 8),

    DEAD_PACMAN(1, 0, 8),
    PREY_GHOST( 6, 0, 2),
    FLASH_GHOST(6, 0, 4),
    DEAD_GHOST( 6, 4, 4),
    SCORES(     7, 0, 4);

    public int getRow()    { return _row; }
    public int getColumn() { return _column; }
    public int getLength() { return _length; }


    private SpriteID(int row, int column, int length) {
        _row = row; _column = column; _length = length;
    }

    private final int _row, _column, _length;

}