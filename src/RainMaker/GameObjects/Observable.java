package RainMaker.GameObjects;

public interface Observable {
  void attach(Observer o);
  void detach(Observer o);
  void notifyNewWindSpeed();
}
