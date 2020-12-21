package com.karolzajac.world.entities;

import com.karolzajac.world.enums.MapDirection;
import com.karolzajac.world.interfaces.IPositionChangeObserver;
import com.karolzajac.world.map.AbstractWorldMap;
import com.karolzajac.world.map.RectangularMap;
import com.karolzajac.world.objects.Vector2d;

import java.util.*;

public class Animal {

    private MapDirection dir;
    private Vector2d position;
    private AbstractWorldMap map;
    private int energy;
    private int age;
    private List<Animal> childs = new ArrayList<>();
    private int childsNum;
    private List<IPositionChangeObserver> observers = new ArrayList<>();
    private Random random = new Random();
    private final Genotype genotype;

    public Animal(RectangularMap map, Vector2d initialPosition) {
        this.genotype = new Genotype();
        this.age = 0;
        this.childsNum = 0;
        this.map = map;
        this.dir = MapDirection.randomDirection();
        this.position = initialPosition;
        this.energy = map.getStartEnergy();
        addObserver(map);
    }

    public Animal(AbstractWorldMap map, Vector2d initialPosition, Animal parent1, Animal parent2) {
        this.genotype = new Genotype(parent1, parent2);
        this.age = 0;
        this.childsNum = 0;
        this.map = map;
        this.dir = MapDirection.randomDirection();
        this.position = initialPosition;
        this.energy = parent1.energy / 4 + parent2.energy / 4;
        addObserver(map);
        parent1.addChild(this);
        parent2.addChild(this);
    }

    public String toString() {
        return switch (this.dir) {
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

    public Vector2d getPosition() {
        return this.position;
    }

    public void move() {
        int randomGen = random.nextInt(32);
        int rotate = this.genotype.getGenes().get(randomGen);

        for (int i = 0; i < rotate; i++)
            this.dir = dir.next();
        Vector2d newPos = position.add(this.dir.toUnitVector());
        newPos = map.mapWrapping(newPos);
        positionChanged(position, newPos);
        position = newPos;
        this.age++;
    }

    void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver observer : observers) {
            observer.positionChanged(oldPosition, newPosition, this);
        }
    }

    public int getDominGene() {
        int[] counter = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (int gene : genotype.getGenes()) {
            switch (gene) {
                case 0 -> counter[0]++;
                case 1 -> counter[1]++;
                case 2 -> counter[2]++;
                case 3 -> counter[3]++;
                case 4 -> counter[4]++;
                case 5 -> counter[5]++;
                case 6 -> counter[6]++;
                case 7 -> counter[7]++;
            }
        }
        int dominGene = 0;
        for (int i = 0; i < counter.length; i++)
            if (counter[i] > counter[dominGene])
                dominGene = i;

        return dominGene;
    }

    public int getEnergy() {
        return energy;
    }

    public int getAge() {
        return age;
    }

    public List<Animal> getChilds() {
        return childs;
    }

    public void addChild(Animal child) {
        childs.add(child);
    }

    public int getChildsNumber() {
        return childsNum;
    }

    public void setChildsNumber(int childs) {
        this.childsNum = childs;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Genotype getGenotype() {
        return genotype;
    }

}

