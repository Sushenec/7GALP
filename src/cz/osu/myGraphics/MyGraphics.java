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

                RGB pixel = getRGB(vram.getPixel(x,y));
                //int lightness = (pixel.red + pixel.green + pixel.blue) / 3;

                //Vážený průměr RGB -> lepší vyjádření jasů pro lidksé oko
                int lightness = (int)Math.round(0.299 * pixel.red + 0.587 * pixel.green + 0.114 * pixel.blue);
                lightness = Math.clamp(lightness, 0, 255);

                vram.setPixel(x, y, lightness, lightness, lightness);
            }
        }
    }

    // Decomposes ARGB integer into its components (doest return alfa chanel)
    public static RGB getRGB(int argb){

        return new RGB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
    }

    // Inverts colors in image
    public static void invertColors(V_RAM vram){

        for(int y = 0; y < vram.getHeight(); y++){
            for(int x = 0; x < vram.getWidth(); x++){

                RGB pixel = getRGB(vram.getPixel(x, y));

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

                int lightness = getRGB(vram.getPixel(x, y)).red;
                int error = 0;

                //fixed threshold dithering
                if(lightness < threshold){

                    vram.setPixel(x, y, 0, 0, 0);
                    error = lightness;

                }else{
                    vram.setPixel(x, y, 255, 255, 255);
                    error = lightness - 255;
                }

                //error distribution right pixel
                if((x + 1) < vram.getWidth()){
                    int currentLightness = getRGB(vram.getPixel(x + 1, y)).red;
                    int adjustedError = (int)Math.round( error * errorDistributionMatrix[1][0]);
                    int newLightness = Math.clamp((adjustedError + currentLightness), 0 ,255);

                    vram.setPixel(x + 1, y, newLightness, newLightness, newLightness);
                }

                //error distribution bottom pixel
                if((y + 1) < vram.getHeight()){
                    int currentLightness = getRGB(vram.getPixel(x, y + 1)).red;
                    int adjustedError = (int)Math.round( error * errorDistributionMatrix[0][1]);
                    int newLightness = Math.clamp((adjustedError + currentLightness), 0 ,255);

                    vram.setPixel(x, y + 1, newLightness, newLightness, newLightness);
                }

                //error distribution bottom-right pixel
                if(((y + 1) < vram.getHeight()) && ((x + 1) < vram.getWidth())){
                    int currentLightness = getRGB(vram.getPixel(x + 1, y + 1)).red;
                    int adjustedError = (int)Math.round( error * errorDistributionMatrix[1][1]);
                    int newLightness = Math.clamp((adjustedError + currentLightness), 0 ,255);

                    vram.setPixel(x + 1, y + 1, newLightness, newLightness, newLightness);
                }
            }
        }
    }

    //shifts hue in HSL model by shiftDegree ammount
    public static void hueShift(V_RAM vram, int shiftDegree){
        for(int y = 0; y < vram.getHeight(); y++){
            for(int x = 0; x < vram.getWidth(); x++){

                RGB pixel = getRGB(vram.getPixel(x ,y));
                HSL color = new HSL(pixel);

                color.hue = (color.hue + shiftDegree + 360) % 360;

                RGB newPixel = new RGB(color);
                vram.setPixel(x, y, newPixel.red, newPixel.green, newPixel.blue);
            }
        }
    }
}
