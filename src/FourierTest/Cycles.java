package FourierTest;

import java.awt.*;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;

public class Cycles
//this is like my draw class
{
    static double time = 0;
    static int N;
    //static ArrayList<Double> wave = new ArrayList<>();
    static ArrayList<Point2D.Double> path = new ArrayList<>();


    public static Point2D.Double drawCycles(Graphics2D g, double xshift, double yshift, double rotation, double[][] fourierY, Color a) {

        //g.setColor(a);
        //g.fillRect(0,0,800,800);
        g.setColor(Color.BLACK);

        N = fourierY.length;
        double x = 0;
        double y = 0;

        for (int i = 0; i < fourierY.length; i++) {
            // for (int i = fourierY.length-2; i>=0; i--){
            double prevx = x;
            double prevy = y;

            double freq = fourierY[i][2];
            double radius = fourierY[i][3];
            double phase = fourierY[i][4];

            x += radius * Math.cos(freq * time + phase + rotation);
            y += radius * Math.sin(freq * time + phase + rotation);
            //g.drawOval(prevx+150-radius,prevy+200,radius*2,radius*2);
            Ellipse2D.Double epi = new Ellipse2D.Double(prevx + xshift - radius, prevy + yshift - radius, radius * 2, radius * 2);
            g.draw(epi);
            //g.drawLine(prevx+150,prevy+200,x+150,y+200); //axis
            Line2D.Double axis = new Line2D.Double(prevx + xshift, prevy + yshift, x + xshift, y + yshift);
            g.draw(axis);

            //System.out.println("radius " + radius + "  x " + x + "  y " + y);
        }

        //if(x<0) System.out.println(x);

        Point2D.Double vector = new Point2D.Double((x)+xshift, (y)+yshift);
        // Ellipse2D.Double bitch = new Ellipse2D.Double(x+xshift,y+yshift,20.0,20.0);
        //System.out.println("x =  " + (x+xshift) + "    y = " + (y+yshift));
        return vector;

        //******wave.add(0,y);
        //translate 200,0
        //g.translate(200+xshift,0+yshift);
        //******Line2D.Double anchor = new Line2D.Double(x-200 + 200 + xshift,y + yshift,0+200+xshift,wave.get(0)+yshift);
        //*******g.draw(anchor);
        // g.drawLine(x+150,y+200,350,wave.get(0)+200);

       /*int xpoints[] = new int[wave.size()];
       int ypoints[] = new int[wave.size()];
       for(int i = 0; i<wave.size(); i++){
           xpoints[i] = i+350;
           ypoints[i] = wave.get(i)+200;
       }
       int npoints = wave.size();

       g.drawPolygon(xpoints, ypoints, npoints);

       g.setColor(Color.PINK);
       g.drawLine(xpoints[0],ypoints[0],xpoints[npoints-1],ypoints[npoints-1]);
       g.setColor(Color.BLACK);*/

        /*******Path2D.Double wavePath = new Path2D.Double();
         wavePath.moveTo(0+200+xshift,wave.get(0)+yshift);
         for(int i=  1; i<wave.size(); i++){
         wavePath.lineTo(i+200+xshift,wave.get(i)+yshift);
         }
         g.draw(wavePath);

         final double dt = 2* Math.PI/fourierY.length;
         time+=dt;

         if(wave.size()>250)
         wave.remove(wave.size()-1);******/

    }

    public static void wavePath(Point2D.Double V, Graphics2D g) {
        g.setColor(Color.BLACK);
        path.add(V);
        //Ellipse2D.Double test = new Ellipse2D.Double(100*path.get(path.size()-1).x+200,100*path.get(path.size()-1).y+200, 10,10);
        //Line2D.Double oof = new Line2D.Double(V.x,V.y,path.get(path.size()-1).x+200,path.get(path.size()-1).y+200);
        //g.draw(oof);
        //System.out.println(path.get(path.size()-1));
        //g.draw(test);
        Path2D.Double wave = new Path2D.Double();
        //g.translate(200,200);
        int a = 0;
        int b = 0;
        wave.moveTo(path.get(0).x+a,path.get(0).y+b);
        for(int i=  1; i<path.size(); i++){
            wave.lineTo(path.get(i).x+a,path.get(i).y+b);
            //wave.lineTo(i,2*i);
        }
        g.draw(wave);

        final double dt = 2* Math.PI/N;
        time+=dt;
        //System.out.println(path.get(path.size()-1));
        /*if(path.size()>250)
            path.remove(path.size()-1);*/
    }

}

