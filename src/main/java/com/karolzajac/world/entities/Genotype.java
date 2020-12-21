package com.karolzajac.world.entities;

import java.util.*;

public class Genotype {
    private List<Integer> genes = new ArrayList<>();
    Random random = new Random();

    public Genotype() {
        this.genes = createGenotype();
    }

    public Genotype(Animal parent1, Animal parent2) {
        this.genes = childGenotype(parent1, parent2);
    }

    public static void genesSort(List<Integer> genotype) {
        Collections.sort(genotype);
    }

    public List<Integer> createGenotype() {

        List<Integer> newGenotype = new ArrayList<>();

        do {
            newGenotype.add(randomGen());
        }
        while (newGenotype.size() < 32);

        //if genotype  not contains some moves need to add it by this function:
        newGenotype = addMissingGenes(newGenotype);

        genesSort(newGenotype);
        return newGenotype;
    }

    public List<Integer> childGenotype(Animal parent1, Animal parent2) {
        List<Integer> childGenotype = new ArrayList<>();
        //get two random indexes to split genotypes to 3 parts
        int split1 = randomIndex();
        int split2 = randomIndex();
        do {
            split2 = randomIndex();
        }
        while (split1 == split2);
        Animal stronger, weaker;
        if (parent1.getEnergy() > parent2.getEnergy()) {
            stronger = parent1;
            weaker = parent2;
        } else {
            stronger = parent2;
            weaker = parent1;
        }

        //creating a new genotype for child : two parts from stronger parent
        for (int i = 0; i < 32; i++) {
            if (i < Math.min(split1, split2) || i >= Math.max(split1, split2)) {
                childGenotype.add(i, stronger.getGenotype().getGenes().get(i));
            } else {
                childGenotype.add(i, weaker.getGenotype().getGenes().get(i));
            }

        }
        //if some genes are missing need to add it to let animal move every direction
        childGenotype = addMissingGenes(childGenotype);

        genesSort(childGenotype);
        return childGenotype;
    }

    public int randomGen() {
        //generate random gene from 0 to 7
        int g = random.nextInt(8);
        return g;
    }

    public int randomIndex() {
        //generate random gene position from 0 to 31
        int pos = random.nextInt(32);
        return pos;
    }

    public List<Integer> addMissingGenes(List<Integer> genotype) {

        for (int i = 0; i < 8; i++) {
            if (!genotype.contains(i)) {
                do {
                    int index = randomIndex();
                    int removedGen = genotype.get(index);
                    genotype.remove(index);
                    genotype.add(index, i);
                    if (genotype.contains(removedGen)) break;
                    else {
                        genotype.add(index, removedGen);
                    }
                }
                while (true);
            }
        }
        return genotype;
    }

    public String toString() {
        return this.genes.toString();
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }
}

