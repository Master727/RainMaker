package RainMaker.GameObjects;

class OnScreen implements CloudState {
  @Override
  public void toggleState(Cloud cloud) {
    cloud.setState(new OffScreenRight());
  }

  @Override
  public String toString() {
    return "OnScreen";
  }

  @Override
  public void repositionCloud(Cloud cloud) {
  }
}
