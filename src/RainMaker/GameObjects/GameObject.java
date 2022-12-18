package RainMaker.GameObjects;

import javafx.scene.Group;

import java.util.Random;

public abstract class GameObject extends Group {
  static final Random RAND = new Random();

  public GameObject() {
  }

  double getRandomNumberWithinRange(int max, int min) {
    return RAND.nextInt(max - min) + min;
  }
}
