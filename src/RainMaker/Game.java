package RainMaker;

import RainMaker.GameObjects.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;

import java.io.File;
import java.util.Random;

public class Game extends Pane implements Updatable {
  public static final int MIN_CLOUD_FULLNESS = 30;
  public static final int MAX_POND_FULLNESS = 100;
  public static final int INITIAL_NUM_POND = 3;
  public static final int INITIAL_NUM_BLIMP = 3;
  public static final int CHANCE_OF_BLIMP_SPAWN = 1000;
  public static final int MAX_NUMBER_BLIMPS = 6;
  public static final int ONE_LESS_THAN_MAX = 5;
  public static final int CHANCE_OF_CLOUD_SPAWN = 100;
  public static final int MAX_CLOUD_FULLNESS = 100;
  public static final int POND_WIN_CONDITION = 80;
  public static final double SPEED_MARGIN_OF_ERROR = .2;
  public static final double CLOUD_DIAMETER_FACTOR = 2.5;
  private static double elapsedTime = 0;
  private static double pastTime = 0;
  private static boolean fireEventInActive = true;
  private static final int INITIAL_FUEL = 250;
  private static final int REFUELING_AMOUNT = 50;
  private static final double SECONDS_CONVERSION_FACTOR = 1e9;
  private static final int MAX_WIND_REGENERATION_TIME = 25;
  private static final int MIN_WIND_REGENERATION_TIME = 10;
  private Helipad gameHelipad;
  private Helicopter gameHelicopter;
  private AnimationTimer game;
  private Ponds gamePonds;
  private Clouds gameClouds;
  private Wind gameWind;
  private Blimps gameBlimps;
  private static final Random RAND = new Random();
  private static final int MAX_NUMBER_OF_CLOUDS = 7;
  private static final int MIN_NUMBER_OF_CLOUDS = 3;
  private static int numberOfCloudsOnScreen = 0;
  private static int numberOfBlimpsOnScreen = 0;
  private static final StringBuilder GAME_OVER_STRING_BUILDER = new StringBuilder();
  private static Alert alert;
  private static Game gameInstance;
  private double randomTime;

  public static synchronized Game getInstance() {
    if (gameInstance == null) {
      gameInstance = new Game();
    }
    return gameInstance;
  }

  public Game() {
    //Image is taken from https://www.vectorstock.com/
    //Artist alexzel
    //All credit and rights belong to vectorstock and alexzel
    File backgroundFile = new File("Images/background3.jpg");
    Image backgroundImage = new Image(backgroundFile.toURI().toString());
    ImagePattern backgroundPattern = new ImagePattern(backgroundImage);

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

  void init() {
    game = new AnimationTimer() {
      private double old = -1;

      @Override
      public void handle(long now) {
        if (old < 0) old = now;
        double delta = (now - old) / SECONDS_CONVERSION_FACTOR;
        old = now;
        elapsedTime += delta;
        update();
        gameWinLoseRestart();
      }
    };
    game.start();
  }

  public void update() {
    for (Cloud cloud : gameClouds) {
      if (cloud.isFullnessOverX(MIN_CLOUD_FULLNESS)) {
        for (Pond pond : gamePonds) {
          if (pond.isPondRadiusUnderX(MAX_POND_FULLNESS) && isCloudNearPond(cloud,
              pond)) {
            pond.update();
          }
        }
      }
      cloud.update();
      if (isCloudOffScreen(cloud)) {
        cloud.getState().toggleState(cloud);
        numberOfCloudsOnScreen--;
      }
    }
    for (Blimp gameBlimp : gameBlimps) {
      gameBlimp.update();
      if(isBlimpOffScreen(gameBlimp)){
        gameBlimp.getState().toggleState(gameBlimp);
        numberOfBlimpsOnScreen--;
      }
    }
    gameHelicopter.update();
    shouldCloudBeGenerated();
    shouldWindSpeedChange();
    shouldBlimpBeGenerated();

  }

  void shouldWindSpeedChange() {
    randomTime = RAND.nextDouble(MAX_WIND_REGENERATION_TIME -
        MIN_WIND_REGENERATION_TIME) + MIN_WIND_REGENERATION_TIME;
    if(elapsedTime - pastTime > randomTime) {
      pastTime = elapsedTime;
      gameWind.notifyNewWindSpeed();
      gameWind.update();
    }
  }

  void instantiateGameObjects() {
    int initialNumberOfClouds = RAND.nextInt(
        MAX_NUMBER_OF_CLOUDS - MIN_NUMBER_OF_CLOUDS) + MIN_NUMBER_OF_CLOUDS;

    gamePonds = new Ponds();
    gameClouds = new Clouds();
    gameHelipad = new Helipad();
    gameBlimps = new Blimps();
    gameHelicopter = new Helicopter(INITIAL_FUEL);
    gameWind = new Wind();
    for (int i = 0; i < INITIAL_NUM_POND; i++) {
      Pond gamePond = new Pond();
      gamePonds.addPondToList(gamePond);
    }
    for (int i = 0; i < initialNumberOfClouds; i++) {
      makeNewOffScreenCloud();
    }
    for(int i = 0; i < Game.INITIAL_NUM_BLIMP; i++){
      makeNewBlimp();
    }
    this.getChildren().addAll(gamePonds, gameClouds, gameHelipad,
        gameBlimps, gameHelicopter, gameWind);
  }

  void makeNewBlimp() {
    Blimp gameBlimp = new Blimp();
    for (Blimp blimp : gameBlimps) {
      while(isIntersect(blimp, gameBlimp)){
        gameBlimp.repositionBlimp();
      }
    }
    gameBlimps.addBlimpToList(gameBlimp);
    numberOfBlimpsOnScreen++;
  }
  void shouldBlimpBeGenerated(){
    int randomNumber = RAND.nextInt(CHANCE_OF_BLIMP_SPAWN);
    if(randomNumber < 2){
      if(gameBlimps.getListSize() < MAX_NUMBER_BLIMPS){
        makeNewBlimp();
      }else if(numberOfBlimpsOnScreen < ONE_LESS_THAN_MAX){
        for (Blimp gameBlimp : gameBlimps){
          if(gameBlimp.getState().toString().equals("OffScreen")) {
            gameBlimp.getState().repositionBlimp(gameBlimp);
            break;
          }
        }
      }
    }
  }
  void shouldCloudBeGenerated() {
    int randomNumber = RAND.nextInt(CHANCE_OF_CLOUD_SPAWN);
    if (numberOfCloudsOnScreen < MAX_NUMBER_OF_CLOUDS && randomNumber < 2) {
      if (gameClouds.getListSize() < MAX_NUMBER_OF_CLOUDS) {
        makeNewOnScreenCloud();
      } else {
        for (Cloud cloud : gameClouds) {
          if(cloud.getState().toString().equals("OffScreen")) {
            cloud.getState().repositionCloud(cloud);
            break;
          }
        }
      }
    }
  }
  void makeNewOffScreenCloud() {
    Cloud gameCloud = new Cloud(gameWind.getWindSpeed());
    gameClouds.addCloudToList(gameCloud);
    numberOfCloudsOnScreen++;
    gameWind.attach(gameCloud);
  }

  void makeNewOnScreenCloud() {
    Cloud gameCloud = new Cloud(gameWind.getWindSpeed());
    gameCloud.repositionCloud();
    gameClouds.addCloudToList(gameCloud);
    numberOfCloudsOnScreen++;
  }

  void headLeft() {
    gameHelicopter.getState().headLeft(gameHelicopter);
  }

  void headRight() {
    gameHelicopter.getState().headRight(gameHelicopter);
  }

  void increaseSpeed() {
    gameHelicopter.getState().increaseSpeed(gameHelicopter);
  }

  void decreaseSpeed() {
    gameHelicopter.getState().decreaseSpeed(gameHelicopter);
  }

  void flipIgnition() {
    if (isIntersect(gameHelicopter, gameHelipad)) {
      gameHelicopter.getState().toggleIgnition(gameHelicopter);
    }
  }

  void fireEvent() {
    fireEventInActive = !fireEventInActive;
    for (Cloud gameCloud : gameClouds) {
      if (isIntersect(gameHelicopter, gameCloud)) {
        if (!gameCloud.isFullnessOverX(MAX_CLOUD_FULLNESS)) {
          gameCloud.colorChange();
        }
      }
    }
    fireEventInActive = !fireEventInActive;
  }

  void gameWinLoseRestart() {
    if (gamePonds.isFullnessOfAllPondsOverX(POND_WIN_CONDITION) || gameHelicopter.getFuel()
        < 1) {
      if (gamePonds.isFullnessOfAllPondsOverX(POND_WIN_CONDITION) && isIntersect(gameHelicopter,
          gameHelipad) && gameHelicopter.getHelicopterStateName().equals(
          "Off")) {
        stopAnimationLoops();
        addText("Congratulations! You won with a score of " +
            gameHelicopter.getFuel() + " Would you like to play " +
            "again?");
        showAlert();
      } else if(gameHelicopter.getFuel() < 1) {
        stopAnimationLoops();
        addText("Your helicopter has run out of fuel. " +
            "Would you like to try again?");
        showAlert();
      }

    }
  }
  void stopAnimationLoops(){
    game.stop();
    gameHelicopter.stopBlade();
    for (Cloud cloud : gameClouds) {
      cloud.stopClouds();
    }
    for (Blimp blimp : gameBlimps) {
      blimp.stopBlimp();
    }
  }
  void showAlert(){
    alert = new Alert(Alert.AlertType.CONFIRMATION, GAME_OVER_STRING_BUILDER.toString(),
        ButtonType.YES, ButtonType.NO);
    alert.setOnHidden(evt -> {
      if (alert.getResult() == ButtonType.YES) {
        this.getChildren().clear();
        instantiateGameObjects();
        this.init();
      } else
        Platform.exit();
    });
    alert.show();
  }

  void hardRestart() {
    game.stop();
    this.getChildren().clear();
    numberOfCloudsOnScreen = 0;
    numberOfBlimpsOnScreen = 0;
    instantiateGameObjects();
    this.init();
  }
  void refuel(){
    for(Blimp gameBlimp : gameBlimps) {
      if(isIntersect(gameBlimp, gameHelicopter) &&
          isHeliBlimpSpeedClose(gameBlimp) &&
          gameHelicopter.getFuel() <= INITIAL_FUEL){
        gameHelicopter.incrementFuel(REFUELING_AMOUNT);
        gameBlimp.setBlimpFuel(REFUELING_AMOUNT);
      }
    }
  }
  boolean isHeliBlimpSpeedClose(Blimp blimp){
    return blimp.getBlimpSpeed() < gameHelicopter.getSpeed() + SPEED_MARGIN_OF_ERROR &&
        blimp.getBlimpSpeed() > gameHelicopter.getSpeed() - SPEED_MARGIN_OF_ERROR;
  }
  void addText(String s) {
    GAME_OVER_STRING_BUILDER.setLength(0);
    GAME_OVER_STRING_BUILDER.append(s);
  }

  boolean isIntersect(GameObject object1, GameObject object2) {
    return object1.getBoundsInParent().intersects(object2.getBoundsInParent());
  }

  boolean isCloudNearPond(Cloud cloud, Pond pond) {
    double distance;
    distance =
        Math.hypot(Math.abs(cloud.getTranslateX() - pond.getTranslateX()),
            Math.abs(cloud.getTranslateY() - pond.getTranslateY()));

    return cloud.getCloudDiameter() * CLOUD_DIAMETER_FACTOR > distance;
  }

  boolean isCloudOffScreen(Cloud cloud) {
    return cloud.getTranslateX() - cloud.getCloudWidth() > GameApp.getGameWidth();
  }
  boolean isBlimpOffScreen(Blimp blimp){
    return blimp.getTranslateX() - blimp.getBlimpWidth() >
        GameApp.getGameWidth();
  }

  public static boolean getFireEvent() {
    return fireEventInActive;
  }
}
