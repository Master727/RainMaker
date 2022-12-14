package RainMaker.GameObjects;

import RainMaker.GameApp;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Helipad extends GameObject {
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 4;
  private static final int HELIPAD_REC_W_H = 75;
  private static final int HELIPAD_CIR_RAD = HELIPAD_REC_W_H - 20;
  private static final int HALF_GAME_WIDTH = GAME_WIDTH / 2;
  private static final int HALF_HELIPAD_POS = OFFSET / 2;
  private static final int HALF_HELIPAD_REC_W_H =
      HELIPAD_REC_W_H / 2;
  private final Circle c;
  private final Rectangle r;
  private static final Point2D INITIAL_REC_POS =
      new Point2D(HALF_GAME_WIDTH - HALF_HELIPAD_REC_W_H,
          HALF_HELIPAD_POS - HALF_HELIPAD_REC_W_H);

  public Helipad() {
    c = new Circle();
    r = new Rectangle();

    r.setFill(Color.TRANSPARENT);
    r.setStroke(Color.YELLOW);
    r.setHeight(HELIPAD_REC_W_H);
    r.setWidth(HELIPAD_REC_W_H);
    r.setTranslateX(INITIAL_REC_POS.getX());
    r.setTranslateY(INITIAL_REC_POS.getY());

    c.setFill(Color.TRANSPARENT);
    c.setStroke(Color.WHITE);
    c.setRadius(HELIPAD_CIR_RAD);
    c.setCenterX(HALF_GAME_WIDTH);
    c.setCenterY(HALF_HELIPAD_POS);
    this.getChildren().addAll(r, c);
  }
}
