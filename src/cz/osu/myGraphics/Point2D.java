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

    @Override
    public String toString() {
        return "(" + x + " ;" + y + " ;" + w + ")";
    }
}
