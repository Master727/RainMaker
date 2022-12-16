package RainMaker.GameObjects;

import RainMaker.GameApp;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class Wind extends GameObject implements Observable, Updatable{

  private static final int GAME_HEIGHT = GameApp.getGameHeight();
  private static final int GAME_WIDTH = GameApp.getGameWidth();
  private static final Random RAND = new Random();
  private static final double WIND_SPEED = .5;
  private static final double MAX_WIND_SPEED = 1.5;
  private static double windSpeed = WIND_SPEED;
  private List<Observer> observers;
  private GameText windText;
  public Wind(){
    observers = new LinkedList<>();
    windSpeed = WIND_SPEED;

    windText = new GameText();
    windText.setText(String.format("Wind Speed: %.2f", windSpeed));
    windText.setFill(Color.BLACK);
    int textPositionY = GAME_HEIGHT - 10;
    int textPositionX = GAME_WIDTH / 2 - 70;
    windText.positionText(textPositionX, textPositionY);
    windText.setFont(20);

    this.getChildren().add(windText);
  }
  public double getWindSpeed(){
    return windSpeed;
  }
  @Override
  public void attach(Observer o) {
    observers.add(o);
  }

  @Override
  public void detach(Observer o) {
    observers.remove(o);
  }

  @Override
  public void notifyNewWindSpeed() {
    windSpeed = abs(windSpeed + RAND.nextGaussian(.3, .2));
    if(windSpeed >= MAX_WIND_SPEED){
      windSpeed = abs(windSpeed + RAND.nextGaussian(-.3, .2));
    }
    for (Observer observer : observers) {
      observer.updateWindSpeed(windSpeed);
    }

  }
  @Override
  public void update() {
    windText.setText(String.format("Wind Speed: %.2f", windSpeed));
  }
}
