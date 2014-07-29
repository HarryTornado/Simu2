package component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import connector.Connector;
import connector.InputConnector;

@SuppressWarnings("serial")
class MenuEvent extends ActionEvent {
	private Point point;

	public MenuEvent(JMenuItem dflt, Point point) {
		super(dflt, 0, null);
		this.point = point;
	}

	public Point getPoint() {
		return point;
	}
}

/**
 * @author jyk
 */
public @SuppressWarnings("serial")
class ElementMenu extends JPopupMenu {
	private Component element;
	private ElementPanel panel;
	private JMenuItem dflt;

	public void fireDflt(Point mousePoint) {
		if (dflt == null)
			return;
		ActionListener[] al = dflt.getActionListeners();
		if (al != null && al.length > 0)
			al[0].actionPerformed(new MenuEvent(dflt, mousePoint));
	}

	public ElementMenu(Component e) {
		this.element = e;

		JMenuItem tm = new JMenuItem("Menu for " + e.getName());
		tm.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		tm.setForeground(Color.red);
		tm.setBackground(Color.cyan);
		add(tm);
		tm.setEnabled(false);

		add(new JPopupMenu.Separator());

		boolean has = false;
		if (e instanceof Plot || e instanceof StateSpaceTrajectory) {
			makeShowItem();
			has = true;
		}

		if (e instanceof Editable) {
			if (has)
				add(new JPopupMenu.Separator());
			makeEditItem();
			has = true;
		}

		if (!(e instanceof Plot || e instanceof StateSpaceTrajectory)) {
			if (has)
				add(new JPopupMenu.Separator());
			makeFlipHItem();
			makeFlipVItem();
		}

		makeDeleteItem();

		// makeOutputItems();

		makeInputItems();
	}

	private void makeInputItems() {
		boolean first = true;
		for (Connector c : element.getConnectors()) {
			if (c instanceof InputConnector) {
				InputConnector ic = (InputConnector) c;
				if (ic.isConnected()) {
					if (first) {
						first = false;
						add(new JPopupMenu.Separator());
					}
					ComponentMenuItem item = new ComponentMenuItem(
							"disconnect (" + ic.getName() + ") from "
									+ ic.getSource(), ic);
					add(item);
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							InputConnector ic = (InputConnector) ((ComponentMenuItem) e
									.getSource()).getConnector();
							ic.disconnect();
							setVisible(false);
							panel.repaint();
						}
					});
				}
			}
		}
	}

	// private void makeOutputItems() {
	// boolean first = true;
	// if (element instanceof OutputStub)
	// return;
	// for (Connector c : element.getConnectors()) {
	// if (c instanceof OutputConnector) {
	// if (first) {
	// first = false;
	// add(new JPopupMenu.Separator());
	// }
	// ComponentMenuItem item = new ComponentMenuItem("connect ("
	// + c.getName() + ") to ...", c);
	// add(item);
	// item.addActionListener(new ActionListener() {
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// ComponentMenuItem item = (ComponentMenuItem) e
	// .getSource();
	// panel.setSelectedOC((OutputConnector) item
	// .getConnector(), element);
	// }
	// });
	// }
	// }
	// }

	private void makeDeleteItem() {
		if (element instanceof Stub)
			return;
		add(new JPopupMenu.Separator());
		JMenuItem delete = new JMenuItem("delete");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.remove(ElementMenu.this.element);
			}
		});
		add(delete);
	}

	private void makeFlipVItem() {
		JMenuItem flipV = new JMenuItem("flip vertically");
		flipV.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ElementMenu.this.element.flipV();
				if (panel != null)
					panel.repaint();
			}
		});
		add(flipV);
	}

	private void makeFlipHItem() {
		JMenuItem flipH = new JMenuItem("flip horizontally");
		flipH.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ElementMenu.this.element.flipH();
				if (panel != null)
					panel.repaint();
			}
		});

		add(flipH);
	}

	private void makeEditItem() {
		JMenuItem editItem = new JMenuItem("edit...");
		editItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Point p = null;
				if (e instanceof MenuEvent)
					p = ((MenuEvent) e).getPoint();
				((Editable) ElementMenu.this.element).showDialog(p);

			}
		});
		add(editItem);
		if (dflt == null)
			dflt = editItem;
	}

	private void makeShowItem() {
		JMenuItem showItem = new JMenuItem("show");
		showItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ElementMenu.this.element instanceof Plot) {
					((Plot) ElementMenu.this.element).showResult();
				} else {
					((StateSpaceTrajectory) ElementMenu.this.element)
							.showResult();
				}
			}
		});
		add(showItem);
		dflt = showItem;
	}

	/**
	 * @param panel
	 * @uml.property name="panel"
	 */
	public void setPanel(ElementPanel panel) {
		this.panel = panel;
	}
}
