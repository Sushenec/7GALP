package cz.osu.myGraphics;

public class Point2D {
    public double x;
    public double y;
    public double w;

    public Point2D() {
        x = 0;
        y = 0;
        w = 0;
    }

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
        this.w = 0;
    }

    @Override
    public String toString() {
        return "(" + x + " ;" + y + " ;" + w + ")";
    }
}
