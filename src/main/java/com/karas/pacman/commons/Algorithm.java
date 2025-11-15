package com.karas.pacman.commons;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

import com.karas.pacman.maps.ImmutableMap;

public class Algorithm {
    
    public static ArrayDeque<Direction> bfsShortestPath(Vector2 begin, Vector2 target, ImmutableMap map) {
        ArrayDeque<Vector2> queue = new ArrayDeque<>();
        HashMap<Vector2, Vector2> previous = new HashMap<>();
        HashSet<Vector2> visited = new HashSet<>();

        queue.add(begin);
        visited.add(begin);

        while (!queue.isEmpty()) {
            Vector2 curr = queue.poll();
            if (curr.equals(target)) 
                break;

            for (Direction dir : Direction.values()) {
                Vector2 next = curr.add(dir.toVector2());
                if (!visited.contains(next) && map.isMovable(next)) {
                    queue.add(next);
                    previous.put(next, curr);
                    visited.add(next);
                }
            }
        }

        return reconstructPath(begin, target, previous);
    }

    public static ArrayDeque<Direction> bfsFurthestPath(Vector2 position, Vector2 threat, ImmutableMap map, int pathLength) {
        ArrayDeque<SimpleEntry<Vector2, Integer>> queue = new ArrayDeque<>();
        HashMap<Vector2, Vector2> previous = new HashMap<>();
        HashSet<Vector2> visited = new HashSet<>();

        queue.add(new SimpleEntry<>(position, 0));
        visited.add(position);
        double maxDistFromThreat = -1;
        Vector2 furthest = null;

        while (!queue.isEmpty()) {
            Vector2 curr = queue.peek().getKey();
            int steps = queue.poll().getValue();

            if (steps >= pathLength) {
                double distFromThreat = curr.toDistance(threat);
                if (maxDistFromThreat < distFromThreat) {
                    maxDistFromThreat = distFromThreat;
                    furthest = curr;
                }
                continue;
            }

            for (Direction dir : Direction.values()) {
                Vector2 next = curr.add(dir.toVector2());
                if (!visited.contains(next) && map.isMovable(position)) {
                    queue.add(new SimpleEntry<>(next, steps + 1));
                    previous.put(next, curr);
                    visited.add(next);
                }
            }
        }

        return reconstructPath(position, furthest, previous);
    }

    
    private static ArrayDeque<Direction> reconstructPath(Vector2 begin, Vector2 target, HashMap<Vector2, Vector2> previous) {
        ArrayDeque<Direction> result = new ArrayDeque<>();
        Vector2 curr = target;
        while (!curr.equals(begin) && previous.containsKey(curr)) {
            Vector2 prev = previous.get(curr);
            result.addFirst(prev.toDirection(curr));
            curr = prev;
        }
        return result;
    }

    private Algorithm() {}

}
