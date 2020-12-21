package com.karolzajac.world;

import com.karolzajac.world.entities.Animal;
import com.karolzajac.world.enums.MapDirection;
import com.karolzajac.world.map.RectangularMap;
import com.karolzajac.world.objects.Vector2d;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class SimulationEngine {
    private final RectangularMap map;
    private final List<Animal> deadAnimals = new ArrayList<>();
    private final List<Integer> deathDay = new ArrayList<>();
    private final List<List<Button>> view;
    public GridPane root;
    public Text text;
    public Animal followed = null;
    private String stats;
    private int day = 0;


    public SimulationEngine(RectangularMap map, List<List<Button>> map1, GridPane root, Text stat) {
        this.map = map;
        this.view = map1;
        this.root = root;
        this.text = stat;
    }

    public void runSimulation() {
        day++;
        growNewPlants();
        moveAnimals();
        removeDeadAnimals();
        eatGrass();
        copulate();
        showActualStats();
        drawColour();

    }

    //placing primary animals on the map
    public void placePrimaryAnimals(int n, RectangularMap map) {
        Random random = new Random();
        int x;
        int y;
        int i = 0;
        do {
            x = random.nextInt(map.width + 1);
            y = random.nextInt(map.height + 1);
            Animal newPet = new Animal(map, new Vector2d(x, y));
            map.place(newPet);
            i++;
        }
        while (i < n);
    }

    //PARTS OF SIMULATION:

    //1) every "day" of simulation starts with growing two new plants
    private void growNewPlants() {
        map.growGrass();
    }

    //2) move animal if alive, if dead - remove it from the map
    private void moveAnimals() {
        int i = 0;
        for (Animal a : map.animalList) {
            if (a.getEnergy() >= map.getMoveEnergy() && !deadAnimals.contains(a)) {
                a.move();
                a.setEnergy(a.getEnergy() - map.getMoveEnergy());
            } else {
                deadAnimals.add(a);
                deathDay.add(day - 1);
                view.get(map.height - a.getPosition().getY()).get(a.getPosition().getX()).setStyle("-fx-background-color: #ff1a1a");
            }

            if (map.animalList.isEmpty()) {
                //System.out.println("All animals are dead! RIP");
                break;
            }
            i++;
        }

    }

    private void removeDeadAnimals() {
        for (Animal a : deadAnimals) {
            this.map.animalList.remove(a);
        }
    }

    //3) time for dinner:) if animals stands on plants they earn energy
    //   if more animals on one plant they will fight for it and... the strongest always winning
    private void eatGrass() {
        Set<Vector2d> keys = map.grassPieces.keySet();
        List<Vector2d> tmp = new CopyOnWriteArrayList<>(keys);
        for (Vector2d key : tmp) {
            if (map.dominatorAt(key) != null) {
                map.dominatorAt(key).setEnergy(map.dominatorAt(key).getEnergy() + map.getPlantEnergy());
                map.grassPieces.remove(key);
            }
        }

    }

    //4) more than 1 animals on the same position = <3  only two the strongest animals can copulate
    private void copulate() {
        Set<Vector2d> keys = map.animals.keySet();
        List<Animal> newAnimals = new CopyOnWriteArrayList<>();
        for (Vector2d key : keys) {
            if (map.animals.get(key).size() >= 2) {
                //take two strongest on position
                Animal parent1 = map.animals.get(key).get(0);
                Animal parent2 = map.animals.get(key).get(1);
                if (canCopulate(parent1, parent2)) {
                    //creating child
                    Animal child = new Animal(map, bornPosition(key), parent1, parent2);
                    parent1.setEnergy(parent1.getEnergy() - parent1.getEnergy() / 4);
                    parent2.setEnergy(parent2.getEnergy() - parent2.getEnergy() / 4);
                    parent1.setChildsNumber(parent1.getChildsNumber() + 1);
                    parent2.setChildsNumber(parent2.getChildsNumber() + 1);
                    newAnimals.add(child);
                }
            }
        }
        for (Animal child : newAnimals) {
            map.place(child);
        }
    }

    private Vector2d bornPosition(Vector2d position) {
        Random random = new Random();
        int randomDir = random.nextInt(8);
        MapDirection dir = MapDirection.NORTH;
        for (int i = 0; i < randomDir; i++) dir = dir.next();
        MapDirection startDir = dir;
        Vector2d newPos;
        do {
            dir = dir.next();
            newPos = position.add(dir.toUnitVector());
            if (dir == startDir) break;
        }
        while (map.isOccupied(newPos));
        position = newPos;
        position = map.mapWrapping(position);
        return position;
    }

    private boolean canCopulate(Animal parent1, Animal parent2) {
        return (parent1.getEnergy() >= map.getStartEnergy() / 2 && parent2.getEnergy() >= map.getStartEnergy() / 2);
    }

    private void drawColour() {
        for (int i = 0; i <= map.width; i++) {
            for (int j = 0; j <= map.height; j++) {
                if (map.isOccupied(new Vector2d(i, j))) {
                    if (map.objectAt(new Vector2d(i, j)) instanceof Animal) {
                        switch (animalColour(((Animal) map.objectAt(new Vector2d(i, j))).getEnergy())) {
                            case 1 -> setButtonColor(i, j, "#392613");
                            case 2 -> setButtonColor(i, j, "#604020");
                            case 3 -> setButtonColor(i, j, "#734d26");
                            case 4 -> setButtonColor(i, j, "#996633");
                            case 5 -> setButtonColor(i, j, "#bf8040");
                            case 6 -> setButtonColor(i, j, "#cc9966");
                            case 7 -> setButtonColor(i, j, "#d9b38c");
                            case 8 -> setButtonColor(i, j, "#dfbf9f");
                            case 9 -> setButtonColor(i, j, "#ecd9c6");
                        }
                        if (map.objectAt(new Vector2d(i, j)) == followed) setButtonColor(i, j, "#0000ff");
                    } else {
                        if (map.objectAt(new Vector2d(i, j)) != null)
                            setButtonColor(i, j, "#006600");
                    }
                } else {
                    if (map.isJunglePosition(new Vector2d(i, j)))
                        setButtonColor(i, j, "#46b946");
                    else setButtonColor(i, j, "#609f70");
                }
            }
        }
    }

    private void setButtonColor(int i, int j, String color) {
        view.get(map.height - j).get(i).setStyle("-fx-background-color: " + color);
    }

    private int animalColour(int energy) {
        if (energy > 3 * map.getStartEnergy()) return 1;
        if (energy <= 3 * map.getStartEnergy() && energy > 2 * map.getStartEnergy()) return 2;
        if (energy <= 2 * map.getStartEnergy() && energy > 3 * map.getStartEnergy() / 2) return 3;
        if (energy <= 3 * map.getStartEnergy() / 2 && energy > map.getStartEnergy()) return 4;
        if (energy <= map.getStartEnergy() && energy > 3 * map.getStartEnergy() / 4) return 5;
        if (energy <= 3 * map.getStartEnergy() / 4 && energy > map.getStartEnergy() / 2) return 6;
        if (energy <= map.getStartEnergy() / 2 && energy > map.getStartEnergy() / 4) return 7;
        if (energy <= map.getStartEnergy() / 4 && energy > map.getStartEnergy() / 8) return 8;
        if (energy <= map.getStartEnergy() / 8) return 9;
        else return 10;
    }

    //statistics
    private void showActualStats() {
        int tmp = getMostCommonGene();
        String preparedText = "CURRENT STATISTICS:";
        String followedText = "";
        preparedText += "\n Simulation Day: " + day;
        preparedText += "\nAnimals alive: " + map.animalList.size();
        preparedText += "\nPlants: " + map.grassPieces.size();
        preparedText += "\nDominant gene of all : " + tmp;
        preparedText += "\nAnimals with dominant gene: " + getAnimalsWithDominGene(tmp);
        preparedText += "\nAverage energy of animal: " + averageEnergy();
        preparedText += "\nAverage survived days: " + averageLifeLength();
        preparedText += "\nAverage number of animal's childs: " + averageChildNumber();

        stats = preparedText;
        if (followed != null) {
            followedText += "\n\nFOLLOWED ANIMAL: ";
            followedText += "\nPosition: " + followed.getPosition() + ", Energy: " + followed.getEnergy();
            followedText += "\nGenotype:\n " + followed.getGenotype().toString();
            followedText += "\nDominant gene:\n " + followed.getDominGene();
            followedText += "\nNubmer of childs:\n " + followed.getChildsNumber();
            followedText += "\nNubmer of descendants:\n " + descendants(followed);

            if (deadAnimals.contains(followed)) {
                followedText = "\n\nFOLLOWED ANIMAL: \nFollowed animal is already dead!";
                followedText += "\nDay of the death: " + deathDay.get(deadAnimals.indexOf(followed));
            }
        }
        text.setText(preparedText + followedText);
    }

    public void onClickEvent(int i, int j) {
        Object object = map.objectAt(new Vector2d(j, map.height - i));
        if (object instanceof Animal) {
            followed = (Animal) object;
            view.get(i).get(j).setStyle("-fx-background-color: #0000ff");
        }
        showActualStats();
    }

    private int getMostCommonGene() {
        int[] counter = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        for (Animal a : map.animalList) {
            for (int gene : a.getGenotype().getGenes()) {
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
        }
        int mostCommonGene = 0;
        for (int i = 0; i <= 7; i++)
            if (counter[i] > counter[mostCommonGene])
                mostCommonGene = i;

        return mostCommonGene;
    }

    private List<Vector2d> getAnimalsWithDominGene(int mostCommonGene) {
        List<Vector2d> animals = new ArrayList<>();
        for (Animal a : map.animalList) {
            if (a.getDominGene() == mostCommonGene)
                animals.add(a.getPosition());
        }
        return animals;
    }

    private int averageEnergy() {
        int sumEnergy = 0;
        for (Animal a : map.animalList)
            sumEnergy += a.getEnergy();
        return Math.round((float) sumEnergy / map.animalList.size());
    }

    private int averageLifeLength() {
        int sum = 0;
        for (Animal a : deadAnimals)
            sum += a.getAge();
        return Math.round((float) sum / deadAnimals.size());
    }

    private int averageChildNumber() {
        int sum = 0;
        for (Animal a : map.animalList)
            sum += a.getChildsNumber();
        return Math.round((float) sum / map.animalList.size());
    }

    public void writeStatsToFile(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(stats);
        fileWriter.close();
    }

    private int descendants(Animal animal) {
        int sum = 0;
        for (Animal child : animal.getChilds()) {
            sum += descendants(child);
        }
        return sum + animal.getChildsNumber();
    }
}
