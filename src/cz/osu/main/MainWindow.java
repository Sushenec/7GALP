package cz.osu.main;

import cz.osu.myGraphics.Kernel;
import cz.osu.myGraphics.MyGraphics;
import cz.osu.myGraphics.Point2D;
import cz.osu.myGraphics.RGB;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class MainWindow extends JPanel{

    private ImagePanel imagePanel;

    private V_RAM vram;
    private V_RAM bufferVram;

    private MyGraphics graphics;

    public MainWindow(){

        initialize();
        
        vram = new V_RAM(100, 100);
        bufferVram = vram.copy();



        imagePanel.setImage(vram.getImage());
    }

    private void initialize(){

        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();

        imagePanel = new ImagePanel();
        imagePanel.setBounds(10,60, 970, 600);
        this.add(imagePanel);

        //open image
        JButton button = new JButton();
        button.setBounds(150,10,120,30);
        button.setText("Load Image");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                openImage();
            }
        });
        this.add(button);

        //save image as PNG
        JButton button4 = new JButton();
        button4.setBounds(10,10,120,30);
        button4.setText("Save as PNG");
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImageAsPNG();
            }
        });
        this.add(button4);

        JFormattedTextField numberInput = new JFormattedTextField(NumberFormat.getNumberInstance());
        numberInput.setBounds(290, 10, 120, 30);
        numberInput.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Number value = (Number) numberInput.getValue();

                if(value != null){
                    double number = value.doubleValue();

                    //Reset vram to original picture
                    vram = bufferVram.copy();

                    //My graphics
                    Point2D p0 = new Point2D(10,10);
                    Point2D p1 = new Point2D(70, 10);
                    Point2D p2 = new Point2D(70,80);
                    Point2D p3 = new Point2D(10, 80);

                    MyGraphics.drawCurve(vram, p0, p1, p2, p3, RGB.darkOrchid, (int)number);

                    vram.setPixel((int)p0.x,(int)p0.y, 255,255,255);
                    vram.setPixel((int)p3.x, (int)p3.y, 255,255,255);
                    imagePanel.setImage(vram.getImage());
                }

            }
        });
        this.add(numberInput);

        JFrame frame = new JFrame("Raster Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setSize(1004, 705);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void openImage(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Load Image");

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            try {

                BufferedImage temp = ImageIO.read(file);

                if(temp != null){

                    vram = new V_RAM(temp);
                    bufferVram = vram.copy();

                    Kernel k = Kernel.createBlurKernel(3);

                    vram = MyGraphics.scale(vram, 1.1);

                    imagePanel.setImage(vram.getImage());

                }else {

                    JOptionPane.showMessageDialog(null, "Unable to load image", "Open image: ", JOptionPane.ERROR_MESSAGE);
                }

            }catch (IOException e){

                e.printStackTrace();
            }
        }
    }

    private void saveImageAsPNG(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Save Image as PNG");

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            String fname = file.getAbsolutePath();

            if(!fname.endsWith(".png") ) file = new File(fname + ".png");

            try {

                ImageIO.write(imagePanel.getImage(), "png", file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        new MainWindow();
    }
}
