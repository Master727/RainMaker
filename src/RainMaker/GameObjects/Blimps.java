package RainMaker.GameObjects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Blimps extends TransientGameObjects implements Iterable<Blimp>{
  private final List blimpList;
  public Blimps(){
    blimpList = new LinkedList<>();
  }
  public void addBlimpToList(Blimp blimp) {
    blimpList.add(blimp);
    this.getChildren().add(blimp);
  }

  public int getListSize() {
    return blimpList.size();
  }

  @Override
  public Iterator<Blimp> iterator() {
    return blimpList.iterator();
  }
}
