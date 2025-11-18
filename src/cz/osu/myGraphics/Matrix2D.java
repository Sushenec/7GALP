package cz.osu.myGraphics;

public class Matrix2D {
    public double m00, m01, m02;
    public double m10, m11, m12;
    public double m20, m21, m22;

    public Matrix2D(){
        m00 = 0;
        m01 = 0;
        m02 = 0;

        m10 = 0;
        m11 = 0;
        m12 = 0;

        m20 = 0;
        m21 = 0;
        m22 = 0;
    }

    public static Matrix2D getIdentityMatrix(){
        Matrix2D identity = new Matrix2D();

        identity.m00 = 1;
        identity.m11 = 1;
        identity.m22 = 1;

        return identity;
    }

    public static Matrix2D getTranslationMatrix(double x, double y){
        Matrix2D translation = Matrix2D.getIdentityMatrix();

        translation.m02 = x;
        translation.m12 = y;

        return translation;
    }

    public static Matrix2D getRotationMatrix(double degree){
        Matrix2D rotation = new Matrix2D();

        double radians = Math.toRadians(degree);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        rotation.m00 = cos;
        rotation.m01 = -sin;

        rotation.m10 = sin;
        rotation.m11 = cos;

        rotation.m22 = 1;


        return rotation;
    }

    public static Matrix2D getScaleMatrix(double ratio){
        Matrix2D scale = new Matrix2D();

        scale.m00 = ratio;
        scale.m11 = ratio;
        scale.m22 = 1;

        return scale;
    }

    public Point2D multiply(Point2D point){
        Point2D newPoint = new Point2D();

        newPoint.x = m00 * point.x + m01 * point.y + m02 * point.w;
        newPoint.y = m10 * point.x + m11 * point.y + m12 * point.w;
        newPoint.w = m20 * point.x + m21 * point.y + m22 * point.w;

        return newPoint;
    }

    public Matrix2D multiply(Matrix2D matrix){
        Matrix2D newMatrix = new Matrix2D();

        newMatrix.m00 = m00 * matrix.m00 + m01 * matrix.m10 + m02 * matrix.m20;
        newMatrix.m10 = m10 * matrix.m00 + m11 * matrix.m10 + m12 * matrix.m20;
        newMatrix.m20 = m20 * matrix.m00 + m21 * matrix.m10 + m22 * matrix.m20;

        newMatrix.m01 = m00 * matrix.m01 + m01 * matrix.m11 + m02 * matrix.m21;
        newMatrix.m11 = m10 * matrix.m01 + m11 * matrix.m11 + m12 * matrix.m21;
        newMatrix.m21 = m20 * matrix.m01 + m21 * matrix.m11 + m22 * matrix.m21;

        newMatrix.m02 = m00 * matrix.m02 + m01 * matrix.m12 + m02 * matrix.m22;
        newMatrix.m12 = m10 * matrix.m02 + m11 * matrix.m12 + m12 * matrix.m22;
        newMatrix.m22 = m20 * matrix.m02 + m21 * matrix.m12 + m22 * matrix.m22;

        return newMatrix;
    }
}
