package component;

import help.Help;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import net.effortech.harry.swing.UrlDialog;

/**
 * @author jyk
 */
public enum ElementLists {
	add(Add.class), aproximate_difference(ApproxDif.class), dynamic_system(
			DynaSys.class), fncOne(FuncOne.class), fncTwo(FuncTwo.class), integration(
			Integration.class), mult(Mult.class), multiplexer(Multiplexer.class), plotter(
			Plot.class),
	source(Source.class), // iStub(InputStub.class), oStub(OutputStub.class),
	sub(Sub.class), sub_system(SubSystem.class), trajectory(
					StateSpaceTrajectory.class);

	/**
	 * @uml.property name="cls"
	 */
	@SuppressWarnings("unchecked")
	private Class cls;
	private ImageIcon icon;

	@SuppressWarnings("unchecked")
	private ElementLists(Class cls) {
		this.cls = cls;
	}

	/**
	 * @return
	 * @uml.property name="btn"
	 */
	public EleBtn getBtn(final ElementPanel panel) {
		EleBtn btn = new EleBtn(cls);

		btn.setIcon(getIcon());

		btn.setToolTipText(getToolTipText(cls));

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EleBtn btn = (EleBtn) e.getSource();
				try {
					Component ele = (Component) btn.getCls().newInstance();
					// ele.scale(scale);
					panel.add(ele);
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		return btn;
	}

	/**
	 * @return
	 * @uml.property name="cls"
	 */
	@SuppressWarnings("unchecked")
	public Class getCls() {
		return cls;
	}

	/**
	 * @return
	 * @uml.property name="helpItem"
	 */
	public JMenuItem getHelpItem() {
		return makeHelpItem();
	}

	/**
	 * @return
	 * @uml.property name="icon"
	 */
	private ImageIcon getIcon() {
		if (icon == null) {
			try {
				Component element = (Component) cls.newInstance();
				element.scale(.5);
				BufferedImage bi = new BufferedImage(70, 30,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = (Graphics2D) bi.getGraphics();
				g.setColor(Color.black);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				int x = (bi.getWidth() - element.getWidth()) / 2;
				int y = (bi.getHeight() - element.getHeight()) / 2;
				element.setXY(x, y);
				element.draw(g);
				icon = new ImageIcon(bi);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return icon;
	}

	/**
	 * @return
	 * @uml.property name="item"
	 */
	public JMenuItem getItem(final ElementPanel panel) {
		EleItem item = new EleItem(cls);

		item.setIcon(getIcon());

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EleItem btn = (EleItem) e.getSource();
				try {
					Component ele = (Component) btn.getCls().newInstance();
					panel.add(ele);
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		item.setToolTipText(getToolTipText(cls));

		return item;
	}

	@SuppressWarnings("unchecked")
	private String getToolTipText(Class cls) {
		String help = null;
		try {
			StringBuffer sb;
			URL url = Help.class.getResource("tooltips/" + cls.getSimpleName()
					+ ".html");
			BufferedReader br = new BufferedReader(new InputStreamReader(url
					.openStream()));
			sb = new StringBuffer();
			String s = null;
			while ((s = br.readLine()) != null)
				sb.append(s);
			br.close();
			help = sb.toString();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return help;
	}

	private JMenuItem makeHelpItem() {
		JMenuItem helpItem = new JMenuItem("help on " + this.name());
		helpItem.setIcon(getIcon());
		helpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					UrlDialog ud = new UrlDialog(null, 800, 600, "On "
							+ ElementLists.this.name(), Help.class
							.getResource("module/" + cls.getSimpleName()
									+ ".html"));
					ud.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null,
							"Error in making help item for " + this
									+ "\nMessage: " + e1.getMessage());
				}
			}
		});
		return helpItem;
	}

	public static List<JComponent> getBtnsForModule(ElementPanel panel) {
		List<JComponent> lst = new ArrayList<JComponent>();
		lst.add(ElementLists.sub_system.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.source.getBtn(panel));
		lst.add(new JToolBar.Separator());
		// lst.add(ElementLists.iStub.getBtn(panel));
		// lst.add(ElementLists.oStub.getBtn(panel));
		// lst.add(new JToolBar.Separator());
		lst.add(ElementLists.add.getBtn(panel));
		lst.add(ElementLists.sub.getBtn(panel));
		lst.add(ElementLists.mult.getBtn(panel));
		lst.add(ElementLists.multiplexer.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.fncOne.getBtn(panel));
		lst.add(ElementLists.fncTwo.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.dynamic_system.getBtn(panel));
		lst.add(ElementLists.aproximate_difference.getBtn(panel));
		lst.add(ElementLists.integration.getBtn(panel));
		return lst;
	}

	public static List<JComponent> getBtnsForSimu(ElementPanel panel) {
		List<JComponent> lst = new ArrayList<JComponent>();
		lst.add(ElementLists.sub_system.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.source.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.add.getBtn(panel));
		lst.add(ElementLists.sub.getBtn(panel));
		lst.add(ElementLists.mult.getBtn(panel));
		lst.add(ElementLists.multiplexer.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.fncOne.getBtn(panel));
		lst.add(ElementLists.fncTwo.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.dynamic_system.getBtn(panel));
		lst.add(ElementLists.aproximate_difference.getBtn(panel));
		lst.add(ElementLists.integration.getBtn(panel));
		lst.add(new JToolBar.Separator());
		lst.add(ElementLists.trajectory.getBtn(panel));
		lst.add(ElementLists.plotter.getBtn(panel));
		return lst;
	}

	public static ElementLists getByName(String name) {
		for (ElementLists cp : ElementLists.values()) {
			if (cp.name().equals(name))
				return cp;
		}
		return null;
	}

	public static List<JComponent> getMenuItemsForModule(ElementPanel panel) {
		List<JComponent> lst = new ArrayList<JComponent>();
		lst.add(ElementLists.sub_system.getItem(panel));
		lst.add(new JSeparator());

		lst.add(ElementLists.source.getItem(panel));
		lst.add(new JSeparator());

		// lst.add(ElementLists.iStub.getItem(panel));
		// lst.add(ElementLists.oStub.getItem(panel));
		// lst.add(new JSeparator());
		lst.add(ElementLists.add.getItem(panel));
		lst.add(ElementLists.sub.getItem(panel));
		lst.add(ElementLists.mult.getItem(panel));
		lst.add(ElementLists.multiplexer.getItem(panel));
		lst.add(new JSeparator());
		lst.add(ElementLists.fncOne.getItem(panel));
		lst.add(ElementLists.fncTwo.getItem(panel));
		lst.add(new JSeparator());
		lst.add(ElementLists.dynamic_system.getItem(panel));
		lst.add(ElementLists.aproximate_difference.getItem(panel));
		lst.add(ElementLists.integration.getItem(panel));
		return lst;
	}

	public static List<JComponent> getMenuItemsForSimu(ElementPanel panel) {
		List<JComponent> lst = new ArrayList<JComponent>();
		lst.add(ElementLists.sub_system.getItem(panel));
		lst.add(new JSeparator());

		lst.add(ElementLists.source.getItem(panel));
		lst.add(new JSeparator());
		lst.add(ElementLists.add.getItem(panel));
		lst.add(ElementLists.sub.getItem(panel));
		lst.add(ElementLists.mult.getItem(panel));
		lst.add(ElementLists.multiplexer.getItem(panel));
		lst.add(new JSeparator());
		lst.add(ElementLists.fncOne.getItem(panel));
		lst.add(ElementLists.fncTwo.getItem(panel));
		lst.add(new JSeparator());
		lst.add(ElementLists.dynamic_system.getItem(panel));
		lst.add(ElementLists.aproximate_difference.getItem(panel));
		lst.add(ElementLists.integration.getItem(panel));
		lst.add(new JSeparator());
		lst.add(ElementLists.trajectory.getItem(panel));
		lst.add(ElementLists.plotter.getItem(panel));
		return lst;
	}
}
