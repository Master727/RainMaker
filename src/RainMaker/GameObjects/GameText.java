package RainMaker.GameObjects;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class GameText extends GameObject {
  private final Text text;

  public GameText(String textString) {
    text = new Text(textString);
    text.setScaleY(-1);
    text.setFont(Font.font(15));
    this.getChildren().add(text);
  }

  public GameText() {
    this("");
  }

  void setText(String textString) {
    text.setText(textString);
  }

  void setFill(Color color) {
    text.setFill(color);
  }
  void setFont(int fontSize){
    text.setFont(Font.font(fontSize));
  }

  void positionText(double x, double y) {
    text.setTranslateX(x);
    text.setTranslateY(y);
  }
}
