package connector;

import java.awt.Color;
import java.awt.Graphics2D;

public class DirectLine extends ConnectLine {
	public DirectLine(Connector from, Connector to) {
		super(from, to);
	}

	@Override
	public void draw(Graphics2D g, Color color) {
		drawHandle(g);
		ConnectLine.drawLine(g, from.getDirectP(), to.getDirectP());
	}
}
