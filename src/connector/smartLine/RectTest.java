package connector.smartLine;

import java.awt.Point;
import java.awt.Rectangle;

public class RectTest {
	public static void main(String[] args) {

		Rectangle r = new Rectangle(10, 10, 100, 100);

		System.out.println("r.contains(10, 90):"
				+ r.contains(new Point(90, 9)));

		Rectangle r2 = new Rectangle(9, 9, 200, 1);
		System.out.println("r.intersetcs(r2)" + r.intersects(r2));
	}
}
