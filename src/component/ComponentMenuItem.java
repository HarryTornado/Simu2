package component;

import javax.swing.JMenuItem;

import connector.Connector;

@SuppressWarnings("serial")
public class ComponentMenuItem extends JMenuItem {
	private Connector connector;

	public ComponentMenuItem(String text, Connector c) {
		super(text);
		this.connector = c;
	}

	public Connector getConnector() {
		return connector;
	}
}
