package RainMaker.GameObjects;

public interface CloudState {
  void toggleState(Cloud cloud);

  String toString();

  void repositionCloud(Cloud cloud);
}
