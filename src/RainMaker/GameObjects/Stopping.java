package RainMaker.GameObjects;

class Stopping implements HelicopterState{

  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Starting());
  }
  @Override
  public String toString(){
    return "Stopping";
  }
  @Override
  public void increaseSpeed(Helicopter helicopter){}
  @Override
  public void decreaseSpeed(Helicopter helicopter){}
  @Override
  public void headLeft(Helicopter helicopter){}
  @Override
  public void headRight(Helicopter helicopter){}
}