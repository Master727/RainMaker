package RainMaker.GameObjects;

class CloudOffScreen implements CloudState {
  @Override
  public void toggleState(Cloud cloud) {
    cloud.setState(new CloudOnScreen());
  }

  @Override
  public void repositionCloud(Cloud cloud) {
    cloud.repositionCloud();
    cloud.setState(new CloudOnScreen());
  }

  @Override
  public String toString() {
    return "OffScreen";
  }
}
