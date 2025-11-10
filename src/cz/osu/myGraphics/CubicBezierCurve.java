package cz.osu.myGraphics;

public class CubicBezierCurve {
    public Point p0;
    public Point p1;
    public Point p2;
    public Point p3;

    public Point a0 = new Point();
    public Point a1 = new Point();
    public Point a2 = new Point();
    public Point a3 = new Point();


    public CubicBezierCurve(Point p0, Point p1, Point p2, Point p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        preCompute();
    }

    private void preCompute(){
        this.a0.x = p0.x;
        this.a0.y = p0.y;

        this.a1.x = (-3) * p0.x + 3 * p1.x;
        this.a1.y = (-3) * p0.y + 3 * p1.y;

        this.a2.x = 3 * p0.x + (-6) * p1.x + 3 * p2.x;
        this.a2.y = 3 * p0.y + (-6) * p1.y + 3 * p2.y;

        this.a3.x = -p0.x + 3 * p1.x + (-3) * p2.x + p3.x;
        this.a3.y = -p0.y + 3 * p1.y + (-3) * p2.y + p3.y;
    }

    public Point getPoint(double t){
        Point result = new Point();

        double tt = t * t;
        double ttt = tt * t;

        result.x = a0.x + t * a1.x + tt * a2.x + ttt * a3.x;
        result.y = a0.y + t * a1.y + tt * a2.y + ttt * a3.y;

        return result;
    }
}
