package RainMaker.GameObjects;

class Ready implements HelicopterState {
  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Stopping());
  }

  @Override
  public String toString() {
    return "Ready";
  }

  @Override
  public void increaseSpeed(Helicopter helicopter) {
    if (!helicopter.isHelicopterMaxSpeed()) {
      helicopter.increaseSpeed();
    }
  }

  @Override
  public void decreaseSpeed(Helicopter helicopter) {
    if (!helicopter.isHelicopterMinSpeed()) {
      helicopter.decreaseSpeed();
    }
  }

  @Override
  public void headLeft(Helicopter helicopter) {
    helicopter.headLeft();
  }

  @Override
  public void headRight(Helicopter helicopter) {
    helicopter.headRight();
  }
}
