package cz.osu.myGraphics;

public class RGB {
    public int red;
    public int green;
    public int blue;

    /*
    * red = <0, 256)
    * green = <0, 256)
    * blue = <0, 256)
    * */

    public RGB(int red, int green, int blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGB(HSL hsl){
        float saturation = hsl.saturation / 100f;
        float lightness = hsl.lightness / 100f;

        float chroma = (1 - Math.abs((2 * lightness) - 1)) * saturation;

        float hue = hsl.hue / 60f;
        float x = chroma * (1 - Math.abs((hue % 2) - 1));
        float min = lightness - (chroma / 2f);

        if(hue >= 0 && hue <= 1){

            this.red = Math.round((chroma + min) * 255);
            this.green = Math.round((x + min) * 255);
            this.blue = Math.round((min) * 255);

        }else if(hue <= 2){

            this.red = Math.round((x + min) * 255);
            this.green = Math.round((chroma + min) * 255);
            this.blue = Math.round((min) * 255);

        }else if(hue <= 3){

            this.red = Math.round((min) * 255);
            this.green = Math.round((chroma + min) * 255);
            this.blue = Math.round((x + min) * 255);

        }else if(hue <= 4){

            this.red = Math.round((min) * 255);
            this.green = Math.round((x + min) * 255);
            this.blue = Math.round((chroma + min) * 255);

        }else if(hue <= 5){

            this.red = Math.round((x + min) * 255);
            this.green = Math.round((min) * 255);
            this.blue = Math.round((chroma + min) * 255);

        }else if(hue <= 6){

            this.red = Math.round((chroma + min) * 255);
            this.green = Math.round((min) * 255);
            this.blue = Math.round((x + min) * 255);

        }
    }
}
