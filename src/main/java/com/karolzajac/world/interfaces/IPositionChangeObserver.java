package com.karolzajac.world.interfaces;

import com.karolzajac.world.entities.Animal;
import com.karolzajac.world.objects.Vector2d;

public interface IPositionChangeObserver {

    void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal);
}
