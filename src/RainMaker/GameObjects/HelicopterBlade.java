package RainMaker.GameObjects;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

class HelicopterBlade extends GameObject {
  private File helicopterBladeFile;
  private ImageView helicopterBlade;
  private AnimationTimer rotateBlade;
  private static double elapsedTime = 0;
  private double rotationSpeed = 0;
  private static final double ROTATION_INCREMENT = .1;

  public HelicopterBlade() {
    helicopterBladeFile = new File("Images/Helicopter Blade.png");
    Image image = new Image(helicopterBladeFile.toURI().toString());
    helicopterBlade = new ImageView(image);
    helicopterBlade.setPreserveRatio(true);
    helicopterBlade.setScaleY(-1);
    helicopterBlade.setFitHeight(90);
    this.getChildren().add(helicopterBlade);
  }

  void positionHelicopterBlade(double x, double y) {
    helicopterBlade.setTranslateX(x);
    helicopterBlade.setTranslateY(y);
  }

  void rotateBlade() {
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

  double getBladeRotationSpeed() {
    return rotationSpeed;
  }

  void spinUpBlade() {
    rotationSpeed += ROTATION_INCREMENT;
  }

  void spinDownBlade() {
    rotationSpeed -= ROTATION_INCREMENT;
  }

}
