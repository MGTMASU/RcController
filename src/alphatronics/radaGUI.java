package alphatronics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

public class radaGUI extends javax.swing.JPanel {
    
    public radaGUI() {
        initComponents();
//        t.start();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        float b = a+0.5f ;
        float c = a-0.5f ;
        g.clearRect(0, 0, 400, 400);
        g.setColor(Color.BLACK);
        g.fillArc(0, 0, 400, 400, 0, 180);
        g.drawLine(0, 200, 400, 200);
        g.setColor(Color.green);
        g.drawLine(200, 200, 200+(int)(200*Math.cos((-(30f/180f)*Math.PI))), 200+(int)(200*Math.sin(-(30f/180f)*Math.PI)));
        g.drawLine(200, 200, 200+(int)(200*Math.cos((-(60f/180f)*Math.PI))), 200+(int)(200*Math.sin(-(60f/180f)*Math.PI)));
        g.drawLine(200, 200, 200+(int)(200*Math.cos((-(90f/180f)*Math.PI))), 200+(int)(200*Math.sin(-(90f/180f)*Math.PI)));
        g.drawLine(200, 200, 200+(int)(200*Math.cos((-(120f/180f)*Math.PI))), 200+(int)(200*Math.sin(-(120f/180f)*Math.PI)));
        g.drawLine(200, 200, 200+(int)(200*Math.cos((-(150f/180f)*Math.PI))), 200+(int)(200*Math.sin(-(150f/180f)*Math.PI)));
        g.drawLine(200, 200, 200+(int)(190*Math.cos((-(c/180f)*Math.PI))), 200+(int)(190*Math.sin(-(c/180f)*Math.PI)));
        g.drawLine(200, 200, 200+(int)(190*Math.cos((-(b/180f)*Math.PI))), 200+(int)(190*Math.sin(-(b/180f)*Math.PI)));
        g.drawLine(200, 200, 200+(int)(200*Math.cos((-(a/180f)*Math.PI))), 200+(int)(200*Math.sin(-(a/180f)*Math.PI)));
        g.drawArc(50,50,300,300,0,180);
        g.drawArc(100,100,200,200,0,180);
        g.drawArc(150,150,100,100,0,180);
        drawObjects(g);
        if(invert){
            removeAllObjects();
        }
        invert = false;
        if(a==0)invert = true;
        if(a==180)invert = true;
        
    }
    
//    private Timer t = new Timer(45, (ActionEvent e) -> {
//        repaint();
//    });
    
    float a = 0f;
    
    boolean invert = false;
    
    private void drawObject(Graphics g , float theta , float distance){
        g.setColor(Color.red);
        g.fillOval(200+(int)(distance*Math.cos((-(theta/180f)*Math.PI))), 200+(int)(distance*Math.sin(-(theta/180f)*Math.PI)),5,5);        
    }
    
    private void drawObjects(Graphics g){
        for(int a = 0 ; a < objects.size() ; a++){
            drawObject(g, objects.get(a)[0],objects.get(a)[1]);
        }
    }
    
    public void addNewObject(float theta , float distance){
        float object [] = new float[2];
        object[0] = theta;
        object[1] = distance;
        objects.add(object);
    }
    
    private void removeAllObjects(){
        for(int a = 0 ; a < objects.size() ; a++){
            objects.remove(a);
        }
    }
    
    private final ArrayList<float[]> objects = new ArrayList();
    
    public void move(int theta){
        a = theta;
        repaint();
    }
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
