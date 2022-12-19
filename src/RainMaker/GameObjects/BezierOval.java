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

class BezierOval extends Group{
  private static final Random RAND = new Random();
  public static final int RADIUS_FACTOR_MAX = 2;
  public static final double RADIUS_FACTOR_MIN = 1.5;
  private final Ellipse ellipse;
  private final double initialTheta = 0;
  private double currentTheta = initialTheta;
  private final int maxRadius = 60;
  private final int minRadius = 50;
  private final int minorMaxRadius = 35;
  private final int minorMinRadius = 30;
  private final double maxThetaChange = PI / 3;
  private final double minThetaChange = PI / 4;
  private final int majorRadius =
      RAND.nextInt(maxRadius - minRadius) + minRadius;
  private final int minorRadius =
      RAND.nextInt(minorMaxRadius - minorMinRadius) + minorMinRadius;
  private final double randomThetaIncrease =
      RAND.nextDouble(maxThetaChange - minThetaChange) + minThetaChange;
  private final List<QuadCurve> bezierCurveList;

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
  }

  void generateBezierCurve() {
    double startPointX = majorRadius * cos(initialTheta);
    double startPointY = minorRadius * sin(initialTheta);
    Point2D startPoint = new Point2D(startPointX, startPointY);
    while (currentTheta < initialTheta + (2 * PI)) {
      double lastTheta = currentTheta;
      currentTheta += randomThetaIncrease;
      double radiusFactor =
          RAND.nextDouble(RADIUS_FACTOR_MAX - RADIUS_FACTOR_MIN)
              + RADIUS_FACTOR_MIN;

      double controlX =
          majorRadius * cos((lastTheta + currentTheta) / 2) * radiusFactor;
      double controlY = minorRadius * sin((lastTheta + currentTheta) / 2)
          * radiusFactor;

      //radiusFractor needs to be randomized
      Point2D controlPoint = new Point2D(controlX, controlY);

      startPointX = majorRadius * cos(currentTheta);
      startPointY = minorRadius * sin(currentTheta);
      Point2D endPoint = new Point2D(startPointX, startPointY);
      QuadCurve bezierCurve = new QuadCurve(startPoint.getX(), startPoint.getY(),
          controlPoint.getX(), controlPoint.getY(), endPoint.getX(),
          endPoint.getY());
      startPoint = endPoint;
      bezierCurve.setFill(Color.WHITE);
      bezierCurve.setStroke(Color.BLACK);
      this.getChildren().add(bezierCurve);
      bezierCurveList.add(bezierCurve);
    }
  }
  int getBezierWidth(){
    return (int)this.getLayoutBounds().getWidth() - 5;
  }
  int getBezierHeight(){
    return (int)this.getLayoutBounds().getHeight();
  }
}
