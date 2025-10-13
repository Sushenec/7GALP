package cz.osu.myGraphics;

public class HSL {
    public float hue;
    public float saturation;
    public float lightness;

    /*
    * hue = <0, 360)
    * saturation = <0, 100>
    * lightness = <0 ,100>
    * */

    public HSL(float hue, float saturation, float lightness){
        this.hue = hue;
        this.saturation = saturation;
        this.lightness = lightness;
    }

    public HSL(RGB rgb){

        float red = rgb.red / 255f;
        float green = rgb.green / 255f;
        float blue = rgb.blue / 255f;

        float max = Math.max(Math.max(red, green), blue);
        float min = Math.min(Math.min(red, green), blue);
        float chroma = max - min;

        if(chroma == 0){
            this.hue = 0;
        }else{
            if(red == max){
                this.hue = ((green - blue) / chroma) % 6;

            } else if(green == max){
                this.hue = ((blue - red) / chroma) + 2;

            }else if(blue == max){
                this.hue = ((red - green) / chroma) + 4;

            }

                this.hue *= 60;
        }

        this.lightness = (max + min) / 2f;
        this.saturation = (this.lightness > .5f)? chroma / (2 - 2 * this.lightness): chroma / (2 * this.lightness);

        this.lightness *= 100;
        this.saturation *= 100;


    }

}
