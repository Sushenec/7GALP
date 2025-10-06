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

    // Decomposes ARGB integer into its components
    public static RGB getRGB(int argb){
        return new RGB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, (argb >> 16) & 0xFF);
    }
}
