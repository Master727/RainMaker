package RainMaker.GameObjects;

class OffScreenRight implements CloudState {
  @Override
  public void toggleState(Cloud cloud) {
    cloud.setState(new OnScreen());
  }

  @Override
  public void repositionCloud(Cloud cloud) {
    cloud.repositionCloud();
    cloud.setState(new OnScreen());
  }

  @Override
  public String toString() {
    return "OffScreenRight";
  }
}
