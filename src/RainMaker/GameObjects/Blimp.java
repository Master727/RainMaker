package RainMaker.GameObjects;

import RainMaker.GameApp;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Random;

public class Blimp extends TransientGameObjects implements Updatable{
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int MAX_FUEL_CAPACITY = 10000;
  private static final int MIN_FUEL_CAPACITY = 5000;
  private static final int TEXT_OFFSET_W = 3;
  private static final int TEXT_OFFSET_H = 10;
  private static final double INITIAL_BLIMP_SPEED = .2;
  public static final int IMAGE_HEIGHT = 80;
  public static final double CONVERSION_FACTOR = 1e9;
  private double blimpSpeed = INITIAL_BLIMP_SPEED;
  private final File helicopterBladeFile = new File(
      "Images/Battle Blimp.png");
  private final Image image = new Image(helicopterBladeFile.toURI().toString());
  private final ImageView blimp = new ImageView(image);
  private final GameText blimpText;
  private static double elapsedTime = 0;
  private final int randomMaxH;
  private final int randomMinH;
  private int fuelCapacity;
  private BlimpState blimpState = new BlimpOnScreen();
  AnimationTimer moveBlimp;
  public Blimp(){

    blimpText = new GameText();
    blimp.setPreserveRatio(true);
    blimp.setScaleY(-1);
    blimp.setFitHeight(IMAGE_HEIGHT);

    fuelCapacity = RAND.nextInt(MAX_FUEL_CAPACITY - MIN_FUEL_CAPACITY)
        + MIN_FUEL_CAPACITY;

    blimpText.setText(String.format("%5d", fuelCapacity));
    blimpText.setFill(Color.YELLOW);
    int textPositionX = (int)(blimp.getLayoutBounds().getWidth() - TEXT_OFFSET_W) / 2;
    int textPositionY =
        (int)(blimp.getLayoutBounds().getHeight() + TEXT_OFFSET_H) / 2;
    blimpText.positionText(textPositionX, textPositionY);
    blimpText.setFont(15);


    randomMaxH = GAME_HEIGHT - (int)blimp.getLayoutBounds().getHeight();
    randomMinH = (int) (blimp.getTranslateY() +
        blimp.getLayoutBounds().getHeight());

    repositionBlimp();
    this.getChildren().addAll(blimp, blimpText);
    setBlimpSpeed();
    moveBlimp(this);

  }

  void setBlimpSpeed() {
    double speedOffset = RAND.nextGaussian(.1,.2);
    blimpSpeed = INITIAL_BLIMP_SPEED + speedOffset;
  }
  public void setBlimpFuel(int refuelingAmount){
    if(fuelCapacity > 0){
      fuelCapacity -= refuelingAmount;
    }
  }

  public double getBlimpSpeed() {
    return blimpSpeed;
  }

  void moveBlimp(Blimp blimp) {
   moveBlimp = new AnimationTimer() {
      private double old = -1;

      @Override
      public void handle(long now) {
        if (old < 0) old = now;
        double delta = (now - old) / CONVERSION_FACTOR;
        old = now;
        elapsedTime += delta;
        blimp.setTranslateX(blimp.getTranslateX() + blimpSpeed);
      }
    };
    moveBlimp.start();
  }
  public void repositionBlimp() {
    Point2D newRepositionPoint =
        new Point2D(- blimp.getLayoutBounds().getWidth(),
            RAND.nextInt(
                randomMaxH - randomMinH) + randomMinH);
    this.setTranslateX(newRepositionPoint.getX());
    this.setTranslateY(newRepositionPoint.getY());
    fuelCapacity = RAND.nextInt(MAX_FUEL_CAPACITY - MIN_FUEL_CAPACITY)
        + MIN_FUEL_CAPACITY;
    setBlimpSpeed();
  }
  public BlimpState getState() {
    return blimpState;
  }

  void setState(BlimpState state) {
    this.blimpState = state;
  }
  @Override
  public void update() {
    blimpText.setText(String.format("%5d", fuelCapacity));
  }
  public int getBlimpWidth(){
    return (int)blimp.getLayoutBounds().getWidth();
  }
  public void stopBlimp(){
    moveBlimp.stop();
  }
}
