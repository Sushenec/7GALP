package cz.osu.myGraphics;

public class Kernel {
    public int[][] kernel;
    public int width;
    public int height;
    public int divider = 0;

    private Kernel(int size){
        this.width = size;
        this.height = size;
        this.kernel = new int[size][size];
    }

    public static Kernel createBlurKernel(int size){
        Kernel k = new Kernel(size);

        for(int y = 0; y < k.height; y++){
            for(int x = 0; x < k.width; x++) {
                k.kernel[y][x] = 1;
                k.divider += k.kernel[y][x];
            }
        }
        return k;
    }

}
