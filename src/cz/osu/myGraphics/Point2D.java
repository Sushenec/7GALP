package cz.osu.myGraphics;

import java.awt.*;

public class Point2D {
    public double x;
    public double y;
    public double w;

    public Point2D() {
        x = 0;
        y = 0;
        w = 1;
    }

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
        this.w = 1;
    }

    public Point getPoint(){
        int newX = (int)Math.round(this.x / this.w);
        int newY = (int)Math.round(this.y / this.w);

        return new Point(newX, newY);
    }

    public Point2D multiply(double multiplicator){
        Point2D newPoint = new Point2D(this.x, this.y);
        newPoint.x *= multiplicator;
        newPoint.y *= multiplicator;
        return newPoint;
    }

    public Point2D add(Point2D a){
        Point2D newPoint = new Point2D(this.x, this.y);
        newPoint.x += a.x;
        newPoint.y += a.y;
        return newPoint;
    }

    @Override
    public String toString() {
        return "(" + x + " ;" + y + " ;" + w + ")";
    }
}
