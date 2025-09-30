package cz.osu.myGraphics;

import cz.osu.main.V_RAM;
import java.awt.image.BufferedImage;

public class MyGraphics {
    private V_RAM vram;

    public MyGraphics(V_RAM vram){
        this.vram = vram;
    }

    public V_RAM getVram(){

        return this.vram;
    }

    public BufferedImage getImage(){

        return this.vram.getImage();
    }

    public void fill(int red, int green, int blue){

        for (int y = 0; y < vram.getHeight(); y++){
            for (int x = 0; x < vram.getWidth(); x++){
                vram.setPixel(x, y, red, green, blue);
            }
        }
    }

    public void drawRectangle(int x, int y, int width, int height, int borderSize ,int red, int green, int blue){

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
}
