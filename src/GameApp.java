import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import java.io.File;
import java.util.*;

import static java.lang.Math.*;

public class GameApp extends Application {
  private static final int GAME_HEIGHT = 800;
  private static final int GAME_WIDTH = 800;
  private static final Point2D size = new Point2D(GAME_WIDTH, GAME_HEIGHT);

  private Game newGame;

  public void start(Stage stage) {
    //Image is taken from https://www.vectorstock.com/
    //Artist alexzel
    //All credit and rights belong to vectorstock and alexzel
    File backgroundFile = new File("Images/background3.jpg");
    Image backgroundImage = new Image(backgroundFile.toURI().toString());
    ImagePattern backgroundPattern = new ImagePattern(backgroundImage);
    newGame = new Game(backgroundImage);
    Scene scene = new Scene(newGame, size.getX(), size.getY());
    scene.setFill(backgroundPattern);
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
      case  I:  newGame.flipIgnition();
            break;
      case  R: newGame.hardRestart();
            break;
      case  SPACE: newGame.fireEvent();
            break;
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
  private static double elapsedTime = 0;
  private static double pastTime = 0;
  private static boolean fireEventInActive = true;
  private static final int INITIAL_FUEL = 25000;
  private Helipad gameHelipad;
  private Helicopter gameHelicopter;
  private AnimationTimer game;
  private Ponds gamePonds;
  private Clouds gameClouds;
  private static boolean cloudListInUse = true;
  private static final Random RAND = new Random();
  private static final int MAX_NUMBER_OF_CLOUDS = 6;
  private static final int MIN_NUMBER_OF_CLOUDS = 3;
  private static int numberOfCloudsOnScreen = 0;
  private static final StringBuilder STRING_BUILDER = new StringBuilder();
  private static Alert alert;
  public Game(Image backgroundImage){
    BackgroundImage paneBackground = new BackgroundImage(backgroundImage,
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT);
    Background background = new Background(paneBackground);
    this.setBackground(background);
    instantiateGameObjects();
    this.setScaleY(-1);
    init();
  }
  void init(){
    game = new AnimationTimer() {
      private double old = -1;
      @Override
      public void handle(long now) {
        if (old < 0) old = now;
        double delta = (now - old) / 1e9;
        old = now;
        elapsedTime += delta;
        update();
        gameWinLoseRestart();
      }
    };
    game.start();
  }
  public void update(){
    for (Cloud cloud : gameClouds) {
      if(cloud.isFullnessOverX(30)) {
//        if(elapsedTime - pastTime > 1.5 && fireEventInActive) {
//          pastTime = elapsedTime;
//          cloud.decay();
//        }
        for(Pond pond : gamePonds) {
          if(pond.isPondRadiusUnderX(100) && isCloudNearPond(cloud,
            pond)){
            pond.update();
          }
        }
      }
      cloud.update();
      if(isCloudOffScreen(cloud)){
        cloud.getState().toggleState(cloud);
        numberOfCloudsOnScreen--;
      }

    }
    cloudListInUse = !cloudListInUse;
    gameHelicopter.update();
    shouldCloudBeGenerated();
  }
  void instantiateGameObjects(){
    int initialNumberOfClouds = RAND.nextInt(
        MAX_NUMBER_OF_CLOUDS - MIN_NUMBER_OF_CLOUDS) + MIN_NUMBER_OF_CLOUDS;
    System.out.println(initialNumberOfClouds);

    gamePonds = new Ponds();
    gameClouds = new Clouds();

    gameHelipad = new Helipad();
    gameHelicopter = new Helicopter(INITIAL_FUEL);
    for(int i = 0; i < 3; i++){
      Pond gamePond = new Pond();
      gamePonds.addPondToList(gamePond);
    }
    for(int i = 0; i < initialNumberOfClouds; i++){
      makeNewOffScreenCloud();
    }
    this.getChildren().addAll(gamePonds,gameClouds,gameHelipad,
        gameHelicopter);
  }
  void makeNewOffScreenCloud(){
    Cloud gameCloud = new Cloud();
    gameClouds.addCloudToList(gameCloud);
    numberOfCloudsOnScreen++;
  }
  void makeNewOnScreenCloud(){
    Cloud gameCloud = new Cloud();
    gameCloud.repositionCloud();
    gameClouds.addCloudToList(gameCloud);
    numberOfCloudsOnScreen++;
  }
  void headLeft(){
    gameHelicopter.getState().headLeft(gameHelicopter);
  }
  void headRight(){
    gameHelicopter.getState().headRight(gameHelicopter);
  }
  void increaseSpeed(){
    gameHelicopter.getState().increaseSpeed(gameHelicopter);
  }
  void decreaseSpeed(){
    gameHelicopter.getState().decreaseSpeed(gameHelicopter);
  }
  void flipIgnition(){
    if(isIntersect(gameHelicopter, gameHelipad)){
      gameHelicopter.toggleIgnition(gameHelicopter);
    }
  }
  void fireEvent(){
    fireEventInActive = !fireEventInActive;
    for (Cloud gameCloud : gameClouds) {
      if(isIntersect(gameHelicopter, gameCloud)){
        if(!gameCloud.isFullnessOverX(100)){
          gameCloud.colorChange();
        }
      }
    }
    fireEventInActive = !fireEventInActive;
  }
   void gameWinLoseRestart(){
    if(gamePonds.isFullnessOfAllPondsOverX(80) || gameHelicopter.getFuel()
        < 1 ){
      game.stop();
      if(gamePonds.isFullnessOfAllPondsOverX(80) && isIntersect(gameHelicopter,
          gameHelipad) && gameHelicopter.getHelicopterStateName().equals(
              "Off")){
        addText("Congratulations! You won with a score of " +
            gameHelicopter.getFuel() + " Would you like to play " +
            "again?");
      }else{
        addText("Your helicopter has run out of fuel. " +
            "Would you like to try again?");
      }
      alert = new Alert(Alert.AlertType.CONFIRMATION, STRING_BUILDER.toString(),
          ButtonType.YES, ButtonType.NO);
      alert.setOnHidden(evt -> {
        if (alert.getResult() == ButtonType.YES) {
          this.getChildren().clear();
          instantiateGameObjects();
          this.init();
        }
        else
          Platform.exit();
      });
      alert.show();
    }
  }
  void hardRestart(){
    game.stop();
    this.getChildren().clear();
    instantiateGameObjects();
    this.init();
  }
  void addText(String s){
    STRING_BUILDER.setLength(0);
    STRING_BUILDER.append(s);
  }
  boolean isIntersect(GameObject object1, GameObject object2){
    return object1.getBoundsInParent().intersects(object2.getBoundsInParent());
  }
  boolean isCloudNearPond(Cloud cloud, Pond pond){
    double distance;
    distance =
        Math.hypot(Math.abs(cloud.getTranslateX() - pond.getTranslateX()),
            Math.abs(cloud.getTranslateY() - pond.getTranslateY()));

    return cloud.getCloudDiameter() * 2.5 > distance;
  }
  void shouldCloudBeGenerated(){
    int randomNumber = RAND.nextInt(200);
    if(numberOfCloudsOnScreen < MAX_NUMBER_OF_CLOUDS && randomNumber < 2) {
      if (gameClouds.getListSize() < MAX_NUMBER_OF_CLOUDS) {
        makeNewOnScreenCloud();
      }
      else{
        for(Cloud cloud : gameClouds) {
          if(cloud.getState().toString().equals("OffScreenRight")){
            cloud.getState().repositionCloud(cloud);
            System.out.println(randomNumber);
            break;
          }
        }
      }
    }
  }
  boolean isCloudOffScreen(Cloud cloud){
    return cloud.getTranslateX() - cloud.getCloudWidth() >
        GameApp.getGameWidth();
  }
  static boolean getFireEvent() {
    return fireEventInActive;
  }
}
abstract class GameObject extends Group {
  private static final Random RAND = new Random();
  public GameObject(){
  }
  double getRandomNumberWithinRange(int max, int min){
    return RAND.nextInt(max - min) + min;
  }
}

class Pond extends GameObject implements Updatable{
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
  public Pond(){
    pondText = new Text();
    pondText.setScaleY(-1);
    pondText.setFill(Color.WHITE);
    getNewPosition();
  }
  public void update(){
    pondText.setText(String.format("%4d", (int)pondRadius));
    grow();
  }
  void grow(){
    if(isPondRadiusUnderX(100)){
      double area = (Math.pow(c.getRadius(), 2) * Math.PI) + 3;
      pondRadius = Math.sqrt(area/Math.PI);
      c.setRadius(pondRadius);
    }
  }
  boolean isPondRadiusUnderX(double x){
    return !(pondRadius >= x);
  }
  int getPondRadius(){
    return (int)pondRadius;
  }
  void getNewPosition(){
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

class Ponds extends GameObject implements Iterable<Pond> {
  private List<Pond> pondList;

  public Ponds() {
    pondList = new LinkedList<>();
  }
  void addPondToList(Pond p) {
    if(getListSize() > 1){
      for (Pond pond : pondList) {
        if(isIntersect(pond, p)){
          p.getNewPosition();
        }
      }
    }
    pondList.add(p);
    this.getChildren().add(p);
  }
  int getListSize(){
    return pondList.size();
  }
  boolean isFullnessOfAllPondsOverX(int x){
    int totalFullnessOfAllPonds = 0;
    for (Pond pond : pondList) {
      totalFullnessOfAllPonds =
          (totalFullnessOfAllPonds + pond.getPondRadius()) / getListSize();
    }
    return totalFullnessOfAllPonds > x;
  }
  boolean isIntersect(Pond pond1, Pond pond2){
    return pond1.getBoundsInParent().intersects(pond2.getBoundsInParent());
  }
  @Override
  public Iterator<Pond> iterator() {
    return pondList.iterator();
  }
}

class BezierOval extends Group{
  private static final Random RAND = new Random();
  private Ellipse ellipse;
  private QuadCurve bezierCurve;
  private double initailTheta = 0;
  private double currentTheta = initailTheta;
  private Point2D startPoint;
  private Point2D endPoint;
  private Point2D controlPoint;
  private double radiusFactor = RAND.nextDouble(2 + 1.5) + 1.5;
  private final int maxRadius = 50;
  private final int minRadius = 40;
  private final int minorMaxRadius = 25;
  private final int minorMinRadius = 20;
  private final double maxThetaChange = PI / 2;
  private final double minThetaChange = PI / 3;
  private final int majorRadius =
      RAND.nextInt(maxRadius - minRadius) + minRadius;
  private final int minorRadius =
      RAND.nextInt(minorMaxRadius - minorMinRadius) + minorMinRadius;
  private double randomThetaIncrease =
      RAND.nextDouble(maxThetaChange - minThetaChange) + minThetaChange;

  public BezierOval(){
    double startPointX = majorRadius * cos(initailTheta);
    double startPointY = minorRadius * sin(initailTheta);
    ellipse = new Ellipse(majorRadius, minorRadius);
    startPoint = new Point2D(startPointX, startPointY);

    while(currentTheta < initailTheta + (2 * PI)){
      double lastTheta = currentTheta;
      //randomize the currentTheta
      //Could create a theta larger than the oval. Set a ceiling for
      // currentTheta and a floor
//      if((2 * PI) - currentTheta < maxThetaChange){
//        break;
//      }
      currentTheta += randomThetaIncrease;
      double radiusFactor = RAND.nextDouble(2 - 1.5) + 1.5;

      double cx =
          majorRadius * cos((lastTheta + currentTheta) /2) * radiusFactor;
      double cy = minorRadius * sin((lastTheta + currentTheta) /2) * radiusFactor;

      //radiusFractor needs to be randomized
      controlPoint = new Point2D(cx, cy);

      startPointX = majorRadius * cos(currentTheta);
      startPointY = minorRadius * sin(currentTheta);
      endPoint = new Point2D(startPointX, startPointY);
      bezierCurve = new QuadCurve(startPoint.getX(), startPoint.getY(),
          controlPoint.getX(), controlPoint.getY(), endPoint.getX(),
          endPoint.getY());
      startPoint = endPoint;
      bezierCurve.setFill(Color.WHITE);
      bezierCurve.setStroke(Color.BLACK);
      this.getChildren().add(bezierCurve);
    }
    this.getChildren().add(ellipse);
  }
  void setFill(Color color){
    ellipse.setFill(color);
//    for ((Shape) Node obj : this.getChildren() ) {
//      set color of all the bezier curve
//    }

  }
}

class Cloud extends GameObject implements Updatable{
  private static final int CLOUD_WIDTH = 50;
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 10;
  private static final int RANDOM_MAX_W = GAME_WIDTH - CLOUD_WIDTH;
  private static final int RANDOM_MAX_H = GAME_HEIGHT - CLOUD_WIDTH;
  private static final int RANDOM_MIN_H = OFFSET + CLOUD_WIDTH;
  private static final int STARTING_COLOR = 255;
  private static final double CLOUD_OPACITY = .7;
  private int cloudColor = STARTING_COLOR;
  private int cloudFullness = 0;
  private Text cloudText;
  private BezierOval c;
  private static final Random RAND = new Random();
  private Point2D CloudPosition;
  private static final double WIND_SPEED = 1;
  private double cloudSpeed = WIND_SPEED * abs(RAND.nextGaussian(.2,.6));
  private CloudState cloudState = new OnScreen();
  private AnimationTimer moveCloud;
  private double elapsedTime = 0;
  private double pastTime = 0;

  public Cloud(){
    cloudText = new Text();
    c = new BezierOval();

    cloudText.setScaleY(-1);
    cloudText.setText(String.format("%4d", cloudFullness));
    cloudText.setFill(Color.BLACK);

    c.setFill(Color.WHITE);
    //c.setRadius(CLOUD_WIDTH);
    c.setOpacity(CLOUD_OPACITY);

    positionCloud();
    moveCloud(this);
  }
  public void update(){
    cloudText.setText(String.format("%4d", cloudFullness));
  }
  void colorChange(){
    cloudColor = STARTING_COLOR - cloudFullness;
    cloudFullness += 1;
    c.setFill(Color.rgb(cloudColor, cloudColor, cloudColor));
  }
  void decay(){
    cloudColor = STARTING_COLOR - cloudFullness;
    cloudFullness -= 1;
    c.setFill(Color.rgb(cloudColor,cloudColor, cloudColor));
  }
  boolean isFullnessOverX(double x){
    return cloudFullness >= x;
  }
  double getCloudDiameter() {
    return CLOUD_WIDTH * 2;
  }
  double getSpeed(){
    return cloudSpeed;
  }
  double getCloudWidth(){
    return CLOUD_WIDTH;
  }
  CloudState getState() {
    return cloudState;
  }
  void setState(CloudState state) {
    this.cloudState = state;
  }
 void positionCloud() {
    CloudPosition = new Point2D(RAND.nextInt
        (RANDOM_MAX_W - CLOUD_WIDTH) + CLOUD_WIDTH, RAND.nextInt
        (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);

    this.setTranslateX(CloudPosition.getX());
    this.setTranslateY(CloudPosition.getY());
    this.getChildren().addAll(c, cloudText);
  }
  void repositionCloud(){
    CloudPosition = new Point2D(-CLOUD_WIDTH, RAND.nextInt
        (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);
    this.setTranslateX(CloudPosition.getX());
    this.setTranslateY(CloudPosition.getY());

    cloudSpeed = WIND_SPEED * abs(RAND.nextGaussian());
    cloudFullness = 0;
    c.setFill(Color.WHITE);
  }
  void moveCloud(Cloud cloud){
    moveCloud = new AnimationTimer() {
      private double old = -1;
      @Override
      public void handle(long now) {
        if (old < 0) old = now;
        double delta = (now - old) / 1e9;
        old = now;
        elapsedTime += delta;
        cloud.setTranslateX(cloud.getTranslateX() + cloudSpeed);
        if(isFullnessOverX(30) && Game.getFireEvent() &&
            elapsedTime - pastTime > 1.5){
          pastTime = elapsedTime;
          decay();
        }
      }
    };
    moveCloud.start();
  }
}
//class OffScreenCloud extends GameObject implements Updatable,Cloud{
//  private static final int CLOUD_WIDTH = 50;
//  private static final int GAME_HEIGHT = GameApp.getGameHeight();
//  private static final int GAME_WIDTH = GameApp.getGameWidth();
//  private static final int OFFSET = GAME_HEIGHT / 10;
//  private static final int RANDOM_MAX_W = GAME_WIDTH - CLOUD_WIDTH;
//  private static final int RANDOM_MAX_H = GAME_HEIGHT - CLOUD_WIDTH;
//  private static final int RANDOM_MIN_H = OFFSET + CLOUD_WIDTH;
//  private static final int STARTING_COLOR = 255;
//  private static final double CLOUD_OPACITY = .7;
//  private int cloudColor = STARTING_COLOR;
//  private int cloudFullness = 0;
//  private Text cloudText;
//  private Circle c;
//  private static final Random RAND = new Random();
//  private Point2D CloudPosition;
//  private static final double WIND_SPEED = 1;
//  private double cloudSpeed = WIND_SPEED * abs(RAND.nextGaussian(.4, .7));
//  private CloudState cloudState = new OffScreenRight();
//  private AnimationTimer moveCloud;
//  private double elapsedTime = 0;
//
//  public OffScreenCloud() {
//    cloudText = new Text();
//    c = new Circle();
//
//    cloudText.setScaleY(-1);
//    cloudText.setText(String.format("%4d", cloudFullness));
//    cloudText.setFill(Color.BLACK);
//
//    c.setFill(Color.WHITE);
//    c.setRadius(CLOUD_WIDTH);
//    c.setOpacity(CLOUD_OPACITY);
//
//    positionCloud();
//    moveCloud(this);
//  }
//  public void update() {
//    cloudText.setText(String.format("%4d", cloudFullness));
//  }
//  void colorChange() {
//    cloudColor = STARTING_COLOR - cloudFullness;
//    cloudFullness += 1;
//    c.setFill(Color.rgb(cloudColor, cloudColor, cloudColor));
//  }
//  void decay() {
//    if (isFullnessOverX(30)) {
//      cloudColor = STARTING_COLOR - cloudFullness;
//      cloudFullness -= 1;
//      c.setFill(Color.rgb(cloudColor, cloudColor, cloudColor));
//    }
//  }
//  boolean isFullnessOverX(double x) {
//    return cloudFullness >= x;
//  }
//  double getCloudDiameter() {
//    return CLOUD_WIDTH * 2;
//  }
//  double getSpeed() {
//    return cloudSpeed;
//  }
//  double getCloudWidth() {
//    return CLOUD_WIDTH;
//  }
//  CloudState getState() {
//    return cloudState;
//  }
//  void setState(CloudState state) {
//    this.cloudState = state;
//  }
//  @Override
//  public void positionCloud() {
//    CloudPosition = new Point2D(-CLOUD_WIDTH, RAND.nextInt
//        (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);
//    this.setTranslateX(CloudPosition.getX());
//    this.setTranslateY(CloudPosition.getY());
//  }
//  void repositionCloud() {
//    CloudPosition = new Point2D(-CLOUD_WIDTH, RAND.nextInt
//        (RANDOM_MAX_H - RANDOM_MIN_H) + RANDOM_MIN_H);
//    this.setTranslateX(CloudPosition.getX());
//    this.setTranslateY(CloudPosition.getY());
//
//    cloudSpeed = WIND_SPEED * abs(RAND.nextGaussian());
//    cloudFullness = 0;
//  }
//  void moveCloud(OffScreenCloud cloud) {
//    moveCloud = new AnimationTimer() {
//      private double old = -1;
//
//      @Override
//      public void handle(long now) {
//        if (old < 0) old = now;
//        double delta = (now - old) / 1e9;
//        old = now;
//        elapsedTime += delta;
//        cloud.setTranslateX(cloud.getTranslateX() + cloudSpeed);
//      }
//    };
//    moveCloud.start();
//  }
//}
//
//interface Cloud{
//  void positionCloud();
//}
//
//class CloudFactory extends GameObject{
//  public Cloud createCloud(String s){
//    return switch (s) {
//      case "OnScreenCloud" -> new OnScreenCloud();
//      default -> new OffScreenCloud();
//    };
//  }
//}

interface CloudState{
  void toggleState(Cloud cloud);
  String toString();
  void repositionCloud(Cloud cloud);
}
class OnScreen implements CloudState{
  @Override
  public void toggleState(Cloud cloud) {
    cloud.setState(new OffScreenRight());
  }
  @Override
  public String toString(){
    return "OnScreen";
  }
  @Override
  public void repositionCloud(Cloud cloud) {}
}
class OffScreenRight implements CloudState{
  @Override
  public void toggleState(Cloud cloud) {
    cloud.setState(new OnScreen());
  }
  @Override
  public void repositionCloud(Cloud cloud) {
    cloud.repositionCloud();
    cloud.setState(new OnScreen());
  }
  @Override
  public String toString(){
    return "OffScreenRight";
  }
}

class Clouds extends GameObject implements Iterable<Cloud>{
  private List cloudList;
  private AnimationTimer moveCloud;
  private double elapsedTime = 0;
  public Clouds(){
    cloudList = new LinkedList<>();
  }
  void addCloudToList(Cloud cloud) {
    cloudList.add(cloud);
    this.getChildren().add(cloud);
  }
  int getListSize(){
    return cloudList.size();
  }
  @Override
  public Iterator<Cloud> iterator() {
    return cloudList.iterator();
  }
}

class Helipad extends GameObject {
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

  public Helipad(){
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
class Helicopter extends GameObject implements Updatable{
  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final int OFFSET = GAME_HEIGHT / 4;
  private static final int HALF_GAME_WIDTH = GAME_WIDTH / 2;
  private static final int HALF_HELIPAD_POS = OFFSET / 2;
  private static final double INITIAL_HEADING = 0;
  private static final double INITIAL_SPEED = 0;
  private static final double MAX_COPTER_SPEED = 10;
  private static final double MIN_COPTER_SPEED = -2;
  private static final double HELICOPTER_BODY_POSITION_X =
      HALF_GAME_WIDTH-15;
  private static final double HELICOPTER_BODY_POSITION_Y =
      HALF_HELIPAD_POS - 50;
  private static final double HELICOPTER_BLADE_POSITION_X =
      HALF_GAME_WIDTH-2;
  private static final double HELICOPTER_BLADE_POSITION_Y =
      HALF_HELIPAD_POS - 50;
  private static final double HELICOPTER_TEXT_POSITION_X =
      HALF_GAME_WIDTH - 35;
  private static final double HELICOPTER_TEXT_POSITION_Y =
      HALF_HELIPAD_POS - 55;
  private static final double BLADE_MAX_ROTATION = 50;
  private static final double BLADE_MIN_ROTATION = 0;
  private double heading;
  private double speed;
  private int fuel;
  private double theta;
  private final double speedIncrease = .1;
  private final double headingChange = 5;
  private GameText helicopterText;
  private HelicopterBody helicopterBody;
  private HelicopterBlade helicopterBlade;
  private HelicopterState helicopterState = new Off();

  public Helicopter(int initialFuel){
    heading = INITIAL_HEADING;
    speed = INITIAL_SPEED;
    fuel = initialFuel;
    helicopterBody = new HelicopterBody();
    helicopterText = new GameText();
    helicopterBlade = new HelicopterBlade();
    helicopterBlade.rotateBlade();

    helicopterText.positionText(HELICOPTER_TEXT_POSITION_X,
        HELICOPTER_TEXT_POSITION_Y);
    helicopterText.setText(String.format("%9d", fuel));
    helicopterText.setFill(Color.YELLOW);

    helicopterBody.positionHelicopterBody(HELICOPTER_BODY_POSITION_X,
        HELICOPTER_BODY_POSITION_Y);
    helicopterBlade.positionHelicopterBlade(HELICOPTER_BLADE_POSITION_X,
        HELICOPTER_BLADE_POSITION_Y);
    this.getChildren().addAll(helicopterBody, helicopterBlade, helicopterText);
  }
  public void update(){
    if(helicopterState.toString().equals("Ready")){
      helicopterText.setText(String.format("%9d", fuel));
      updateFuel();
      move();
    }
    else if(helicopterState.toString().equals("Starting")){
      if(helicopterBlade.getBladeRotationSpeed() < BLADE_MAX_ROTATION){
        helicopterBlade.spinUpBlade();
      }
      else{
        helicopterState = new Ready();
      }
    }
    else if(helicopterState.toString().equals("Stopping")) {
      if(helicopterBlade.getBladeRotationSpeed() > BLADE_MIN_ROTATION){
        helicopterBlade.spinDownBlade();
      }
      else{
        helicopterState = new Off();
      }
    }
  }
  void move(){
    this.setTranslateX(getTranslateX() + speed * Math.cos(toRadians(theta)));
    this.setTranslateY(getTranslateY() + speed * Math.sin(toRadians(theta)));
    theta = 90 - heading;
    helicopterBody.setRotate(360 - heading);
  }
  boolean isHelicopterMaxSpeed(){
    return speed >= MAX_COPTER_SPEED;
  }
  boolean isHelicopterMinSpeed(){
    return speed <= MIN_COPTER_SPEED;
  }
  void updateFuel(){
    fuel -= (abs(1 + speed));
  }
  void headLeft(){
    heading = (heading%360) - headingChange;
  }
  void headRight(){
    heading = (heading%360) + headingChange;
  }
  void increaseSpeed(){
    speed += speedIncrease;
  }
  void decreaseSpeed(){
    speed -= speedIncrease;
  }
  int getFuel(){
    return fuel;
  }
  String getHelicopterStateName(){
    return helicopterState.toString();
  }
  public HelicopterState getState() {
    return helicopterState;
  }
  void setState(HelicopterState state) {
    this.helicopterState = state;
  }
  void toggleIgnition(Helicopter helicopter){
    helicopterState.toggleIgnition(helicopter);
  }
}
class HelicopterBody extends GameObject{
  private File helicopterBodyFile;
  private ImageView helicopterBody;
  public HelicopterBody() {
    helicopterBodyFile = new File("Images/Vector 1 2.png");
    Image image = new Image(helicopterBodyFile.toURI().toString());
    helicopterBody = new ImageView(image);
    helicopterBody.setPreserveRatio(true);
    helicopterBody.setScaleY(-1);
    helicopterBody.setFitHeight(90);
    this.getChildren().add(helicopterBody);
  }
  void positionHelicopterBody(double x, double y){
    helicopterBody.setTranslateX(x);
    helicopterBody.setTranslateY(y);
  }
}

class HelicopterBlade extends GameObject{
  private File helicopterBladeFile;
  private ImageView helicopterBlade;
  private AnimationTimer rotateBlade;
  private static double elapsedTime = 0;
  private double rotationSpeed = 0;
  private static final double ROTATION_INCREMENT = .1;
  public HelicopterBlade(){
    helicopterBladeFile = new File("Images/Helicopter Blade.png");
    Image image = new Image(helicopterBladeFile.toURI().toString());
    helicopterBlade = new ImageView(image);
    helicopterBlade.setPreserveRatio(true);
    helicopterBlade.setScaleY(-1);
    helicopterBlade.setFitHeight(90);
    this.getChildren().add(helicopterBlade);
  }
  void positionHelicopterBlade(double x, double y){
    helicopterBlade.setTranslateX(x);
    helicopterBlade.setTranslateY(y);
  }
  void rotateBlade(){
    rotateBlade = new AnimationTimer() {
      private double old = -1;
      @Override
      public void handle(long now) {
        if (old < 0) old = now;
        double delta = (now - old) / 1e9;
        old = now;
        elapsedTime += delta;
        helicopterBlade.setRotate(helicopterBlade.getRotate() + rotationSpeed);
      }
    };
    rotateBlade.start();
  }
  double getBladeRotationSpeed(){
    return rotationSpeed;
  }
  void spinUpBlade(){
    rotationSpeed += ROTATION_INCREMENT;
  }
  void spinDownBlade(){
    rotationSpeed -= ROTATION_INCREMENT;
  }

}
interface HelicopterState {
  void toggleIgnition(Helicopter helicopter);
  String toString();
  //void seedingCloud();
  void increaseSpeed(Helicopter helicopter);
  void decreaseSpeed(Helicopter helicopter);
  void headLeft(Helicopter helicopter);
  void headRight(Helicopter helicopter);
}
class Off implements HelicopterState{
  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Starting());
  }
  @Override
  public String toString(){
    return "Off";
  }
  @Override
  public void increaseSpeed(Helicopter helicopter){
  }
  @Override
  public void decreaseSpeed(Helicopter helicopter){
  }
  @Override
  public void headLeft(Helicopter helicopter){}
  @Override
  public void headRight(Helicopter helicopter){}
}

class Ready implements HelicopterState{
  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Stopping());
  }
  @Override
  public String toString(){
    return "Ready";
  }
  @Override
  public void increaseSpeed(Helicopter helicopter){
    if(!helicopter.isHelicopterMaxSpeed()){
      helicopter.increaseSpeed();
    }
  }
  @Override
  public void decreaseSpeed(Helicopter helicopter){
    if(!helicopter.isHelicopterMinSpeed()){
      helicopter.decreaseSpeed();
    }
  }
  @Override
  public void headLeft(Helicopter helicopter){
    helicopter.headLeft();
  }
  @Override
  public void headRight(Helicopter helicopter){
    helicopter.headRight();
  }
}

class Starting implements HelicopterState{
  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Stopping());
  }
  @Override
  public String toString(){
    return "Starting";
  }
  @Override
  public void increaseSpeed(Helicopter helicopter){}
  @Override
  public void decreaseSpeed(Helicopter helicopter){}
  @Override
  public void headLeft(Helicopter helicopter){}
  @Override
  public void headRight(Helicopter helicopter){}

}

class Stopping implements HelicopterState{
//  public Stopping(Helicopter helicopter){
//
//  }
  @Override
  public void toggleIgnition(Helicopter helicopter) {
    helicopter.setState(new Starting());
  }
  @Override
  public String toString(){
    return "Stopping";
  }
  @Override
  public void increaseSpeed(Helicopter helicopter){}
  @Override
  public void decreaseSpeed(Helicopter helicopter){}
  @Override
  public void headLeft(Helicopter helicopter){}
  @Override
  public void headRight(Helicopter helicopter){}
}


class GameText extends GameObject{
  private Text text;
  public GameText(String textString){
    text = new Text(textString);
    text.setScaleY(-1);
    text.setFont(Font.font(15));
    this.getChildren().add(text);
  }
  public GameText(){
    this("");
  }
  void setText(String textString){
    text.setText(textString);
  }
  void setFill(Color color){
    text.setFill(color);
  }
  void positionText(double x, double y){
    text.setTranslateX(x);
    text.setTranslateY(y);
  }
}

