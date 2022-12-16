package RainMaker.GameObjects;

import RainMaker.Game;
import RainMaker.GameApp;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Random;

import static java.lang.Math.abs;

public class Cloud extends GameObject implements Updatable, Observer {
  private static final int CLOUD_WIDTH = 50;
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 10;
  private static final int RANDOM_MAX_W = GAME_WIDTH - CLOUD_WIDTH;
  private static final int RANDOM_MAX_H = GAME_HEIGHT - CLOUD_WIDTH;
  private static final int RANDOM_MIN_H = OFFSET + CLOUD_WIDTH;
  private static final int STARTING_COLOR = 255;
  private static final double CLOUD_OPACITY = .7;
  private static final double MAX_CLOUD_SPEED = 1.2;
  private int cloudColor = STARTING_COLOR;
  private int cloudFullness = 0;
  private Text cloudText;
  private BezierOval c;
  private static final Random RAND = new Random();
  private Point2D cloudPosition;
  private double cloudSpeed;
  private double cloudSpeedOffset = RAND.nextGaussian(.1, .3);
  private CloudState cloudState = new OnScreen();
  private AnimationTimer moveCloud;
  private double elapsedTime = 0;
  private double pastTime = 0;

  public Cloud(double windSpeed) {
    cloudText = new Text();
    c = new BezierOval();

    cloudText.setScaleY(-1);
    cloudText.setText(String.format("%4d", cloudFullness));
    cloudText.setFill(Color.BLACK);

    c.setFill(Color.WHITE);
    c.setOpacity(CLOUD_OPACITY);

    cloudSpeed = abs(windSpeed + cloudSpeedOffset);

    positionCloud();
    moveCloud(this);
  }

  public void update() {
    cloudText.setText(String.format("%4d", cloudFullness));
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
    return CLOUD_WIDTH * 2;
  }

  public double getCloudWidth() {
    return CLOUD_WIDTH;
  }

  public CloudState getState() {
    return cloudState;
  }

  void setState(CloudState state) {
    this.cloudState = state;
  }

  void positionCloud() {
    cloudPosition = new Point2D(RAND.nextInt
        (RANDOM_MAX_W - CLOUD_WIDTH) + CLOUD_WIDTH, RAND.nextInt
        (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);

    this.setTranslateX(cloudPosition.getX());
    this.setTranslateY(cloudPosition.getY());
    this.getChildren().addAll(c, cloudText);
  }

  public void repositionCloud() {
    cloudPosition = new Point2D(-CLOUD_WIDTH, RAND.nextInt
        (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);
    this.setTranslateX(cloudPosition.getX());
    this.setTranslateY(cloudPosition.getY());

    cloudSpeed -= cloudSpeedOffset;
    getCloudSpeedOffset();
    cloudSpeed += cloudSpeedOffset;
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
    if(cloudSpeed < MAX_CLOUD_SPEED){
      cloudSpeedOffset = RAND.nextGaussian(.1, .3);
    }
  }
  @Override
  public void updateWindSpeed(double newWindSpeed) {
    cloudSpeed = newWindSpeed + cloudSpeedOffset;
  }
}
