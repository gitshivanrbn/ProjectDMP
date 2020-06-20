import static java.lang.Math.floor;
import static java.lang.Math.round;

public class ThreeCircleIntersect {
    private static final double EPSILON = 0.000001;

    public int hexToDec(String hex) {
        return Integer.parseInt(hex, 16) - 65400;
    }

    static double rssiToDistance(int RSSI)
    {
        double rssi = RSSI / 81.868;
        double xPre = 0.2;

        // smaller eps, denotes more accuracy
        double eps = 0.000001;

        // initializing difference between two
        // roots by INT_MAX
        double delX = 2147483647;

        // xK denotes current value of x
        double xK = 0.0;

        // loop untill we reach desired accuracy
        while (delX > eps)
        {
            // calculating current value from previous
            // value by newton's method
            xK = ((0.209 - 1.0) * xPre + rssi / Math.pow(xPre, 0.209 - 1)) / (double) 0.209;
            delX = Math.abs(xK - xPre);
            xPre = xK;
        }

        return floor(1 / xK);
    }

    public boolean calculateThreeCircleIntersection(double x0, double y0, double r0,
                                                     double x1, double y1, double r1,
                                                     double x2, double y2, double r2)
    {
        double a, dx, dy, d, h, rx, ry;
        double point2_x, point2_y;

        // dx and dy are the vertical and horizontal distances betweenthe circle centers.
        dx = x1 - x0;
        dy = y1 - y0;

        /* Determine the straight-line distance between the centers. */
        d = Math.sqrt((dy*dy) + (dx*dx));

        if (d > (r0 + r1)) return false; //No intersect
        if (d < Math.abs(r0 - r1)) return false; //Circles inside each other

        /* "point 2" is the point where the line through the circle
         * intersection points crosses the line between the circle centers.
         */

        /* Determine the distance from point 0 to point 2. */
        a = ((r0*r0) - (r1*r1) + (d*d)) / (2.0 * d) ;

        /* Determine the coordinates of point 2. */
        point2_x = x0 + (dx * a/d);
        point2_y = y0 + (dy * a/d);

        // Determine the distance from point 2 to either of the intersection points.
        h = Math.sqrt((r0*r0) - (a*a));

        // Now determine the offsets of the intersection points from point 2.
        rx = -dy * (h/d);
        ry = dx * (h/d);

        // Determine the absolute intersection points.
        double intersectionPoint1_x = point2_x + rx;
        double intersectionPoint2_x = point2_x - rx;
        double intersectionPoint1_y = point2_y + ry;
        double intersectionPoint2_y = point2_y - ry;

        // Determine if circle 3 intersects at either of the above intersection points.
        dx = intersectionPoint1_x - x2;
        dy = intersectionPoint1_y - y2;
        double d1 = Math.sqrt((dy*dy) + (dx*dx));

        dx = intersectionPoint2_x - x2;
        dy = intersectionPoint2_y - y2;
        double d2 = Math.sqrt((dy*dy) + (dx*dx));

        if(Math.abs(d1 - r2) < EPSILON) {
            System.out.println("Intersection: (" + intersectionPoint1_x + "," + intersectionPoint1_y + ")");

        }
        else if(Math.abs(d2 - r2) < EPSILON) {
            System.out.println("Intersection2: (" + round(intersectionPoint2_x) + "," + round(intersectionPoint2_y) + ")");
        }
        else {
            System.out.println("nada");
        }
        return true;
    }
}
