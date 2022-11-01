import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;

public class GameApp extends Application {
  private static final int GAME_HEIGHT = 800;
  private static final int GAME_WIDTH = 400;
  private static final Point2D size = new Point2D(GAME_WIDTH, GAME_HEIGHT);
  private Game newGame = new Game();


  public void start(Stage stage) {
    Scene scene = new Scene(newGame, size.getX(), size.getY());
    scene.setFill(Color.BLACK);
    scene.setOnKeyPressed(this::setOnKeyPressed);


    stage.setTitle("Rain Maker Game");
    stage.setScene(scene);
    stage.show();
  }
  private void setOnKeyPressed(KeyEvent e){
    switch (e.getCode()){
      case  LEFT: newGame.headLeft();
            break;
      case  RIGHT: newGame.headRight();
            break;
      case  UP: newGame.increaseSpeed();
            break;
      case  DOWN: newGame.decreaseSpeed();
            break;
      case  I:  newGame.turnOnIgnition();
            break;
      case  SPACE: newGame.fireEvent(); break;
    }
  }
  static int getGameWidth(){
    return GAME_WIDTH;
  }
  static int getGameHeight(){
    return GAME_HEIGHT;
  }
}
interface Updatable{
  void update();
}

class Game extends Pane implements Updatable{
  private double elapsedTime = 0;
  private static boolean ignition = false;
  private static final int INITIAL_FUEL = 25000;
  private Pond gamePond = new Pond();
  private Cloud gameCloud = new Cloud();
  private Helipad gameHelipad = new Helipad();
  private Helicopter gameHelicopter = new Helicopter(INITIAL_FUEL);

  public Game(){
    this.setScaleY(-1);
    while(intersect(gameCloud, gamePond)){
      gameCloud.getNewPosition();
    }
    this.getChildren().addAll(gamePond,gameCloud,gameHelipad,gameHelicopter);
    init();
  }
  void init(){
    AnimationTimer game = new AnimationTimer() {
      private double old = -1;
      private double pastTime;
      @Override
      public void handle(long now) {
        if (old < 0) old = now;
        double delta = (now - old) / 1e9;
        old = now;
        elapsedTime += delta;
        if(elapsedTime - pastTime > 0.5){
          pastTime = elapsedTime;
        }
        update();
      }
    };
    game.start();
  }
  public void update(){
    if(gameCloud.isFullnessOverX(30)){
      gameCloud.update();
      gameHelicopter.update();
      gamePond.update();
    }else{
      gameCloud.update();
      gameHelicopter.update();
    }
  }
  boolean intersect(GameObject object1, GameObject object2){
    return object1.getBoundsInParent().intersects(object2.getBoundsInParent());
  }
  void headLeft(){
    if(ignition){
      gameHelicopter.headLeft();
    }
  }
  void headRight(){
    if(ignition){
      gameHelicopter.headRight();
    }
  }
  void increaseSpeed(){
    if(ignition){
      if(!gameHelicopter.isCopterMaxSpeed()) {
        gameHelicopter.increaseSpeed();
      }
    }
  }
  void decreaseSpeed(){
    if(ignition){
      if(!gameHelicopter.isCopterMinSpeed()) {
        gameHelicopter.decreaseSpeed();
      }
    }
  }
  void turnOnIgnition(){
    if(intersect(gameHelicopter, gameHelipad)){
      ignition = true;
    }
  }
  void turnOffIgnition(){
    if(intersect(gameHelicopter, gameHelipad)){
      ignition = false;
    }
  }
  void fireEvent(){
    if(intersect(gameHelicopter, gameCloud)){
      if(!gameCloud.isFullnessOverX(100)){
        gameCloud.colorChange();
      }
    }
  }
  static boolean getIgnition(){
    return ignition;
  }
}
abstract class GameObject extends Group {
  public GameObject(){

  }
}
class Pond extends GameObject implements Updatable{
  private static final Random RAND = new Random();
  private static final int INITIAL_POND_MAX = 30;
  private static final int INITIAL_POND_MIN = 10;
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 4;
  private static final int POND_RADIUS = RAND.nextInt(
      INITIAL_POND_MAX - INITIAL_POND_MIN) + INITIAL_POND_MIN;
  private static final int RANDOM_MAX_W = GAME_WIDTH - POND_RADIUS;
  private static final int RANDOM_MAX_H = GAME_HEIGHT - POND_RADIUS;
  private static final int RANDOM_MIN_H = OFFSET + POND_RADIUS;
  private Circle c = new Circle();
  private double pondRadius = POND_RADIUS;

  private Text pondText = new Text();
  private static final Point2D INITIAL_POND_POS = new Point2D(RAND.nextInt
      (RANDOM_MAX_W - POND_RADIUS) + POND_RADIUS, RAND.nextInt
      (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);
  public Pond(){
    pondText.setScaleY(-1);
    pondText.setTranslateX(INITIAL_POND_POS.getX());
    pondText.setTranslateY(INITIAL_POND_POS.getY());
    pondText.setText(String.format("%4d", (int) pondRadius));
    pondText.setFill(Color.WHITE);

    c.setFill(Color.BLUE);
    c.setRadius(POND_RADIUS);
    c.setCenterX(INITIAL_POND_POS.getX());
    c.setCenterY(INITIAL_POND_POS.getY());
    this.getChildren().addAll(c, pondText);
  }
  public void update(){
    pondText.setText(String.format("%4d", (int)pondRadius));
    grow();
  }
  void grow(){
    double area = (Math.pow(c.getRadius(), 2) * Math.PI) + 3;
    pondRadius = Math.sqrt(area/Math.PI);
    c.setRadius(pondRadius);
  }

}
class Cloud extends GameObject implements Updatable{
  private static final int CLOUD_WIDTH = 50;
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 4;
  private static final int RANDOM_MAX_W = GAME_WIDTH - CLOUD_WIDTH;
  private static final int RANDOM_MAX_H = GAME_HEIGHT - CLOUD_WIDTH;
  private static final int RANDOM_MIN_H = OFFSET + CLOUD_WIDTH;
  private static final int STARTING_COLOR = 255;
  private int cloudColor = STARTING_COLOR;
  private int cloudFullness = 0;
  private double currectTime = 0;
  private Text cloudText = new Text();
  private Circle c = new Circle();
  private static final Random RAND = new Random();
  private static final Point2D INITIAL_CLOUD_POS = new Point2D(RAND.nextInt
      (RANDOM_MAX_W - CLOUD_WIDTH) + CLOUD_WIDTH, RAND.nextInt
      (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);
  public Cloud(){
    cloudText.setScaleY(-1);
    cloudText.setTranslateX(INITIAL_CLOUD_POS.getX());
    cloudText.setTranslateY(INITIAL_CLOUD_POS.getY());
    cloudText.setText(String.format("%4d", cloudFullness));
    cloudText.setFill(Color.BLACK);

    c.setFill(Color.WHITE);
    c.setRadius(CLOUD_WIDTH);
    c.setCenterX(INITIAL_CLOUD_POS.getX());
    c.setCenterY(INITIAL_CLOUD_POS.getY());
    this.getChildren().addAll(c, cloudText);
  }
  void getNewPosition(){
    Point2D ballPosition = new Point2D(RAND.nextInt
        (RANDOM_MAX_W - CLOUD_WIDTH) + CLOUD_WIDTH, RAND.nextInt
        (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);
    c.setRadius(CLOUD_WIDTH);
    c.setCenterX(ballPosition.getX());
    c.setCenterY(ballPosition.getY());
  }
  public void update(){
    cloudText.setText(String.format("%4d", cloudFullness));
  }
  void colorChange(){
    if(cloudFullness < 100){
      cloudColor = STARTING_COLOR - cloudFullness;
      cloudFullness += 1;
    }
    c.setFill(Color.rgb(cloudColor,cloudColor, cloudColor));
  }
  void decay(){

  }
  boolean isFullnessOverX(double x){
    if(cloudFullness >= x){
      return true;
    }
    else return false;
  }
}
class Helipad extends GameObject {
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 4;
  private static final int HELIPAD_REC_W_H = 75;
  private static int HELIPAD_CIR_RAD = HELIPAD_REC_W_H - 20;
  private static final int HALF_GAME_WIDTH = GAME_WIDTH / 2;
  private static final int HALF_HELIPAD_POS = OFFSET / 2;
  private static final int HALF_HELIPAD_REC_W_H =
      HELIPAD_REC_W_H / 2;
  private Circle c = new Circle();
  private Rectangle r = new Rectangle();
  private static final Point2D INITIAL_REC_POS =
      new Point2D(HALF_GAME_WIDTH - HALF_HELIPAD_REC_W_H,
          HALF_HELIPAD_POS - HALF_HELIPAD_REC_W_H);

  public Helipad(){

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
class Helicopter extends GameObject implements Updatable{
  private static final int COPTER_RAD = 10;
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 4;
  private static final int HALF_GAME_WIDTH = GAME_WIDTH / 2;
  private static final int HALF_HELIPAD_POS = OFFSET / 2;
  private static final double INITIAL_HEADING = 90;
  private static final double INITIAL_SPEED = 0;
  private static final double MAX_COPTER_SPEED = 10;
  private static final double MIN_COPTER_SPEED = -2;
  private double heading = INITIAL_HEADING;
  private double speed = INITIAL_SPEED;
  private int fuel;
  private double speedIncrease = .1;
  private boolean ignition = false;
  private double headingChange = 5;
  private Circle c = new Circle();
  private Line l = new Line(0,0,0,30);
  private Text copterText = new Text();
  public Helicopter(int initialFuel){
    fuel = initialFuel;

    copterText.setScaleY(-1);
    copterText.setTranslateX(HALF_GAME_WIDTH);
    copterText.setTranslateY(HALF_HELIPAD_POS);
    copterText.setText(String.format("%9d", fuel));
    copterText.setFill(Color.YELLOW);

    c.setRadius(COPTER_RAD);
    c.setFill(Color.YELLOW);
    c.setCenterX(HALF_GAME_WIDTH);
    c.setCenterY(HALF_HELIPAD_POS);

    l.setStroke(Color.WHITE);
    l.setTranslateX(HALF_GAME_WIDTH);
    l.setTranslateY(HALF_HELIPAD_POS);
    this.getChildren().addAll(c, l, copterText);
  }
  public void update(){
    if(Game.getIgnition()){
      move();
      copterText.setText(String.format("%9d", fuel));
      updateFuel();
    }
  }
  void move(){
    this.setTranslateX(getTranslateX() + (speed * Math.cos(toRadians(heading))));
    this.setTranslateY(getTranslateY() + (speed * Math.sin(toRadians(heading))));
    System.out.println(speed);
    this.setRotate(heading - 90);
  }
  boolean isCopterMaxSpeed(){
    if(speed >= MAX_COPTER_SPEED){
      return true;
    }
    else return false;
  }
  boolean isCopterMinSpeed(){
    if(speed <= MIN_COPTER_SPEED) {
      return true;
    }
    else return false;
  }
  void updateFuel(){
    fuel -= (abs(1 + speed));
  }
  void headLeft(){
    heading += headingChange;
  }
  void headRight(){
    heading -= headingChange;
  }
  void increaseSpeed(){
    speed += speedIncrease;
  }
  void decreaseSpeed(){
    speed -= speedIncrease;
  }
}
class GameText extends GameObject{
  public GameText(){
  }
}

