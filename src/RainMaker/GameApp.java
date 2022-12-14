package RainMaker;

import RainMaker.GameObjects.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;

import java.io.File;
import java.util.Random;

public class GameApp extends Application {
  private static final int GAME_HEIGHT = 800;
  private static final int GAME_WIDTH = 800;
  private static final Point2D size = new Point2D(GAME_WIDTH, GAME_HEIGHT);

  public void start(Stage stage) {

    Scene scene = new Scene(Game.getInstance(), size.getX(), size.getY());
    scene.setOnKeyPressed(this::setOnKeyPressed);


    stage.setTitle("Rain Maker Game");
    stage.setScene(scene);
    stage.show();
  }

  private void setOnKeyPressed(KeyEvent e) {
    switch (e.getCode()) {
      case LEFT:
        Game.getInstance().headLeft();
        break;
      case RIGHT:
        Game.getInstance().headRight();
        break;
      case UP:
        Game.getInstance().increaseSpeed();
        break;
      case DOWN:
        Game.getInstance().decreaseSpeed();
        break;
      case I:
        Game.getInstance().flipIgnition();
        break;
      case R:
        Game.getInstance().hardRestart();
        break;
      case SPACE:
        Game.getInstance().fireEvent();
        break;
    }
  }

  public static int getGameWidth() {
    return GAME_WIDTH;
  }

  public static int getGameHeight() {
    return GAME_HEIGHT;
  }
}

