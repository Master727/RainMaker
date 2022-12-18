package RainMaker.GameObjects;

public class BlimpOnScreen implements BlimpState {

  @Override
  public void toggleState(Blimp blimp) {
    blimp.setState(new BlimpOffScreen());
  }
  @Override
  public String toString(){
    return "OnScreen";
  }

  @Override
  public void repositionBlimp(Blimp blimp) {

  }
}
