package RainMaker.GameObjects;

import RainMaker.GameApp;
import javafx.scene.paint.Color;

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;

public class Helicopter extends GameObject implements Updatable {
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
      HALF_GAME_WIDTH - 15;
  private static final double HELICOPTER_BODY_POSITION_Y =
      HALF_HELIPAD_POS - 50;
  private static final double HELICOPTER_BLADE_POSITION_X =
      HALF_GAME_WIDTH - 2;
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
  private boolean fuelIsNotBeingChanged = false;
  private double theta;
  private final double speedIncrease = .1;
  private final double headingChange = 5;
  private static final int HELICOPTER_POSITION_CORRECTIONS = 90;
  private static final int FULL_CIRCLE_DEGREES = 360;
  private final GameText helicopterText;
  private final HelicopterBody helicopterBody;
  private final HelicopterBlade helicopterBlade;
  private HelicopterState helicopterState = new Off();
  private static final int FONT_SIZE = 15;

  public Helicopter(int initialFuel) {
    heading = INITIAL_HEADING;
    speed = INITIAL_SPEED;
    fuel = initialFuel;
    helicopterBody = new HelicopterBody();
    helicopterText = new GameText();
    helicopterBlade = new HelicopterBlade();
    helicopterBlade.rotateBlade();

    helicopterText.positionText(HELICOPTER_TEXT_POSITION_X,
        HELICOPTER_TEXT_POSITION_Y);
    helicopterText.setText(String.format("F: %6d", fuel));
    helicopterText.setFont(FONT_SIZE);
    helicopterText.setFill(Color.YELLOW);

    helicopterBody.positionHelicopterBody(HELICOPTER_BODY_POSITION_X,
        HELICOPTER_BODY_POSITION_Y);
    helicopterBlade.positionHelicopterBlade(HELICOPTER_BLADE_POSITION_X,
        HELICOPTER_BLADE_POSITION_Y);
    this.getChildren().addAll(helicopterBody, helicopterBlade, helicopterText);
  }

  public void update() {
    if (helicopterState.toString().equals("Ready")) {
      helicopterText.setText(String.format("F: %6d", fuel));
      decrementFuel();
      move();
    } else if (helicopterState.toString().equals("Starting")) {
      if (helicopterBlade.getBladeRotationSpeed() < BLADE_MAX_ROTATION) {
        helicopterBlade.spinUpBlade();
      } else {
        helicopterState = new Ready();
      }
    } else if (helicopterState.toString().equals("Stopping")) {
      if (helicopterBlade.getBladeRotationSpeed() > BLADE_MIN_ROTATION) {
        helicopterBlade.spinDownBlade();
      } else {
        helicopterState = new Off();
      }
    }
  }

  void move() {
    this.setTranslateX(getTranslateX() + speed * Math.cos(toRadians(theta)));
    this.setTranslateY(getTranslateY() + speed * Math.sin(toRadians(theta)));
    theta = HELICOPTER_POSITION_CORRECTIONS - heading;
    helicopterBody.setRotate(FULL_CIRCLE_DEGREES - heading);
  }

  boolean isHelicopterMaxSpeed() {
    return speed >= MAX_COPTER_SPEED;
  }

  boolean isHelicopterMinSpeed() {
    return speed <= MIN_COPTER_SPEED;
  }

  public void decrementFuel() {
    fuelIsNotBeingChanged = !fuelIsNotBeingChanged;
    if(fuelIsNotBeingChanged){
      fuel -= (abs(1 + speed));
    }
    fuelIsNotBeingChanged = !fuelIsNotBeingChanged;
  }
  public void incrementFuel(int refuelingAmount) {
    fuelIsNotBeingChanged = !fuelIsNotBeingChanged;
    if(fuelIsNotBeingChanged){
      fuel += refuelingAmount;
    }
    fuelIsNotBeingChanged = !fuelIsNotBeingChanged;
  }

  void headLeft() {
    heading = (heading % FULL_CIRCLE_DEGREES) - headingChange;
  }

  void headRight() {
    heading = (heading % FULL_CIRCLE_DEGREES) + headingChange;
  }

  void increaseSpeed() {
    speed += speedIncrease;
  }

  void decreaseSpeed() {
    speed -= speedIncrease;
  }

  public int getFuel() {
    return fuel;
  }

  public String getHelicopterStateName() {
    return helicopterState.toString();
  }

  public HelicopterState getState() {
    return helicopterState;
  }

  void setState(HelicopterState state) {
    this.helicopterState = state;
  }

  public double getSpeed() {
    return speed;
  }
}
