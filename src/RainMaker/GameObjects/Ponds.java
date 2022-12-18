package RainMaker.GameObjects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Ponds extends GameObject implements Iterable<Pond> {
  private final List<Pond> pondList;

  public Ponds() {
    pondList = new LinkedList<>();
  }

  public void addPondToList(Pond p) {
    if (getListSize() > 1) {
      for (Pond pond : pondList) {
        if (isIntersect(pond, p)) {
          p.getNewPosition();
        }
      }
    }
    pondList.add(p);
    this.getChildren().add(p);
  }

  int getListSize() {
    return pondList.size();
  }

  public boolean isFullnessOfAllPondsOverX(int x) {
    int totalFullnessOfAllPonds = 0;
    for (Pond pond : pondList) {
      totalFullnessOfAllPonds =
          (totalFullnessOfAllPonds + pond.getPondRadius()) / getListSize();
    }
    return totalFullnessOfAllPonds > x;
  }

  boolean isIntersect(Pond pond1, Pond pond2) {
    return pond1.getBoundsInParent().intersects(pond2.getBoundsInParent());
  }

  @Override
  public Iterator<Pond> iterator() {
    return pondList.iterator();
  }
}
