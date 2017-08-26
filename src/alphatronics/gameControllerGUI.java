package alphatronics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mgtmP
 */
public class gameControllerGUI extends javax.swing.JPanel {

    boolean xy = true;
    boolean zr = true;
    private AnalogValues valueXY = AnalogValues.normal;
    private AnalogValues valueZR = AnalogValues.normal;
    public gameControllerGUI() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();

        setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 435, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 239, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    // End of variables declaration//GEN-END:variables
 
    @Override 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.clearRect(-250, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        g.fillOval(30, 50, 150, 150);
        g.setColor(Color.RED);
        switch(valueXY) {
            case up:
                g.fillOval(30+75-30, 50+75-30-60, 60,60);
                break;
            case down:
                g.fillOval(30+75-30 , 50+75-30+60 ,60 , 60);
                break;
            case left:
                g.fillOval(30+75-30-60, 50+75-30, 60,60);
                break;
            case right:
                g.fillOval(30+75-30+60, 50+75-30, 60,60);
                break;
            case normal:
                g.fillOval(30+75-30, 50+75-30, 60,60);
                break;
            case upRight :
                g.fillOval(30+75-30+40, 50+75-30-40, 60, 60);
                break;
            case upLeft :
                g.fillOval(30+75-30-40, 50+75-30-40, 60, 60);
                break;
            case downRight :
                g.fillOval(30+75-30+40, 50+75-30+40, 60, 60);
                break;
            case downLeft :
                g.fillOval(30+75-30-40, 50+75-30+40, 60, 60);
                break; 
        }

        g.clearRect(250, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        g.fillOval(250, 50, 150, 150);
        g.setColor(Color.RED);
        switch(valueZR) {
            case up:
                g.fillOval(250+75-30, 50+75-30-60, 60,60);
                break;
            case down:
                g.fillOval(250+75-30 , 50+75-30+60 ,60 , 60);
                break;
            case left:
                g.fillOval(250+75-30-60, 50+75-30, 60,60);
                break;
            case right:
                g.fillOval(250+75-30+60, 50+75-30, 60,60);
                break;
            case normal:
                g.fillOval(250+75-30, 50+75-30, 60, 60);
                break;
            case upRight :
                g.fillOval(250+75-30+40, 50+75-30-40, 60, 60);
                break;
            case upLeft :
                g.fillOval(250+75-30-40, 50+75-30-40, 60, 60);
                break;
            case downRight :
                g.fillOval(250+75-30+40, 50+75-30+40, 60, 60);
                break;
            case downLeft :
                g.fillOval(250+75-30-40, 50+75-30+40, 60, 60);
                break;
        }

        /*g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        g.fillOval(30, 50, 150, 150);
        g.fillOval(250, 50, 150, 150);
        g.setColor(Color.RED);
        // Normal Case
        g.fillOval(30+75-30, 50+75-30, 60,60);
        g.fillOval(250+75-30, 50+75-30, 60, 60);
        // up case
        //g.fillOval(30+75-30, 50+75-30-60, 60,60);
        // right case
        //g.fillOval(30+75-30+60, 50+75-30, 60,60);
        //left case
        //g.fillOval(30+75-30-60, 50+75-30, 60,60);
        //down case
        //g.fillOval(30+75-30 , 50+75-30+60 ,60 , 60);
        //g.fillOval(250+75-30, 50+75-30+60 ,60 , 60);
        g.drawString("X - Y Analog", 30+45, 50+75+110);
        g.drawString("Z - ZR Analog", 30+40+220, 50+75+110);
        // clear half of the panel
        //g.clearRect(-250, 0, this.getWidth(), this.getHeight());*/
    }
    
    public static void main(String[] args){
        gameControllerGUI h = new gameControllerGUI();
    }
    
    public void change_XY_Analog(AnalogValues value){
        xy = true;
        this.valueXY = value;
        repaint();
    }
    
    public void change_ZR_Analog(AnalogValues value){
        zr = true;
        this.valueZR = value;
        repaint();
    }
    
    public enum AnalogValues{
        up , down , left , right , normal , upRight , upLeft , downRight , downLeft
    }
}
