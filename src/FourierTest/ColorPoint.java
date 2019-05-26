package FourierTest;

import java.awt.*;

public class ColorPoint extends Point {

    private Color color;

    public ColorPoint(int x, int y, Color color){
        super(x, y);
        this.color = color;
    }

    public double distanceTo(Point other){
        return Math.sqrt(Math.pow(other.getX() - this.getX(), 2) + Math.pow(other.getY() - this.getY(), 2));
    }

    public int colorDiff(ColorPoint other){
        return (Math.abs(other.getColor().getRed() - this.getColor().getRed())) + Math.abs((other.getColor().getBlue() - this.getColor().getBlue()
        )) + Math.abs((other.getColor().getGreen() - this.getColor().getGreen()));
    }
    public Color getColor(){
        return color;
    }
    public boolean isShadeOf(Color color, int threshold){
        return (Math.abs(this.getColor().getRed() - color.getRed()) + Math.abs(this.getColor().getGreen() - color.getGreen()) +
                Math.abs(this.getColor().getBlue() - color.getBlue()) <= threshold);
    }
}
