package help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import net.effortech.harry.swing.UrlDialog;

import component.ElementLists;

public class Help {
	public static void showOnFormula() {
		try {
			UrlDialog ud = new UrlDialog(null, 800, 600, "On formula",
					Help.class.getResource("doc/formula/help.html"));
			ud.setVisible(true);
		} catch (IOException e) {
		}
	}

	public static void showOnTf() {
		try {
			UrlDialog ud = new UrlDialog(null, 800, 600, "On formula",
					Help.class.getResource("doc/tf/tf.html"));
			ud.setVisible(true);
		} catch (IOException e) {
		}
	}

	public static void showAbout() {
		try {
			UrlDialog ud = new UrlDialog(null, 650, 510, "About us", Help.class
					.getResource("doc/about/about.html"));
			ud.setVisible(true);
		} catch (IOException e) {
		}
	}

	public static JMenu makeHelpMenu() {
		JMenu menu = new JMenu("help");

		JMenuItem formulaItem = new JMenuItem("on formula");
		formulaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Help.showOnFormula();
			}
		});

		menu.add(formulaItem);

		JMenuItem tfItem = new JMenuItem("on transfer function");
		tfItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Help.showOnTf();
			}
		});

		menu.add(tfItem);

		menu.add(new JSeparator());

		try {
			Class.forName("component.ElementLists");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (ElementLists el : ElementLists.values()) {
			menu.add(el.getHelpItem());
		}
		menu.add(new JSeparator());

		JMenuItem aboutItem = new JMenuItem("about us");
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Help.showAbout();
			}
		});

		menu.add(aboutItem);
		return menu;
	}
}
