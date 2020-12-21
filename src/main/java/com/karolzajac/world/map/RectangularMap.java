package com.karolzajac.world.map;

import com.karolzajac.world.objects.Vector2d;
import com.karolzajac.world.entities.Animal;
import com.karolzajac.world.entities.Grass;

import java.util.Random;

import static java.lang.Math.*;

public class RectangularMap extends AbstractWorldMap {
    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    private final int jungleWidth;
    private final int jungleHeight;
    Random random = new Random();

    public RectangularMap(int width, int height, int jungleRatio, int startEnergy, int moveEnergy, int plantEnergy) {
        this.width = width - 1;
        this.height = height - 1;
        this.jungleWidth = round(((float) width - 1) / jungleRatio);
        this.jungleHeight = round(((float) height - 1) / jungleRatio);
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
    }

    public void growGrass() {
        Vector2d newJungleGrassPos = randomJunglePosition();
        Grass newJungleGrass = new Grass(newJungleGrassPos);

        Vector2d newStepGrassPos = randomStepPosition();
        Grass newStepGrass = new Grass(newStepGrassPos);
        if (newJungleGrassPos != null)
            grassPieces.put(newJungleGrassPos, newJungleGrass);
        if (newStepGrassPos != null)
            grassPieces.put(newStepGrassPos, newStepGrass);
    }

    public boolean isJunglePosition(Vector2d position) {
        return position.getX() >= round(((float) width - jungleWidth) / 2) && position.getX() <= width - round(((float) width - jungleWidth) / 2)
                && position.getY() >= round(((float) height - jungleHeight) / 2) && position.getY() <= height - round(((float) height - jungleHeight) / 2);
    }

    private Vector2d randomStepPosition() {
        int x;
        int y;
        int n = 0;
        do {
            x = random.nextInt(width + 1);
            y = random.nextInt(height + 1);
            if (!isJunglePosition(new Vector2d(x, y)) && !isOccupied(new Vector2d(x, y)))
                return new Vector2d(x, y);
            n++;
        }
        while (n < 30);
        for (int i = 0; i <= width; i++)
            for (int j = 0; j <= height; j++)
                if (!isJunglePosition(new Vector2d(i, j)) && !isOccupied(new Vector2d(i, j)))
                    return new Vector2d(i, j);
        return null;
    }

    private Vector2d randomJunglePosition() {
        int x, y;
        int n = 0;
        do {
            x = random.nextInt(width - round(((float) width - jungleWidth) / 2) - round(((float) width - jungleWidth) / 2) + 1) + round(((float) width - jungleWidth) / 2);
            y = random.nextInt(height - round(((float) height - jungleHeight) / 2) - round(((float) height - jungleHeight) / 2) + 1) + round(((float) height - jungleHeight) / 2);
            if (!isOccupied(new Vector2d(x, y)))
                return new Vector2d(x, y);
            n++;
        }
        while (n < 30);
        //we cannot look for random too many times so after 30 tries we will take first free position
        for (int i = round(((float) width - jungleWidth) / 2); i <= width - round(((float) width - jungleWidth) / 2); i++)
            for (int j = round(((float) height - jungleHeight) / 2); j <= round(((float) height - jungleHeight) / 2); j++)
                if (!isOccupied(new Vector2d(i, j)))
                    return new Vector2d(i, j);
        return null;
    }


    @Override
    public Vector2d LowerLeft() {
        return new Vector2d(0, 0);
    }

    @Override
    public Vector2d UpperRight() {
        return new Vector2d(width, height);
    }


    @Override
    public boolean isOccupied(Vector2d position) {
        return (animals.containsKey(position) && animals.get(position).get(0).getEnergy() >= moveEnergy) || grassPieces.containsKey(position);
    }

    @Override
    public Object objectAt(Vector2d position) {
        if (isOccupied(position)) {
            if (animals.containsKey(position)) {
                if (animals.get(position).get(0).getEnergy() >= moveEnergy)
                    return animals.get(position).get(0);
            }
            if (grassPieces.containsKey(position)) return grassPieces.get(position);
        }
        return null;
    }

    public Animal dominatorAt(Vector2d position) {
        if (animals.containsKey(position) && animals.get(position).get(0).getEnergy() > moveEnergy)
            return animals.get(position).get(0);
        return null;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

}
