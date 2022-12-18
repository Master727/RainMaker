package RainMaker.GameObjects;

class CloudOnScreen implements CloudState {
  @Override
  public void toggleState(Cloud cloud) {
    cloud.setState(new CloudOffScreen());
  }

  @Override
  public String toString() {
    return "OnScreen";
  }

  @Override
  public void repositionCloud(Cloud cloud) {
  }
}
