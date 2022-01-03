package com.company;// Java Program to create a
// simple progress bar
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class MandelProgress extends JFrame {

    // create a frame
    static JFrame f;

    static JProgressBar b;

    public MandelProgress()
    {

        // create a frame
        f = new JFrame("ProgressBar demo");

        // create a panel
        JPanel p = new JPanel();

        // create a progressbar
        b = new JProgressBar();

        // set initial value
        b.setValue(0);

        b.setStringPainted(true);

        // add progressbar
        p.add(b);

        // add panel
        f.add(p);

        // set the size of the frame
        f.setSize(500, 500);
        f.setVisible(true);

        fill(0);
    }

    // function to increase progress
    public void fill(int set)
    {
        b.setValue(set);

    }
}