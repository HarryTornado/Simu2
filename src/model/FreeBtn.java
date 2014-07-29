package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import component.Component;

@SuppressWarnings("serial")
public class FreeBtn extends JButton {
	public FreeBtn(Component e) {
		int ww = 150;
		int hh = 80;
		BufferedImage bi = new BufferedImage(ww, hh,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.lightGray);
		g.fillRect(0, 0, ww, hh);
		g.setColor(Color.black);
		int x = (ww - e.getWidth()) / 2;
		int y = (hh - e.getHeight()) / 2;
		e.setXY(x, y);
		e.draw(g);

		this.setIcon(new ImageIcon(bi));
	}
}
