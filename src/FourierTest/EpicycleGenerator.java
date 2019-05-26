package FourierTest;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class EpicycleGenerator extends JPanel
        //this is like my setup class
{
    /* JFrame frame;
     double[][]Xpoints, Ypoints;

     public EpicycleGenerator(double[][]Xvals, double[][]Yvals)
     {
         frame = new JFrame("Fourier Transform drawing");
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setResizable(false);
         frame.setSize(800, 800);
         frame.setVisible(true);
         frame.add(this);
         Xpoints = Xvals;
         Ypoints = Yvals;
         init();
     }

     public void init()
     {
         frame.setBackground(Color.PINK);
     }

     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.PINK);
         Cycles.drawCycles(g, 50,200, 0, Xpoints, Color.BLACK);
         Cycles.drawCycles(g,400,50,Math.PI/2,Ypoints,Color.BLUE);
         try
         {
             Thread.sleep(80);
         }
         catch (InterruptedException ie)
         {
             ie.printStackTrace();
         }

         repaint();
     }*/
    JFrame frame;
    ArrayList<double[][]> points = new ArrayList<>();
    ArrayList<Double> xshift = new ArrayList<>();
    ArrayList<Double> yshift = new ArrayList<>();
    ArrayList<Double> r = new ArrayList<>();
    Point2D.Double vX;
    Point2D.Double vY;
    Point2D.Double V;

    public EpicycleGenerator()
    {
        /*frame = new JFrame("Fourier Transform drawing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(5000, 5000);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.add(this);
        init();*/
        frame = new JFrame("Fourier Transform drawing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(10000, 5000);
        //frame.add(panelPane);
        frame.add(this);
        this.setPreferredSize(new Dimension(10000,5000));
        JScrollPane pane=new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setPreferredSize(this.getPreferredSize());
        frame.add(pane);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.pack();
    }

    public void init()
    {
        frame.setBackground(Color.PINK);
    }

    public void addEpicylce(double x,double y,double rot,double[][]vals){
        xshift.add(x);
        yshift.add(y);
        r.add(rot);
        points.add(vals);
    }
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D)gg;
        setBackground(Color.PINK);
        int a = 0;
        vX =  Cycles.drawCycles(g, xshift.get(0),yshift.get(0), r.get(0), points.get(0), Color.BLACK);
        vY = Cycles.drawCycles(g, xshift.get(1),yshift.get(1), r.get(1), points.get(1), Color.BLACK);
        V = new Point2D.Double(vX.x,vY.y);
        Line2D.Double axis1 = new Line2D.Double(vX.x+a,vX.y+a,V.x+a,V.y+a);
        Line2D.Double axis2 = new Line2D.Double(vY.x+a,vY.y+a,V.x+a,V.y+a);
        g.draw(axis1);
        g.draw(axis2);
        //System.out.println("V.x = " + V.x + "   V.y = " + V.y);
        //g.fillOval((int)V.x,(int)V.y,10,10);

        Cycles.wavePath(V,g);
        try
        {
            Thread.sleep(50);
        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }

        repaint();
    }

}

