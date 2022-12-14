package RainMaker.GameObjects;

import RainMaker.GameApp;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Random;

public class Pond extends GameObject implements Updatable {
  private static final Random RAND = new Random();
  private static final int INITIAL_POND_MAX = 30;
  private static final int INITIAL_POND_MIN = 10;
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 10;
  private Circle c;
  private double pondRadius = 0;
  private Text pondText;
  private Point2D pondPosition;

  public Pond() {
    pondText = new Text();
    pondText.setScaleY(-1);
    pondText.setFill(Color.WHITE);
    getNewPosition();
  }

  public void update() {
    pondText.setText(String.format("%4d", (int) pondRadius));
    grow();
  }

  void grow() {
    if (isPondRadiusUnderX(100)) {
      double area = (Math.pow(c.getRadius(), 2) * Math.PI) + 3;
      pondRadius = Math.sqrt(area / Math.PI);
      c.setRadius(pondRadius);
    }
  }

  public boolean isPondRadiusUnderX(double x) {
    return !(pondRadius >= x);
  }

  int getPondRadius() {
    return (int) pondRadius;
  }

  void getNewPosition() {
    this.getChildren().clear();
    c = new Circle();
    pondRadius = RAND.nextInt(
        INITIAL_POND_MAX - INITIAL_POND_MIN) + INITIAL_POND_MIN;
    int randomMaxH = GAME_HEIGHT - (int) pondRadius;
    int randomMaxW = GAME_WIDTH - (int) pondRadius;
    int randomMinH = OFFSET + (int) pondRadius;

    pondPosition = new Point2D(RAND.nextInt
        (randomMaxW - (int) pondRadius) + pondRadius, RAND.nextInt
        (randomMaxH - randomMinH) + randomMinH);

//    pondText.setTranslateX(pondPosition.getX());
//    pondText.setTranslateY(pondPosition.getY());
    pondText.setText(String.format("%4d", (int) pondRadius));

    c.setFill(Color.BLUE);
    c.setRadius(pondRadius);
    this.setTranslateX(pondPosition.getX());
    this.setTranslateY(pondPosition.getY());
    this.getChildren().addAll(c, pondText);
  }
}
