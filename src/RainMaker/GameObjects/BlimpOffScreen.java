package RainMaker.GameObjects;

public class BlimpOffScreen implements BlimpState{
  @Override
  public void toggleState(Blimp blimp) {
    blimp.setState(new BlimpOnScreen());
  }
  @Override
  public String toString(){
    return "OffScreen";
  }

  @Override
  public void repositionBlimp(Blimp blimp) {
    blimp.repositionBlimp();
    blimp.setState(new BlimpOnScreen());
  }
}
