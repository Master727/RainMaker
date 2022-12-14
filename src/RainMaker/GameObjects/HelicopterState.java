package RainMaker.GameObjects;

public interface HelicopterState {
  void toggleIgnition(Helicopter helicopter);

  String toString();

  //void seedingCloud();
  void increaseSpeed(Helicopter helicopter);

  void decreaseSpeed(Helicopter helicopter);

  void headLeft(Helicopter helicopter);

  void headRight(Helicopter helicopter);
}
