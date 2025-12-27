package com.karas.pacman.screens;

import java.awt.Font;
import java.awt.Graphics2D;

import com.karas.pacman.Constants;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;

public final class Navigator implements Paintable {

    // we have std::pair at home they said
    public static record Option(String label, Class<? extends Screen> screenClass) {}

    public Navigator(Vector2 position, Font FontRef, Option... options) {
        _index = 0;
        _position = position;
        _options = options;
        _Font = FontRef;
    }

    public void next() {
        _index = (_index + 1) % _options.length;
    }

    public void previous() {
        _index = (_index - 1 + _options.length) % _options.length;
    }

    public Class<? extends Screen> select() {
        return _options[_index].screenClass();
    }

    @Override
    public void repaint(Graphics2D G) {
        G.setFont(_Font);
        for (int i = 0; i < _options.length; i++) {
            G.setColor(i == _index ? Constants.Color.HIGHLIGHT : Constants.Color.TEXT);
            G.drawString(
                _options[i].label(), 
                _position.ix(), 
                _position.iy() + i * (_Font.getSize() + 10)
            );
        }
    }


    private final Font _Font;

    private final Option[] _options;
    private final Vector2 _position;
    private int _index;
}
