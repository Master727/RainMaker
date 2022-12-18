package RainMaker.GameObjects;

public interface BlimpState {
  void toggleState(Blimp blimp);

  String toString();

  void repositionBlimp(Blimp blimp);
}
