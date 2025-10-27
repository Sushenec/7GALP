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
                //int lightness = (pixel.red + pixel.green + pixel.blue) / 3;

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
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                for(int yK = 0; yK < kernel.height; yK++){
                    for(int xK = 0; xK < kernel.width; xK++){
                        int targetX = x + xK - kernel.width / 2;
                        int targetY = y + yK - kernel.height / 2;

                        targetX = Math.clamp(targetX, 0, source.getWidth() - 1);
                        targetY = Math.clamp(targetY, 0, source.getHeight() - 1);

                        RGB pixel = new RGB(source.getPixel(targetX, targetY));

                        sumR += kernel.kernel[yK][xK] * pixel.red;
                        sumG += kernel.kernel[yK][xK] * pixel.green;
                        sumB += kernel.kernel[yK][xK] * pixel.blue;
                    }
                }

                sumR /= kernel.divider;
                sumG /= kernel.divider;
                sumB /= kernel.divider;

                vram.setPixel(x, y, sumR, sumG, sumB);
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
}
