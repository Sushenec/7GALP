package cz.osu.myGraphics;

import cz.osu.main.V_RAM;

public class MyGraphics {

    public static void fill(V_RAM vram, int red, int green, int blue){

        for (int y = 0; y < vram.getHeight(); y++){
            for (int x = 0; x < vram.getWidth(); x++){
                vram.setPixel(x, y, red, green, blue);
            }
        }
    }

    public static void drawRectangle(V_RAM vram, int x, int y, int width, int height, int borderSize ,int red, int green, int blue){

        if(borderSize > (height / 2.0) || borderSize > (width / 2.0)){
            throw new RuntimeException("Border size is too big");
        }

        if(x < 0 || y < 0 || (x + width) > vram.getWidth() || (y + height) > vram.getHeight()){
            throw new RuntimeException("Rectangle is out of bounds of image");
        }

        for(int _x = x; _x < width + x; _x++){
            for(int border = 0; border < borderSize; border++){

                vram.setPixel(_x, y + border, red, green, blue);
                vram.setPixel(_x, y + height - border - 1, red, green, blue); // -1 je protože indexujeme od nuly a height je číslo, které je se počítá od jedničky
            }
        }

        for(int _y = y; _y < height + y; _y++){
            for(int border = 0; border < borderSize; border++){

                vram.setPixel(x + border, _y, red, green ,blue);
                vram.setPixel(x + width - border - 1, _y, red, green, blue); // -1 je protože indexujeme od nuly a height je číslo, které je se počítá od jedničky
            }
        }
    }

    // Changes vram content to grayscale
    public static void grayScale(V_RAM vram){

        for(int y = 0; y < vram.getHeight(); y++){
            for(int x = 0; x < vram.getWidth(); x++){

                RGB pixel = new RGB(vram.getPixel(x,y));

                //Vážený průměr RGB -> lepší vyjádření jasů pro lidksé oko
                int lightness = (int)Math.round(0.299 * pixel.red + 0.587 * pixel.green + 0.114 * pixel.blue);
                lightness = Math.clamp(lightness, 0, 255);

                vram.setPixel(x, y, lightness, lightness, lightness);
            }
        }
    }

    // Inverts colors in image
    public static void invertColors(V_RAM vram){

        for(int y = 0; y < vram.getHeight(); y++){
            for(int x = 0; x < vram.getWidth(); x++){

                RGB pixel = new RGB(vram.getPixel(x, y));

                vram.setPixel(x, y, (255 - pixel.red), (255 - pixel.green), (255 - pixel.blue));

            }
        }
    }

    public static void errorDistributionDithering(V_RAM vram){

        grayScale(vram);

        int threshold = 128;
        double[][] errorDistributionMatrix = //[x][y]
                {{0, 3/8f},
                {3/8f, 2/8f}};//left top is the current pixel

        for(int y = 0; y < vram.getHeight(); y++){
            for(int x = 0; x < vram.getWidth(); x++){

                int lightness = new RGB(vram.getPixel(x, y)).red;
                int error = 0;

                //fixed threshold dithering
                if(lightness < threshold){

                    vram.setPixel(x, y, 0, 0, 0);
                    error = lightness;

                }else{
                    vram.setPixel(x, y, 255, 255, 255);
                    error = lightness - 255;
                }

                //error distributing
                for(int yMatrix = 0; yMatrix < errorDistributionMatrix.length; yMatrix++){
                    for(int xMatrix = 0; xMatrix < errorDistributionMatrix[yMatrix].length; xMatrix++){
                        if(yMatrix == 0 && xMatrix == 0){
                            continue;
                        }

                        int targetX = Math.clamp(x + xMatrix,0 , vram.getWidth() - 1);
                        int targetY = Math.clamp(y + yMatrix,0 , vram.getHeight() - 1);

                        int currentLightness = new RGB(vram.getPixel(targetX, targetY)).red;
                        int adjustedError = (int)Math.round( error * errorDistributionMatrix[yMatrix][xMatrix]);
                        int newLightness = Math.clamp((adjustedError + currentLightness), 0 ,255);

                        vram.setPixel(targetX, targetY, newLightness, newLightness, newLightness);

                    }
                }
            }
        }
    }

    //shifts hue in HSL model by shiftDegree ammount
    public static void hueShift(V_RAM vram, int shiftDegree){
        for(int y = 0; y < vram.getHeight(); y++){
            for(int x = 0; x < vram.getWidth(); x++){

                RGB pixel = new RGB(vram.getPixel(x ,y));
                HSL color = new HSL(pixel);

                color.hue = (color.hue + shiftDegree + 360) % 360;

                RGB newPixel = new RGB(color);
                vram.setPixel(x, y, newPixel.red, newPixel.green, newPixel.blue);
            }
        }
    }

    public static void convolution(V_RAM vram, Kernel kernel){
        V_RAM source = vram.copy();

        for(int y = 0; y < vram.getHeight(); y++) {
            for (int x = 0; x < vram.getWidth(); x++) {

                //kernel calculation
                RGB sum = new RGB(0);
                for(int yK = 0; yK < kernel.height; yK++){
                    for(int xK = 0; xK < kernel.width; xK++){
                        int targetX = x + xK - kernel.width / 2;
                        int targetY = y + yK - kernel.height / 2;

                        targetX = Math.clamp(targetX, 0, source.getWidth() - 1);
                        targetY = Math.clamp(targetY, 0, source.getHeight() - 1);

                        RGB pixel = new RGB(source.getPixel(targetX, targetY));

                        sum.red += kernel.kernel[yK][xK] * pixel.red;
                        sum.green += kernel.kernel[yK][xK] * pixel.green;
                        sum.blue += kernel.kernel[yK][xK] * pixel.blue;
                    }
                }

                sum.red /= kernel.divider;
                sum.green /= kernel.divider;
                sum.blue /= kernel.divider;


                vram.setPixel(x, y, sum.red, sum.green, sum.blue);
            }
        }
    }

    public static V_RAM scaleDown(V_RAM vram, double ratio){
        //ratio = <0,1>
        int scaledWidth = (int)(vram.getWidth() * ratio);
        int scaledHeight = (int)(vram.getHeight() * ratio);
        V_RAM scaledVram = new V_RAM(scaledWidth, scaledHeight);

        int[][][] map = new int[scaledHeight][scaledWidth][4];

        for(int y = 0; y < vram.getHeight(); y++) {
            for (int x = 0; x < vram.getWidth(); x++) {
                int targetX = Math.clamp((int)(x * ratio), 0, scaledWidth - 1);
                int targetY = Math.clamp((int)(y * ratio), 0, scaledHeight - 1);

                map[targetY][targetX][0] += new RGB(vram.getPixel(x, y)).red;
                map[targetY][targetX][1] += new RGB(vram.getPixel(x, y)).green;
                map[targetY][targetX][2] += new RGB(vram.getPixel(x, y)).blue;

                map[targetY][targetX][3]++;

            }
        }

        for(int y = 0; y < scaledHeight; y++){
            for(int x = 0; x <scaledWidth; x++){
                scaledVram.setPixel(x, y, map[y][x][0] / map[y][x][3], map[y][x][1] / map[y][x][3], map[y][x][2] / map[y][x][3]);
            }
        }

        return scaledVram;

    }

    public static V_RAM scaleUp(V_RAM vram, double ratio){
        //ratio = <1, inf>
        //bilinear interpolation
        int scaledWidth = (int)(vram.getWidth() * ratio);
        int scaledHeight = (int)(vram.getHeight() * ratio);
        V_RAM scaledVram = new V_RAM(scaledWidth, scaledHeight);

        for(int y = 0; y < scaledVram.getHeight(); y++){
            for(int x = 0; x < scaledVram.getWidth(); x++){
                double sourceX = x / ratio;
                double sourceY = y / ratio;

                int westSourceX = Math.clamp((int)Math.floor(sourceX), 0, vram.getWidth() - 1);
                int eastSourceX = Math.min(westSourceX + 1, vram.getWidth() - 1);
                int northSourceY = Math.clamp((int)Math.floor(sourceY), 0, vram.getHeight() - 1);
                int southSourceY = Math.min(northSourceY + 1, vram.getHeight() - 1);

                double distanceLeftX = sourceX - westSourceX;
                double distanceUpY = sourceY - northSourceY;

                RGB northWestPixel = new RGB(vram.getPixel(westSourceX, northSourceY));
                RGB northEastPixel = new RGB(vram.getPixel(eastSourceX, northSourceY));
                RGB southWestPixel = new RGB(vram.getPixel(westSourceX, southSourceY));
                RGB southEastPixel = new RGB(vram.getPixel(eastSourceX, southSourceY));

                RGB pixel = new RGB(0);

                pixel.red = (int)(northWestPixel.red * (1 - distanceLeftX) * (1 - distanceUpY) +
                                  northEastPixel.red * distanceLeftX * (1 - distanceUpY) +
                                  southWestPixel.red * (1 - distanceLeftX) * distanceUpY +
                                  southEastPixel.red * distanceLeftX * distanceUpY);

                pixel.green = (int)(northWestPixel.green * (1 - distanceLeftX) * (1 - distanceUpY) +
                                    northEastPixel.green * distanceLeftX * (1 - distanceUpY) +
                                    southWestPixel.green * (1 - distanceLeftX) * distanceUpY +
                                    southEastPixel.green * distanceLeftX * distanceUpY);

                pixel.blue = (int)(northWestPixel.blue * (1 - distanceLeftX) * (1 - distanceUpY) +
                                   northEastPixel.blue * distanceLeftX * (1 - distanceUpY) +
                                   southWestPixel.blue * (1 - distanceLeftX) * distanceUpY +
                                   southEastPixel.blue * distanceLeftX * distanceUpY);

                scaledVram.setPixel(x, y, pixel.red, pixel.green, pixel.blue);
            }
        }

        return scaledVram;
    }

    public static V_RAM scale(V_RAM vram, double ratio){
        if(ratio < 1){
            return scaleDown(vram, ratio);
        }else{
            return scaleUp(vram, ratio);
        }
    }

    public static void drawLine(V_RAM vram, Point2D p1, Point2D p2, RGB color){

        int x1 = (int)Math.round(p1.x);
        int y1 = (int)Math.round(p1.y);

        int x2 = (int)Math.round(p2.x);
        int y2 = (int)Math.round(p2.y);

        if(x1 == x2 && y1 == y2){//pixel
            vram.setPixel(x1, y1, color);
            return;
        }


        if(y1 == y2){//horizontal line

            if(x1 > x2){//swap pixels if in wrong order
                drawLineHorizontal(vram, x2, y1, x1, color);
            }else{
                drawLineHorizontal(vram, x1, y1, x2, color);
            }
            return;
        }

        if(x1 == x2){//vertical line

            if(y1 > y2){//swap pixels if in wrong order
                drawLineVertical(vram, x1, y2, y1, color);
            }else{
                drawLineVertical(vram, x1, y1, y2, color);
            }
            return;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;

        if(Math.abs(dx) == Math.abs(dy)){//diagonal line
            drawLineDiagonal(vram, x1, y1, x2, y2, color);
            return;
        }

        if(Math.abs(dx) > Math.abs(dy)){//low slope

            if(dx < 0){//swap points to draw from left to right
                drawLineLowSlope(vram, x2, y2, x1, y1, color);
            }else{
                drawLineLowSlope(vram, x1, y1, x2, y2, color);
            }
            return;
        }

        if(Math.abs(dy) > Math.abs(dx)){//high slope

            if(dy < 0){//swap points if in wrong order
                drawLineHighSlope(vram, x2, y2, x1, y1, color);
            }else{
                drawLineHighSlope(vram, x1, y1, x2, y2, color);
            }
            return;
        }
    }

    private static void drawLineLowSlope(V_RAM vram, int x1, int y1, int x2, int y2, RGB color){
        //bresenham

        int p = Math.abs(y2 - y1);
        int q = x2 - x1;

        int h1 = 2 * p;
        int h2 = h1 - 2 * q;// 2p - 2q
        int h = h1 - q;// 2p - q

        int slopeY = (y2 - y1) < 0? -1 : 1;//points up or down

        vram.setPixel(x1, y1, color);

        for(int x = x1 + 1, y = y1; x < x2; x++){

            if(h < 0){
                h += h1;
            }else{
                h += h2;
                y += slopeY;
            }

            vram.setPixel(x, y, color);
        }

        vram.setPixel(x2, y2, color);

//        DDA
//        int distanceX = x2 - x1;
//        int distanceY = y2 - y1;
//
//        double stepY = distanceY / (double)distanceX;
//        double y = y1;
//
//        for(int x = x1; x <= x2; x++, y+= stepY){
//            vram.setPixel(x, (int)Math.round(y), color);
//        }
    }

    private static void drawLineHighSlope(V_RAM vram, int x1, int y1, int x2, int y2, RGB color){
        //bresenham

        int p = y2 - y1;
        int q = Math.abs(x2 - x1);

        int h1 = - 2 * q;
        int h2 = 2 * p + h1;// 2p - 2q
        int h = p + h1;// p - 2q

        int slopeX = (x2 - x1) < 0? -1 : 1;//points left or right

        vram.setPixel(x1, y1, color);

        for(int y = y1 + 1, x = x1; y < y2; y++){
            if(h > 0){
                h += h1;
            }else{
                h += h2;
                x += slopeX;
            }

            vram.setPixel(x, y, color);
        }

        vram.setPixel(x2, y2, color);

//        DDA
//        int distanceX = x2 - x1;
//        int distanceY = y2 - y1;
//
//        double stepX = distanceX / (double)distanceY;
//        double x = x1;
//
//        for(int y = y1; y <= y2; y++, x+= stepX){
//            vram.setPixel((int)Math.round(x), y , color);
//        }
    }

    private static void drawLineHorizontal(V_RAM vram, int x1, int y, int x2, RGB color){
        for(int x = x1; x <= x2; x++){
            vram.setPixel(x, y, color);
        }
    }

    private static void drawLineVertical(V_RAM vram, int x, int y1, int y2, RGB color){
        for(int y = y1; y <= y2; y++){
            vram.setPixel(x, y, color);
        }
    }

    private static void drawLineDiagonal(V_RAM vram, int x1, int y1, int x2, int y2, RGB color){
        int slopeX = (x2 - x1) < 0? -1 : 1;
        int slopeY = (y2 - y1) < 0? -1 : 1;

        for(int x = x1, y = y1; x != x2; x += slopeX, y += slopeY){
            vram.setPixel(x, y, color);
        }
    }

    public static void drawCurve(V_RAM vram, Point2D p0, Point2D p1, Point2D p2, Point2D p3, RGB color, int steps){
        CubicBezierCurve bezier = new CubicBezierCurve(p0, p1, p2, p3);

        //first and last points need to be entered separately
        //because of rounding error in adding doubles when calculating t
        Point2D prevPoint = p0;
        double step = 1d/steps;
        for(double t = step; t < 1; t += step){
            Point2D currentPoint = bezier.getPoint(t);

            drawLine(vram, prevPoint, currentPoint, color);

            prevPoint = currentPoint;
        }
        drawLine(vram, prevPoint, p3, color);
    }


}
