/**
 * Created by Kevin on 2017-04-16.
 */
public class Rectangle {
    double aX,aY,bX,bY;

    Rectangle(double ax,double ay,double bx,double by){
        aX = ax;
        aY = ay;
        bX = bx;
        bY = by;
    }

    public static boolean contains(Rectangle a, Rectangle b) {

        if (a.aX <= b.aX && a.aY >= b.aY && a.bX >= b.bX && a.bY <= b.bY) {
            return true;
        }
        return false;
    }
}
