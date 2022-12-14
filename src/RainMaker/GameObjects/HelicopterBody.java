package RainMaker.GameObjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

class HelicopterBody extends GameObject {
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

  void positionHelicopterBody(double x, double y) {
    helicopterBody.setTranslateX(x);
    helicopterBody.setTranslateY(y);
  }
}
