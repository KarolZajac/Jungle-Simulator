package com.karolzajac.world.enums;

import com.karolzajac.world.objects.Vector2d;

import java.util.Random;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public String toString() {
        return switch (this) {
            case NORTH -> "N";
            case SOUTH -> "S";
            case EAST -> "E";
            case WEST -> "W";
            case NORTHEAST -> "NE";
            case SOUTHEAST -> "SE";
            case SOUTHWEST -> "SW";
            case NORTHWEST -> "NW";
        };
    }

    public MapDirection next() {
        return switch (this) {
            case NORTH -> NORTHEAST;
            case SOUTH -> SOUTHWEST;
            case EAST -> SOUTHEAST;
            case WEST -> NORTHWEST;
            case NORTHEAST -> EAST;
            case SOUTHEAST -> SOUTH;
            case SOUTHWEST -> WEST;
            case NORTHWEST -> NORTH;
        };
    }

    public MapDirection previous() {
        return switch (this) {
            case NORTH -> NORTHWEST;
            case SOUTH -> SOUTHEAST;
            case EAST -> SOUTHWEST;
            case WEST -> NORTHEAST;
            case NORTHEAST -> NORTH;
            case SOUTHEAST -> EAST;
            case SOUTHWEST -> SOUTH;
            case NORTHWEST -> WEST;
        };
    }

    public Vector2d toUnitVector() {
        Vector2d result = new Vector2d(0, 0);
        switch (this) {
            case NORTH:
                return new Vector2d(0, 1);
            case SOUTH:
                return new Vector2d(0, -1);
            case EAST:
                return new Vector2d(1, 0);
            case WEST:
                return new Vector2d(-1, 0);
            case NORTHEAST:
                return new Vector2d(1, 1);
            case SOUTHEAST:
                return new Vector2d(1, -1);
            case SOUTHWEST:
                return new Vector2d(-1, -1);
            case NORTHWEST:
                return new Vector2d(-1, 1);
        }
        return result;
    }

    public static MapDirection randomDirection() {
        Random random = new Random();
        int id = random.nextInt(8);
        return switch (id) {
            case 0 -> NORTH;
            case 1 -> NORTHEAST;
            case 2 -> EAST;
            case 3 -> SOUTHEAST;
            case 4 -> SOUTH;
            case 5 -> SOUTHWEST;
            case 6 -> WEST;
            case 7 -> NORTHWEST;
            default -> NORTH;
        };
    }
}