package FourierTest;

import FourierTest.EpicycleGenerator;
import FourierTest.Fourier;
import org.opencv.core.Core;

import java.awt.*;
import java.util.ArrayList;

public class Test {

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FourierProcessing fourier = new FourierProcessing();
        ArrayList<Point> points = fourier.getPoints();
        double angle = 0;
        int skip = 0;
        double [] x = new double[points.size()];
        double [] y = new double[points.size()];
        for(int a = 0;a<points.size();a+=1)
        // y[a] = (int)(Math.random()*101);
        {
            angle = a;
            //x[a] = 50*Math.sin(3*Math.toRadians(angle));
            //y[a] = 50*Math.cos(3*Math.toRadians(angle));
            x[a] = points.get(a+skip).getX();
            y[a] = points.get(a+skip).getY();
            //angle+=0.02;
        }
        //int [] y = {100,100,100,-100,-100,-100,100,100,100};
        double[][]fourierX = Fourier.DFT(x);
        double [][] fourierY = Fourier.DFT(y);

        fourierX =  sort(fourierX);
        fourierY = sort(fourierY);
        // new EpicycleGenerator(fourierX,fourierY);
        EpicycleGenerator gen = new EpicycleGenerator();
        gen.addEpicylce(600,100,0,fourierY);
        gen.addEpicylce(100,600,Math.PI/2,fourierX);


        //new EpicycleGenerator(100,200,0,fourierX);
        // new EpicycleGenerator(100,400,Math.PI/2,fourierY);
    }

    public static double[][] sort(double[][] arr){
        int n = arr.length;

        for (int i = 0; i < n-1; i++)
        {
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (arr[j][3] < arr[min_idx][3])
                    min_idx = j;

            // Swap the found minimum element with the first
            // element
            double [] temp = arr[min_idx];
            arr[min_idx] = arr[i];
            arr[i] = temp;
        }
        return arr;
    }
}
