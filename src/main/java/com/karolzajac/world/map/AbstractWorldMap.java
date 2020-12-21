package com.karolzajac.world.map;

import com.karolzajac.world.objects.Vector2d;
import com.karolzajac.world.entities.Animal;
import com.karolzajac.world.entities.Grass;
import com.karolzajac.world.interfaces.IPositionChangeObserver;
import com.karolzajac.world.interfaces.IWorldMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    public int width;
    public int height;
    public Map<Vector2d, List<Animal>> animals = new ConcurrentHashMap<>();
    public Map<Vector2d, Grass> grassPieces = new ConcurrentHashMap<>();
    public List<Animal> animalList = new CopyOnWriteArrayList<>();

    @Override
    public boolean place(Animal animal) {
        if (animals.containsKey(animal.getPosition())) {
            animals.get(animal.getPosition()).add(animal);
            animals.get(animal.getPosition()).sort(new Comparator<Animal>() {
                @Override
                public int compare(Animal animal1, Animal animal2) {
                    return Integer.compare(animal2.getEnergy(), animal1.getEnergy());
                }
            });
        } else {
            List<Animal> animalGroup = new ArrayList<>();
            animalGroup.add(animal);
            animals.put(animal.getPosition(), animalGroup);
        }
        animalList.add(animal);
        return true;
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        //removing from old position
        if (animals.get(oldPosition).size() == 1) animals.remove(oldPosition);
        else animals.get(oldPosition).remove(animal);
        //add on its new position
        if (animals.containsKey(newPosition)) {
            animals.get(newPosition).add(animal);
            animals.get(newPosition).sort(new Comparator<Animal>() {
                @Override
                public int compare(Animal animal1, Animal animal2) {
                    return Integer.compare(animal2.getEnergy(), animal1.getEnergy());
                }
            });
        } else {
            List<Animal> animalGroup = new ArrayList<>();
            animalGroup.add(animal);
            animals.put(newPosition, animalGroup);
        }
    }

    //check if given position is out of map, if yes returns position on the other side
    public Vector2d mapWrapping(Vector2d position) {
        if (position.getX() > width) position.setX(0);
        if (position.getY() > height) position.setY(0);
        if (position.getX() < 0) position.setX(width);
        if (position.getY() < 0) position.setY(height);

        return position;
    }

    @Override
    public String toString() {
        MapVisualizer mapVisualizer = new MapVisualizer(this);
        return mapVisualizer.draw(LowerLeft(), UpperRight());
    }

    public abstract Vector2d LowerLeft();

    public abstract Vector2d UpperRight();

}