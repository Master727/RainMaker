package RainMaker.GameObjects;

import RainMaker.Game;
import RainMaker.GameApp;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.Random;

import static java.lang.Math.abs;

public class Cloud extends GameObject implements Updatable, Observer{
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 10;
  private int randomMaxW;
  private int randomMaxH;
  private int randomMinH;
  private int cloudWidth;
  private static final int STARTING_COLOR = 255;
  private static final double CLOUD_OPACITY = .7;
  private static final double MAX_CLOUD_SPEED = 1.2;
  private static final double  MIN_CLOUD_SPEED = .1;
  private int cloudColor = STARTING_COLOR;
  private int cloudFullness = 0;
  private final GameText cloudText;
  private final BezierOval c;
  private static final Random RAND = new Random();
  private double cloudSpeed;
  private double cloudSpeedOffset = RAND.nextGaussian(.1, .2);
  private CloudState cloudState = new CloudOnScreen();
  private double elapsedTime = 0;
  private double pastTime = 0;

  public Cloud(double windSpeed) {
    cloudText = new GameText();
    c = new BezierOval();

    cloudText.setText(String.format("%4d", cloudFullness));
    cloudText.setFill(Color.BLACK);

    cloudWidth = c.getBezierWidth();
    int cloudHeight = c.getBezierHeight();
    randomMaxW = GAME_WIDTH - cloudWidth;
    randomMaxH = GAME_HEIGHT - cloudHeight;
    randomMinH = OFFSET + cloudHeight;

    c.setFill(Color.WHITE);
    c.setOpacity(CLOUD_OPACITY);

    cloudSpeed = abs(windSpeed + cloudSpeedOffset);
    if(cloudSpeed < .1){
      cloudSpeed = windSpeed;
    }

    positionCloud();
    moveCloud(this);
  }

  public void update() {
    cloudText.setText(String.format(" %4d%%", cloudFullness));
  }

  public void colorChange() {
    cloudColor = STARTING_COLOR - cloudFullness;
    cloudFullness += 1;
    c.setFill(Color.rgb(cloudColor, cloudColor, cloudColor));
  }

  void decay() {
    cloudColor = STARTING_COLOR - cloudFullness;
    cloudFullness -= 1;
    c.setFill(Color.rgb(cloudColor, cloudColor, cloudColor));
  }

  public boolean isFullnessOverX(double x) {
    return cloudFullness >= x;
  }

  public double getCloudDiameter() {
    return cloudWidth * 2;
  }

  public double getCloudWidth() {
    return cloudWidth;
  }

  public CloudState getState() {
    return cloudState;
  }

  void setState(CloudState state) {
    this.cloudState = state;
  }
  AnimationTimer moveCloud;

  void positionCloud() {
    Point2D cloudPosition = new Point2D(RAND.nextInt
        (randomMaxW - cloudWidth) + cloudWidth, RAND.nextInt
        (randomMaxH - randomMinH) + randomMinH);
    cloudText.positionText(cloudText.getTranslateX() - 20,
        cloudText.getTranslateY() + 5);

    this.setTranslateX(cloudPosition.getX());
    this.setTranslateY(cloudPosition.getY());
    this.getChildren().addAll(c, cloudText);
  }

  public void repositionCloud() {
    Point2D cloudPosition = new Point2D(-cloudWidth, RAND.nextInt
        (randomMaxH - randomMinH) + randomMinH);


    this.setTranslateX(cloudPosition.getX());
    this.setTranslateY(cloudPosition.getY());
    cloudSpeed -= cloudSpeedOffset;
    getCloudSpeedOffset();
    cloudSpeed += cloudSpeedOffset;
    if(cloudSpeed < .1){
      cloudSpeed = .2;
    }
    cloudFullness = 0;
    c.setFill(Color.WHITE);
  }

  void moveCloud(Cloud cloud) {
    moveCloud = new AnimationTimer() {
      private double old = -1;

      @Override
      public void handle(long now) {
        if (old < 0) old = now;
        double delta = (now - old) / 1e9;
        old = now;
        elapsedTime += delta;
        cloud.setTranslateX(cloud.getTranslateX() + cloudSpeed);
        if (isFullnessOverX(30) && Game.getFireEvent() &&
            elapsedTime - pastTime > 1.5) {
          pastTime = elapsedTime;
          decay();
        }
      }
    };
    moveCloud.start();
  }
  void getCloudSpeedOffset(){
    if(cloudSpeed < MAX_CLOUD_SPEED && cloudSpeed > MIN_CLOUD_SPEED){
      cloudSpeedOffset = RAND.nextGaussian(.1, .3);
    }
  }
  @Override
  public void updateWindSpeed(double newWindSpeed) {
    cloudSpeed = newWindSpeed + cloudSpeedOffset;
  }
  public void stopClouds(){
    moveCloud.stop();
  }
}
