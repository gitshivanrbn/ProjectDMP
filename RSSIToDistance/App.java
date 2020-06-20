import static java.lang.Math.sqrt;

public class App {
    public static void main(String[] args) {
        ThreeCircleIntersect tci = new ThreeCircleIntersect();
//        tci.calculateThreeCircleIntersection(1.0, 3.0, sqrt(2.0),
//                                             3.0, 2.0, 1.0,
//                                             1.0, 1.0, sqrt(2.0));
        System.out.println(ThreeCircleIntersect.rssiToDistance(73) + " meters");
    }
}
