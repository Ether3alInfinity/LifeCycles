package FourierTest;

public class Fourier
{
    public static double[][] DFT(double[]ogX)
    {   double[][]X = new double[ogX.length][5];
        final int N = ogX.length;
        for(int k=0; k<N; k++)
        {
            double re = 0;
            double im = 0;
            for(int n = 0; n<N; n++)
            {
                double phi = 2*Math.PI*k*n/N;
                re += ogX[n]*Math.cos(phi);
                im -= ogX[n]*Math.sin(phi);
            }
            re/=N;
            im/=N;
            double freq = k;
            double amp = Math.sqrt(re*re+im*im);
            double phase = Math.atan2(im,re);
            double[]params = {re,im,freq,amp, phase};
            X[k] = params;
        }
        // for(int a = 0;a<5;a++)
        // System.out.println(X[a][3]);

        return X;
    }
}