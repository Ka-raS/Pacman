// package com.karas.pacman.entities.ghosts;

// import java.util.ArrayDeque;
// import java.util.Collection;

// import com.karas.pacman.Configs;
// import com.karas.pacman.commons.Algorithm;
// import com.karas.pacman.commons.Direction;
// import com.karas.pacman.commons.Vector2;
// import com.karas.pacman.entities.Ghost;
// import com.karas.pacman.entities.ImmutableEntity;
// import com.karas.pacman.maps.ImmutableMap;
// import com.karas.pacman.resources.SpriteSheet;

// public class Pinky extends Ghost {
    
//     public Pinky(ImmutableEntity pacman, ImmutableMap map) {
//         super(
//             Configs.Grid.PINKY_POS,
//             Direction.LEFT,
//             Configs.PX.PINKY_SPEED,
//             SpriteSheet.PINKY,
//             pacman,
//             map
//         );
//     }

//     @Override
//     protected Vector2 getHomeGridPos() {
//         return Configs.Grid.PINKY_POS;
//     }

//     @Override
//     protected SpriteSheet getSpriteName() {
//         return SpriteSheet.PINKY;
//     }

//     @Override
//     protected Collection<Direction> findPathToPacman(Vector2 begin, Vector2 target, boolean[][] movableGrid) {
//         ArrayDeque<Direction> path = new ArrayDeque<>(5);
//         for (Direction dir : Algorithm.bfsShortestPath(begin, target, movableGrid)) {
//             if (path.size() == 10)
//                 break;
//             path.add(dir);
//         }
//         return path;
//     }

//     @Override
//     protected Collection<Direction> findPathToRunaway(Vector2 position, Vector2 threat, boolean[][] movableGrid) {
//         return Algorithm.bfsFurthestPath(position, threat, movableGrid, 20);
//     }

//     @Override
//     protected Collection<Direction> findPathToHome(Vector2 begin, Vector2 target, boolean[][] movableGrid) {
//         return Algorithm.bfsShortestPath(begin, target, movableGrid);
//     }
    
// }
