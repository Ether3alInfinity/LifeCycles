package FourierTest;

import java.awt.*;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.*;


public class FourierProcessing {

    private static int maxCount, concentration, crowdArea, stepSize, blurX, blurY;


    private boolean clicked = false;
    private ArrayList<Point> points = new ArrayList<Point>();
    //canny
    private static final int MAX_LOW_THRESHOLD = 300;
    private static final int RATIO = 3;
    private static final int KERNEL_SIZE = 3;
    private static Size BLUR_SIZE;
    private int lowThresh = 0;
    private int threshold ;
    //sobel
    int scale = 1;
    int delta = 0;
    int ddepth = CvType.CV_16S;
    //laplace

    private Mat src;
    private Mat srcBlur = new Mat();
    private Mat detectedEdges = new Mat();
    private Mat dst = new Mat();
    private JFrame frame;
    private JFrame intake;
    private JRadioButton canny;
    private JRadioButton sobel;
    private JRadioButton laplace;
    private int edgeLord = -1;
    private int runMode = 0;
    private JLabel imgLabel;
    private JLabel picture;

    private String Laplacian = "Laplacian";
    private String Canny = "Canny";
    private String Sobel = "Sobel";

    private BufferedImage saved, img2;
    private String imagePath;

    public boolean triggered = false;
    public FourierProcessing(){
        try {
            Scanner key = new Scanner(new File("src/FourierTest/config.txt"));
            imagePath = key.nextLine();
            runMode = Integer.parseInt(key.nextLine().split(": ")[1]);
            maxCount = Integer.parseInt(key.nextLine().split(": ")[1]);
            concentration = Integer.parseInt(key.nextLine().split(": ")[1]);
            crowdArea = Integer.parseInt(key.nextLine().split(": ")[1]);
            stepSize = Integer.parseInt(key.nextLine().split(": ")[1]);
            blurX = Integer.parseInt(key.nextLine().split(": ")[1]);
            blurY = Integer.parseInt(key.nextLine().split(": ")[1]);
            threshold = Integer.parseInt(key.nextLine().split(": ")[1]);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        BLUR_SIZE = new Size(blurX,blurY);
        // Loads the source image
        src = Imgcodecs.imread(imagePath);
        if (src.empty()) {
            System.out.println("Empty image: " + imagePath);
            System.exit(0);
        }

        /**INPUT SELECTION*/
        intake = new JFrame("User Input");
        canny = new JRadioButton(Canny);
        sobel = new JRadioButton(Sobel);
        laplace = new JRadioButton("None");
        ButtonGroup edgeLords = new ButtonGroup();
        JPanel edgePanel = new JPanel(new GridLayout(2, 2));
        radioButtonSetup(canny, edgeLords, edgePanel);
        radioButtonSetup(sobel, edgeLords, edgePanel);
        radioButtonSetup(laplace, edgeLords, edgePanel);
        canny.addActionListener(e -> {
            System.out.println(e.getActionCommand());
            picture.setIcon(createImageIcon(
                    e.getActionCommand()
                            + ".jpg"));
            edgeLord = 0;
            System.out.println(edgeLord);
        });
        sobel.addActionListener(e -> {
            System.out.println(e.getActionCommand());
            picture.setIcon(createImageIcon(
                    e.getActionCommand()
                            + ".jpg"));
            edgeLord = 1;
            System.out.println(edgeLord);
        });
        laplace.addActionListener(e -> {
            System.out.println(e.getActionCommand());
            /*picture.setIcon(createImageIcon(
                    e.getActionCommand()
                            + ".jpg"));*/
            edgeLord = 3;
            System.out.println(edgeLord);
        });
        if (runMode == 1){
            triggered = true;
            loadSave();
        }

        else {
            //picture label
            picture = new JLabel(createImageIcon(Canny
                    + ".jpg"));
            intake.getContentPane().add(edgePanel, 0);
            edgePanel.add(picture, 1);
            intake.setResizable(true);
            intake.setPreferredSize(new Dimension(600, 600));
            intake.setLocation(700, 75);
            intake.setVisible(true);
            intake.pack();


            /**EDGE MAP*/
            // Create and set up the window.
            frame = new JFrame("Edge Map");
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Set up the content pane.
            BufferedImage img = (BufferedImage) HighGui.toBufferedImage(src);
            addComponentsToPane(frame.getContentPane(), img);
            // Use the content pane's default BorderLayout. No need for
            // setLayout(new BorderLayout());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (!triggered) {
                        triggered = true;
                        System.out.println("WindowClosingDemo.windowClosing");
                        frame = new JFrame("Edge Map (Canny detector demo)");
                        try {
                            PrintWriter outpoot = new PrintWriter(new File("src/FourierTest/points.txt"));
                            for (Point point : points) {
                                outpoot.println(point.getX() + "," + point.getY());
                            }
                            outpoot.close();
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                        if (edgeLord > -1) {
                            System.out.println("wow");
                            runEpi();
                        }
                    }
                }
            });

            // Display the window.
            frame.pack();
            frame.setVisible(true);
        }

    }
    public void loadSave(){
        System.out.println("Loading Save...");
        try{
            Scanner inpoot = new Scanner(new File("src/FourierTest/points.txt"));
            while(inpoot.hasNextLine()){
                String[]point = inpoot.nextLine().split(",");
                points.add(new Point((int)Math.round(Double.parseDouble(point[0])), (int)Math.round(Double.parseDouble(point[1]))));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        runEpi();
    }

    public void runEpi(){
        System.out.println("kangaroo");
        double angle = 0;
        int skip = 0;
        double[] x = new double[points.size()];
        double[] y = new double[points.size()];
        for (int a = 0; a < points.size(); a += 1)
        // y[a] = (int)(Math.random()*101);
        {
            angle = a;
            //x[a] = 50*Math.sin(3*Math.toRadians(angle));
            //y[a] = 50*Math.cos(3*Math.toRadians(angle));
            x[a] = points.get(a + skip).getX();
            y[a] = points.get(a + skip).getY();
            //angle+=0.02;
        }
        //int [] y = {100,100,100,-100,-100,-100,100,100,100};
        double[][] fourierX = Fourier.DFT(x);
        double[][] fourierY = Fourier.DFT(y);

        fourierX = sort(fourierX);
        fourierY = sort(fourierY);
        // new EpicycleGenerator(fourierX,fourierY);
        EpicycleGenerator gen = new EpicycleGenerator();
        gen.addEpicylce(600, 100, 0, fourierY);
        gen.addEpicylce(100, 600, Math.PI / 2, fourierX);


        //new EpicycleGenerator(100,200,0,fourierX);
        // new EpicycleGenerator(100,400,Math.PI/2,fourierY);
    }

    public double[][] sort(double[][] arr){
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


    public ArrayList<Point> getPoints(){
        return points;
    }
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FourierProcessing.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    private void radioButtonSetup(JRadioButton button, ButtonGroup group, JPanel panel){
        group.add(button);
        panel.add(button);
    }

    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("MinThresh, Delta:"));

            JSlider slider = new JSlider(0, MAX_LOW_THRESHOLD, 0);
            slider.setMajorTickSpacing(10);
            slider.setMinorTickSpacing(5);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.addChangeListener(e -> {
                JSlider source = (JSlider) e.getSource();
                lowThresh = source.getValue();
                update();
            });
            sliderPanel.add(slider);





        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));

        pane.add(imgLabel, BorderLayout.CENTER);
    }
    BufferedImage img;
    private int mouseX = 0;
    private int mouseY = 0;
    private int sobeled = 0;

    private void update(){
        System.out.println("updated");
        if (!clicked) {

            dst = new Mat(src.size(), CvType.CV_8UC1, Scalar.all(0));
            switch (edgeLord) {
                case 0: {
                    Imgproc.blur(src, srcBlur, BLUR_SIZE);
                    Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
                    src.copyTo(dst, detectedEdges);
                }
                break;
                case 1: {

                    Mat grad = src;
                    Mat src_gray = new Mat();
                    Mat grad_x = new Mat(), grad_y = new Mat();
                    Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();
                    if (sobeled == 0) {
                        sobeled = 1;
                        // Remove noise by blurring with a Gaussian filter ( kernel size = 3 )
                        Imgproc.GaussianBlur( src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );
                        // Convert the image to grayscale
                        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_RGB2GRAY);

                        //Imgproc.Scharr( src_gray, grad_x, ddepth, 1, 0, scale, delta, Core.BORDER_DEFAULT );
                        Imgproc.Sobel(src_gray, grad_x, ddepth, 1, 0, 3, scale, delta, Core.BORDER_DEFAULT);
                        //Imgproc.Scharr( src_gray, grad_y, ddepth, 0, 1, scale, delta, Core.BORDER_DEFAULT );
                        Imgproc.Sobel(src_gray, grad_y, ddepth, 0, 1, 3, scale, delta, Core.BORDER_DEFAULT);
                        // converting back to CV_8U
                        Core.convertScaleAbs(grad_x, abs_grad_x);
                        Core.convertScaleAbs(grad_y, abs_grad_y);
                        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);
                        Imgproc.threshold(grad, grad, (int)((double)threshold/3), 255, THRESH_TOZERO);


                    }

                    grad.copyTo(dst, new Mat());
                }
                break;
                case 3: src.copyTo(dst, new Mat());
            }




            img = (BufferedImage) HighGui.toBufferedImage(dst);
            imgLabel.setIcon(new ImageIcon(img));


            imgLabel.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!clicked) {
                        int packedInt = img.getRGB(e.getX(), e.getY());
                        Color color = new Color(packedInt, true);
                        System.out.println("Red:   " + color.getRed());
                        System.out.println("Green: " + color.getGreen());
                        System.out.println("Blue:  " + color.getBlue());
                        //System.out.println();
                        System.out.println("mouse");
                        //System.out.println("diffX: " + (e.getX() - Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getX()))));
                        //System.out.println("diffY: " + (e.getY() - Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getY()))));
                        //System.out.println(e.getX());
                        //System.out.println(e.getY());
                        System.out.println(Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getX())));
                        System.out.println(Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getY())));
                        System.out.println("frame");
                        System.out.println(frame.getX());
                        System.out.println(frame.getY());
                        System.out.println("imgLabel");
                        System.out.println(imgLabel.getX());
                        System.out.println(imgLabel.getY());
                        System.out.println();
                    }
                }
            });
//test
            imgLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!clicked) {
                        System.out.println("clicked");
                        mouseX = Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getX())) - frame.getX() - imgLabel.getX() - 10;
                        mouseY = Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getY())) - frame.getY() - imgLabel.getY() - 38;
                        System.out.println(Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getX())));
                        System.out.println(frame.getX());
                        System.out.println(imgLabel.getX());
                        System.out.println("X: " + mouseX);
                        System.out.println(Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getY())));
                        System.out.println(frame.getY());
                        System.out.println(imgLabel.getY());
                        System.out.println("y: " + mouseY);

                        int packedInt = img.getRGB(e.getX(), e.getY());
                        Color color = new Color(packedInt, true);
                        System.out.println(color);
                        System.out.println(Color.BLACK);
                        if (!new ColorPoint(0,0,color).isShadeOf(Color.BLACK, threshold)) {
                            clicked = true;
                            System.out.println("White Pixel Detected");
                            try {
                                clean();
                                try {
                                    startDFSJump(mouseX, mouseY);
                                }catch (StackOverflowError err){
                                    System.out.println("KAMISAMA");
                                    System.exit(0);
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        } else System.out.println("Color Pixel not detected, try again");
                    }
                }
            });
            try {
                ImageIO.write((RenderedImage) img, "jpg", new File("saved.jpg"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    int count = 0;
    public void startDFSJump(int x, int y) throws IOException{
        count++;
        if (count > maxCount) {
            return;
        }
        saved = (BufferedImage)ImageIO.read(new File("saved.jpg"));
        System.out.println("                                                 Starting DFS Jump #" + count + "...");
        double minJumpDist = (double)FourierProcessing.stepSize/2;
        double maxDist = 0;
        ColorPoint here = new ColorPoint(x, y, new Color(saved.getRGB(x, y)));
        points.add(here);
        img2 = new BufferedImage(saved.getWidth(), saved.getHeight(), BufferedImage.TYPE_INT_RGB);
        try {
            img2 = ImageIO.read(new File("saved2.jpg"));
        } catch (FileNotFoundException exc) {
            clean();
        }
        spread(img2, Color.WHITE, x, y, 0, 0);
        ImageIO.write((RenderedImage) img2, "jpg", new File("saved2.jpg"));
        for (int i=(x-FourierProcessing.stepSize)<0?-x:-FourierProcessing.stepSize;
             i<(((x+FourierProcessing.stepSize)>=saved.getWidth())?(saved.getWidth()-x):FourierProcessing.stepSize+1); i++) {
            for (int j = (y - FourierProcessing.stepSize) < 0 ? -y : -FourierProcessing.stepSize;
                 j < (((y + FourierProcessing.stepSize) >= saved.getHeight()) ? (saved.getHeight() - y) : FourierProcessing.stepSize + 1); j++) {
                ColorPoint temp = new ColorPoint(x+i, y+j, new Color(saved.getRGB(x+i, y+j)));
                if((i!=0||j!=0) && (!temp.isShadeOf(Color.BLACK, threshold))) {



                    //System.out.println(here.distanceTo(temp));
                    //System.out.println("X: " + x);
                    //System.out.println("Y: " + y);

                    //if (here.distanceTo(temp) > maxDist) {
                        //maxDist = here.distanceTo(temp);
                        if (!existPoint(x + i, y + j) && numInRange(x+i, y+j, crowdArea) <= concentration) {

                            startDFSJump(x + i, y + j);
                        }
                    //}
                }
            }
        }
    }

    private boolean existPoint(int x, int y){
        for (Point p : points){
            if (p.getX() == x && p.getY() == y) return true;
        }return false;
    }
    private int numInRange(int x, int y, int range){
        int counter = 0;
        for (int i=(x-range)<0?-x:-range; i<(((x+range)>=img2.getWidth())?(img2.getWidth()-x):range+1); i++) {
            for (int j = (y - range) < 0 ? -y : -range; j < (((y + range) >= img2.getHeight()) ? (img2.getHeight() - y) : range + 1); j++) {
                if (existPoint(x+i, y+j))
                    counter++;
            }
        }return counter;
    }
    private void clean() throws  IOException{
        BufferedImage OG = ImageIO.read(new File("saved.jpg"));
        BufferedImage img = new BufferedImage(OG.getWidth(),OG.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                img.setRGB(i, j, Color.BLACK.getRGB());

            }
        }
        ImageIO.write(img, "jpg", new File("saved2.jpg"));
    }











    private void spread(BufferedImage img, Color color, int x, int y, int domain, int range){
        for (int i=(x-domain)<0?-x:-domain; i<(((x+domain)>=img.getWidth())?(img.getWidth()-x):domain+1); i++)
            for (int j=(y-range)<0?-y:-range; j<(((y+range)>=img.getHeight())?(img.getHeight()-y):range+1); j++)
                img.setRGB(x + i, y + j, color.getRGB());
    }

    public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(FourierProcessing::new);
        System.out.println("Hello World!");

    }







}