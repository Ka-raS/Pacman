package com.karas.pacman.entities.ghosts;

import java.util.ArrayDeque;
import java.util.Collection;

import com.karas.pacman.Configs;
import com.karas.pacman.commons.Algorithm;
import com.karas.pacman.commons.Direction;
import com.karas.pacman.commons.Vector2;
import com.karas.pacman.entities.Ghost;
import com.karas.pacman.entities.ImmutableEntity;
import com.karas.pacman.maps.ImmutableMap;
import com.karas.pacman.resources.SpriteSheet;

public class Inky extends Ghost {
    
    public Inky(ImmutableEntity pacman, ImmutableEntity blinky, ImmutableMap map) {
        super(
            Configs.Grid.INKY_POS,
            Direction.RIGHT,
            Configs.PX.INKY_SPEED,
            SpriteSheet.INKY,
            pacman,
            map
        );
        _Blinky = blinky;
    }

    @Override
    protected Vector2 getHomeGridPos() {
        return Configs.Grid.INKY_POS;
    }

    @Override
    protected SpriteSheet getSpriteName() {
        return SpriteSheet.INKY;
    }

    @Override
    protected Collection<Direction> findPathToPacman(Vector2 currPos, Vector2 pacmanPos, ImmutableMap map) {
        Vector2 pacmanPredict = pacmanPos;
        Direction pacmanDir = getPacman().getDirection();
        for (int i = 0; i < 2; ++i) {
            Vector2 next = pacmanPredict.add(pacmanDir.toVector2());
            if (!map.isMovable(next))
                break;
            pacmanPredict = next;
        }

        Vector2 target = pacmanPredict.mul(2).sub(_Blinky.getNearestMovableGridPos());
        if (!map.isMovable(target))
            for (Direction dir : Direction.values()) {
                Vector2 near = target.add(dir.toVector2());
                if (map.isMovable(near)) {
                    target = near;
                    break;
                }
            }

        ArrayDeque<Direction> path = new ArrayDeque<>(5);
        for (Direction dir : Algorithm.bfsShortestPath(currPos, target, map)) {
            if (path.size() == 5)
                break;
            path.add(dir);
        }
        return path;
    }

    @Override
    protected Collection<Direction> findPathToRunaway(Vector2 currPos, Vector2 pacmanPos, ImmutableMap map) {
        return Algorithm.bfsFurthestPath(currPos, pacmanPos, map, 20);
    }

    @Override
    protected Collection<Direction> findPathToHome(Vector2 currPos, Vector2 homePos, ImmutableMap map) {
        return Algorithm.bfsShortestPath(currPos, homePos, map);
    }


    private final ImmutableEntity _Blinky;
    
}
