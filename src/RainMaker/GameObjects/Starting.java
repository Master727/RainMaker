package RainMaker.GameObjects;


class Starting implements HelicopterState {
  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Stopping());
  }

  @Override
  public String toString() {
    return "Starting";
  }

  @Override
  public void increaseSpeed(Helicopter helicopter) {
  }

  @Override
  public void decreaseSpeed(Helicopter helicopter) {
  }

  @Override
  public void headLeft(Helicopter helicopter) {
  }

  @Override
  public void headRight(Helicopter helicopter) {
  }

}
