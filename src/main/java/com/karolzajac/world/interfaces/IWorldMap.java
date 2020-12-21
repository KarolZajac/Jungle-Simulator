package com.karolzajac.world.interfaces;

import com.karolzajac.world.entities.Animal;
import com.karolzajac.world.objects.Vector2d;

public interface IWorldMap {

    boolean place(Animal animal);

    boolean isOccupied(Vector2d position);

    Object objectAt(Vector2d position);
}

