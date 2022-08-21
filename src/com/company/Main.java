package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

    public static class DoublePoint {
        double x;
        double y;
        public DoublePoint(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    public static class BigDecimalPoint {
        BigDecimal x;
        BigDecimal y;
        public BigDecimalPoint(BigDecimal x, BigDecimal y){
            this.x = x;
            this.y = y;
        }
    }

    public static class Point implements Comparable{
        int x;
        int y;
        public int strength;
        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Object o) {
            int s = ((Point)o).strength;
            if(strength > s)
                return 1;
            if(strength == s)
                return 0;
            if(strength < s)
                return -1;
            return -2;
        }
    }

    static int debug = 0;

    static BigDecimal WIDTH = new BigDecimal(2);
    static BigDecimal HEIGHT = new BigDecimal(2);//TODO do these need to be bigdecimal?
    static BigDecimal CENTER_X = new BigDecimal(0);
    static BigDecimal CENTER_Y = new BigDecimal(0);

    static int pixel_center_x = 0;
    static int pixel_center_y = 0;
    static int zoom = 10;


    static MathContext mc = new MathContext(10);

    final static int PANELWIDTH = 1000;
    final static int PANELHEIGHT = 1000;
//TODO default 2000
    final static int mandelResolution = 500;
    static BigDecimal steps = WIDTH.divide(new BigDecimal(mandelResolution), mc);
     static int iterations = 15;
     static JButton iterationButton = null;
     static MandelProgress progress = null;

    public static void main(String[] args) {
        //transformPixelToGrid(750, 750);
        progress = new MandelProgress();
        int width = PANELWIDTH+1;
        int height = PANELHEIGHT+1;
        JFrame frame = new JFrame("Direct draw demo");

        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                return true;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);



        DirectDrawDemo panel = new DirectDrawDemo(width, height);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                java.awt.Point p = new java.awt.Point(e.getX(), e.getY());
                System.out.println("click pixels: " + p.x + " " + p.y);
                if(SwingUtilities.isLeftMouseButton(e)){
                    debug = 0;
                    pixel_center_x = p.x;
                    pixel_center_y = p.y;
                    BigDecimalPoint gridPoint = transformBigPixelToGrid(p.x, p.y);
                    System.out.println("leftclick grid" + gridPoint.x + " " + gridPoint.y);
                    CENTER_X = gridPoint.x;
                    CENTER_Y = gridPoint.y;
                    WIDTH = WIDTH.divide(new BigDecimal(zoom), mc);
                    HEIGHT = HEIGHT.divide(new BigDecimal(zoom), mc);
                    updateSteps();
                    mandel(panel);
                    System.out.println("DONE");
                }
                else if(SwingUtilities.isRightMouseButton(e)){
                    System.out.println("test");
                    debug = 0;
                    pixel_center_x = p.x;
                    pixel_center_y = p.y;
                    BigDecimalPoint gridPoint = transformBigPixelToGrid(p.x, p.y);
                    System.out.println("" + gridPoint.x + " " + gridPoint.y);
                    CENTER_X = gridPoint.x;
                    CENTER_Y = gridPoint.y;
                    WIDTH = WIDTH.multiply(new BigDecimal(zoom));
                    HEIGHT = HEIGHT.multiply(new BigDecimal(zoom));


                    updateSteps();
                    mandel(panel);
                    System.out.println("DONE");
                }
            }
        });

        iterationButton = new JButton(String.valueOf(iterations));
        iterationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String iterationsString = JOptionPane.showInputDialog("number of iterations");
                iterations = Integer.parseInt(iterationsString);
                //iterations++;
                mandel(panel);
                iterationButton.setText(String.valueOf(iterations));
            }
        });


        mandel(panel);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(iterationButton, BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void mandel(DirectDrawDemo panel){
        progress.fill(0);
        panel.drawRect(Color.black, 0, 0, PANELWIDTH, PANELHEIGHT);
        ArrayList<Point> median = new ArrayList<>();

        BigDecimal realStart = CENTER_X.subtract(WIDTH);

        System.out.println("Start X" + (CENTER_X.subtract(WIDTH)));
        System.out.println("End X" + (CENTER_X.add(WIDTH)));
        System.out.println("Start X " + transformBigGridToPixel((CENTER_X.subtract(WIDTH)), new BigDecimal(0)).x);
        System.out.println("End X " + transformBigGridToPixel((CENTER_X.add(WIDTH)), new BigDecimal(0)).x);



        int numSteps = 0;
        BigDecimal realMax = CENTER_X.add(WIDTH);
        BigDecimal ImgMax = CENTER_Y.add(HEIGHT);
        for(BigDecimal xReal = CENTER_X.subtract(WIDTH); (xReal.compareTo(realMax) < 0); xReal = xReal.add(steps)){
            double xRealDouble = xReal.doubleValue();
            double xRealStartDouble = realStart.doubleValue();
            double xRealMaxDouble = realMax.doubleValue();
            //System.out.println((int)(((xRealDouble - xRealStartDouble)/(xRealMaxDouble - xRealStartDouble)) * 100));
            progress.fill((int)(((xRealDouble - xRealStartDouble)/(xRealMaxDouble - xRealStartDouble)) * 100));
            numSteps++;
            //System.out.println(xReal);
            //System.out.println(steps);
            for(BigDecimal yImg = CENTER_Y.subtract(HEIGHT); (yImg.compareTo(ImgMax) < 0); yImg = yImg.add(steps)){
                //System.out.println(xReal);
                //System.out.println(yImg);
                BigComplexNumber c = new BigComplexNumber(xReal, yImg);
                int x = getBoundedRange(c);
                Point p = transformBigGridToPixel(c.getRealBig(), c.getImgBig());
                p.strength = x;
                if(x > 0){
                    median.add(p);
                    System.out.println("color");
                }

                else{//if just black and no color
                    if(debug == 1){
                        System.out.println(p.x);
                        System.out.println(p.y);
                        System.out.println(yImg);
                        System.out.println(xReal);
                        System.out.println(CENTER_X);
                        System.out.println(CENTER_Y);
                        System.out.println("----");
                    }


                    panel.drawRect(Color.black, p.x, p.y, 1, 1);
                }

            }
        }
        /* old calc
        System.out.println(numSteps);
        System.out.println("median size" + median.size());
        if(median.size() == 0)
            return;
        Collections.sort(median);
        int percentile = median.get((median.size()/10)*5).strength;
        for(Point p: median){
            System.out.println("stregnth" + p.strength + "median" + percentile);
            int color = ((p.strength/percentile)*255);
            if(color > 255 || color < 0)
                color = 255;
            panel.drawRect(new Color(255, 255, color), p.x, p.y, 1, 1);
        }

         */

        if(median.size() == 0)
            return;
        for(Point p: median){
            int color = (int)(((double)p.strength/((double)iterations))*255);

            if(color > 255 || color < 0)
                color = 255;
            System.out.println(color);
            panel.drawRect(new Color(color, color, 255), p.x, p.y, 1, 1);
        }

    }
/*
    public static boolean isBounded(ComplexNumber c){
        ComplexNumber current = ComplexNumber.pow(c, 2);
        current = ComplexNumber.add(current, c);
        for(int i = 0; i < iterations; i++){
            current = ComplexNumber.pow(current, 2);
            current = ComplexNumber.add(current, c);
        }
        return true;

    }

 */

    public static int getBoundedRange(BigComplexNumber c){
        BigComplexNumber current = BigComplexNumber.pow(c, 2, mc);
        current = BigComplexNumber.add(current, c);
        boolean outside = false;
        int iterationBreak = 0;

        //BigDecimal [] prev = new BigDecimal[iterations];
        long milliSec = System.currentTimeMillis();
        for(int i = 0; i < iterations; i++){
            long newMilliSec = System.currentTimeMillis();
            if(milliSec - newMilliSec > 1000){
                System.out.println(current.getRealBig());
            }
            /*
           // if(current.getRealBig().floatValue() == 0) {
            System.out.println(i);
            if(i == iterations - 1) {

                System.out.println(current.getRealBig().floatValue());
                for(int j = 0; j < iterations -1 ; j++){
                    System.out.println(j + ": " + prev[j].floatValue());
                }
            }
            //System.out.println("test");

             */
            current = BigComplexNumber.pow(current, 2, mc);
            current = BigComplexNumber.add(current, c);
            if(outsideBigMandelBrotCircle(current.getRealBig(), current.getImgBig())){
                //System.out.println("broke at iteration " + i);
                iterationBreak = i;
                outside = true;
                break;
            }
            //prev[i] = current.getRealBig();
        }
        if(!outside){
            //System.out.println("dint break");
            return 0;
        }

        //return (int)((Math.abs(current.getRealBig().floatValue()) + Math.abs(current.getImgBig().floatValue()))); //TODO previous color calc
        return iterationBreak;
        /*
        int color = (int)((Math.abs(current.getRe()) + Math.abs(current.getIm()))/boundDivision)*255;

        if(color > 255 || color < 0)
            color = 255;
        System.out.println(color);
        return color;

         */


    }

/*
    //5x5 for now
    //IS THIS CORRECT WITH NEGATIVES? idk i dont think so - might not matter since symmetric
    public static Point transformGridToPixel(double real, double imaginary){
        double heightStart = (((double)PANELHEIGHT)/2);
        double widthStart = (((double)PANELWIDTH)/2);


        imaginary -= CENTER_Y;
        real -= CENTER_X;
        heightStart -= (imaginary/HEIGHT)*heightStart;
        widthStart += (real/WIDTH)*widthStart;

        //if(real < 0)
        //    heightStart -= (((double)PANELHEIGHT)/2);

       // System.out.println(real);
        ///System.out.println(heightStart);
        //System.out.println(widthStart);
        //heightStart -= pixel_center_y*2;
        //widthStart -= pixel_center_x*2;
        return new Point((int)Math.round(widthStart), (int)Math.round(heightStart));
    }

 */

    //5x5 for now
    //IS THIS CORRECT WITH NEGATIVES? idk i dont think so - might not matter since symmetric
    public static Point transformBigGridToPixel(BigDecimal real, BigDecimal imaginary){
        double heightStart = (((double)PANELHEIGHT)/2);
        double widthStart = (((double)PANELWIDTH)/2);


        imaginary = imaginary.subtract(CENTER_Y);
        real = real.subtract(CENTER_X);
        heightStart -= (imaginary.divide(HEIGHT, mc)).multiply(new BigDecimal(heightStart)).doubleValue();
        widthStart += (real.divide(WIDTH, mc)).multiply(new BigDecimal(widthStart)).doubleValue();

        return new Point((int)Math.round(widthStart), (int)Math.round(heightStart));
    }
/*
    public static DoublePoint transformPixelToGrid(int x, int y){
        double heightStart = HEIGHT*2;
        double widthStart = WIDTH*2;



        heightStart = ((((PANELHEIGHT/2) - y)/((double)PANELHEIGHT)) * heightStart);
        widthStart = (((x - (PANELHEIGHT/2))/((double)PANELWIDTH)) * widthStart);

        heightStart += CENTER_Y;
        widthStart += CENTER_X;
        return new DoublePoint((double)widthStart, (double)heightStart);
    }

 */


    public static BigDecimalPoint transformBigPixelToGrid(int x, int y){
        BigDecimal heightStart = HEIGHT.multiply(new BigDecimal(2));
        BigDecimal widthStart = WIDTH.multiply(new BigDecimal(2));


// i think TODO panelheight 2nd line should b panelwidht- they are same anyway but should change
        heightStart = (BigDecimal.valueOf(((PANELHEIGHT / 2) - y) / ((double) PANELHEIGHT)).multiply(heightStart));
        widthStart = (BigDecimal.valueOf(((x - (PANELHEIGHT/2))/((double)PANELWIDTH))).multiply(widthStart));

        heightStart = heightStart.add(CENTER_Y);
        widthStart = widthStart.add(CENTER_X);
        return new BigDecimalPoint(widthStart, heightStart);
    }


    public static boolean outsideMandelBrotCircle(double a, double b){
        double lineLength = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        return lineLength > 2;
    }
    public static boolean outsideBigMandelBrotCircle(BigDecimal a, BigDecimal b){
        //not much precision needed
        return outsideMandelBrotCircle(a.floatValue(), b.floatValue());
    }


    public static void updateSteps(){
        steps = WIDTH.divide(new BigDecimal(mandelResolution), mc);
        //TODO find a good way to update this
        mc = new MathContext(mc.getPrecision() + 1);
        iterations += 3;
        iterationButton.setText(String.valueOf(iterations));
    }

}
