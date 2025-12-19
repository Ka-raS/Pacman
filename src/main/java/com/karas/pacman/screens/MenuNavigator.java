package com.karas.pacman.screens;

import java.awt.Font;
import java.awt.Graphics2D;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Paintable;
import com.karas.pacman.commons.Vector2;

public class MenuNavigator implements Paintable {

    // we have std::pair at home they said
    public static record MenuOption(String label, Class<? extends Screen> screenClass) {}

    public MenuNavigator(Vector2 position, Font FontRef, MenuOption... options) {
        _hovering = 0;
        _position = position;
        _options = options;
        _Font = FontRef;
    }

    public void next() {
        _hovering = (_hovering + 1) % _options.length;
    }

    public void previous() {
        _hovering = (_hovering - 1 + _options.length) % _options.length;
    }

    public Class<? extends Screen> select() {
        return _options[_hovering].screenClass();
    }

    @Override
    public void repaint(Graphics2D G) {
        G.setFont(_Font);
        for (int i = 0; i < _options.length; i++) {
            G.setColor(i == _hovering ? Configs.Color.HIGHLIGHT : Configs.Color.TEXT);
            G.drawString(
                _options[i].label(), 
                _position.ix(), 
                _position.iy() + i * (_Font.getSize() + 10)
            );
        }
    }


    private final Font _Font;

    private final MenuOption[] _options;
    private final Vector2 _position;

    private int _hovering;
}

