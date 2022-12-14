package RainMaker.GameObjects;


class Off implements HelicopterState {
  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Starting());
  }

  @Override
  public String toString() {
    return "Off";
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
