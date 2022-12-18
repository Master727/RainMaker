package RainMaker.GameObjects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Clouds extends GameObject implements Iterable<Cloud> {
  private final List cloudList;

  public Clouds() {
    cloudList = new LinkedList<>();
  }

  public void addCloudToList(Cloud cloud) {
    cloudList.add(cloud);
    this.getChildren().add(cloud);
  }

  public int getListSize() {
    return cloudList.size();
  }

  @Override
  public Iterator<Cloud> iterator() {
    return cloudList.iterator();
  }
}
