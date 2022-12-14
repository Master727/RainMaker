package RainMaker.GameObjects;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.QuadCurve;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;

class BezierOval extends Group {
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
  private final double maxThetaChange = PI / 3;
  private final double minThetaChange = PI / 4;
  private final int majorRadius =
      RAND.nextInt(maxRadius - minRadius) + minRadius;
  private final int minorRadius =
      RAND.nextInt(minorMaxRadius - minorMinRadius) + minorMinRadius;
  private double randomThetaIncrease =
      RAND.nextDouble(maxThetaChange - minThetaChange) + minThetaChange;
  private List<QuadCurve> bezierCurveList;

  public BezierOval() {
    ellipse = new Ellipse(majorRadius, minorRadius);
    bezierCurveList = new LinkedList<QuadCurve>();
    generateBezierCurve();
    this.getChildren().add(ellipse);
  }

  void setFill(Color color) {
    for (QuadCurve bezierCurve : bezierCurveList) {
      bezierCurve.setFill(color);
    }
    ellipse.setFill(color);
//    for ((Shape) Node obj : this.getChildren() ) {
//      set color of all the bezier curve
//    }
  }

  void generateBezierCurve() {
    double startPointX = majorRadius * cos(initailTheta);
    double startPointY = minorRadius * sin(initailTheta);
    startPoint = new Point2D(startPointX, startPointY);
    while (currentTheta < initailTheta + (2 * PI)) {
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
          majorRadius * cos((lastTheta + currentTheta) / 2) * radiusFactor;
      double cy = minorRadius * sin((lastTheta + currentTheta) / 2) * radiusFactor;

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
      bezierCurveList.add(bezierCurve);
    }
  }
}
