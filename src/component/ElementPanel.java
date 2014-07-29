package component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import model.ConnectException;
import net.effortech.harry.swing.PopupDialog;

import component.param.Parameter;
import component.param.ParameterManager;

import connector.Connector;
import connector.DirectLine;
import connector.HVLine;
import connector.InputConnector;
import connector.OutputConnector;
import connector.intelligentLine.Inteline;
import connector.intelligentLine.IntelligentLine;
import connector.smartLine.SmartLine;

/**
 * @author jyk
 */
public @SuppressWarnings("serial")
class ElementPanel extends JPanel implements MouseListener, MouseMotionListener {
	/**
	 * @author JiHsianLee
	 */
	public enum LineType {
		direct, inteline, intelligent, smart;

		public static LineType getByName(String name) {
			for (LineType lt : values()) {
				if (lt.name().equals(name))
					return lt;
			}
			return null;
		}
	}

	private static final int maxSteps = 1000000;

	public transient static final PopupDialog pd = new PopupDialog(
			"Simulation", false, false);
	private transient ActionListener adjustListener;
	private List<InputConnector> connectables;

	// private transient EPConnectablesDialog connectablesDialog;
	private transient JPanel ctlPanel;
	private double dt = 0.01;

	private List<Component> elements = new ArrayList<Component>();

	private InputConnector enteredIC;

	private transient List<HVLine> hvLines;

	private LineType lineType = LineType.smart;

	private ActionListener listener;

	private double maxt = 10;

	private double mint = 0;
	private Point movingMousePoint;
	private int mxSave;
	private int mySave;

	private List<Parameter> params = new ArrayList<Parameter>();
	private transient Component selected;

	private OutputConnector selectedOC, enteredOC;

	private List<Stub> stubs = new ArrayList<Stub>();

	public ElementPanel() {
		this.setBackground(new Color(220, 228, 239));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setPreferredSize(new Dimension(2048, 2048));
		makeCb();
	}

	{
		pd.setSize(300, 200);
	}

	public void add(Component e) {
		if (selectedOC != null)
			selectedOC.setSelected(false);
		selectedOC = null;
		if (enteredIC != null)
			enteredIC.setSelected(false);
		enteredIC = null;
		if (e instanceof Stub) {
			if (stubs.size() < 4)
				stubs.add((Stub) e);
			else {
				JOptionPane.showMessageDialog(null,
						"You can not add more than 4 Stubs!");
				return;
			}
		}
		elements.add(e);
		e.setPanel(this);
		setSelected(e);
		changed();
		repaint();
		// connectablesDialog = null;
	}

	public void add2(Component e) {
		elements.add(e);
		e.setPanel(this);
		changed();
		// connectablesDialog = null;
	}

	private void adjustSize(int mx, int my) {
		this.setPreferredSize(new Dimension(mx, my));
		if (adjustListener != null)
			adjustListener.actionPerformed(null);
	}

	private void changed() {
		if (listener != null) {
			listener.actionPerformed(new ActionEvent(this, 0, null));
		}
	}

	public void clear() {
		elements = new ArrayList<Component>();
		params = new ArrayList<Parameter>();
		repaint();
		changed();
	}

	// /**
	// * @return
	// * @uml.property name="connectablesDialog"
	// */
	// public EPConnectablesDialog getConnectablesDialog() {
	// if (connectablesDialog == null)
	// connectablesDialog = new EPConnectablesDialog(this, null);
	// return connectablesDialog;
	// }

	public void editParams() {
		ParameterManager pm = new ParameterManager(params);
		pm.setVisible(true);
		params = pm.getParams();
		for (Component cp : elements) {
			if (cp instanceof SubSystem) {
				((SubSystem) cp).inherite();
			}
		}
	}

	/**
	 * @return
	 * @uml.property name="ctlPanel"
	 */
	public JPanel getCtlPanel() {
		return ctlPanel;
	}

	/**
	 * @return
	 * @uml.property name="elements"
	 */
	public List<Component> getElements() {
		return elements;
	}

	/**
	 * @return
	 * @uml.property name="hvLines"
	 */
	public List<HVLine> getHvLines() {
		return hvLines;
	}

	public List<Parameter> getParams() {
		return params;
	}

	public void inheriteParams(List<Parameter> ps) {
		for (Parameter p : ps) {
			if (!params.contains(p))
				params.add(p);
		}
	}

	public boolean isNonEmpty() {
		return (elements.size() > 0 || params.size() > 0);
	}

	private void makeCb() {
		ctlPanel = new JPanel();
		ctlPanel.setBorder(new TitledBorder(""));
		ButtonGroup bg = new ButtonGroup();

		for (LineType lt : LineType.values()) {
			JCheckBox cb = new JCheckBox(lt.name(), lt == lineType);
			bg.add(cb);
			ctlPanel.add(cb);
			cb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					lineType = LineType.getByName(e.getActionCommand());
					repaint();
				}
			});
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			for (Component ele : elements) {
				if (ele.contains(e.getPoint())) {
					setSelected(ele);
					repaint();
					ele.fireDefaultAction(new Point(e.getXOnScreen()
							+ ele.width / 2, e.getYOnScreen() + ele.height / 2));
					// repaint();
					break;
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selected != null) {
			int dx = e.getX() - mxSave;
			int dy = e.getY() - mySave;
			selected.move(dx, dy);
			repaint();
			changed();
			mxSave = e.getX();
			mySave = e.getY();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (selectedOC != null) {
			movingMousePoint = e.getPoint();
			if (enteredIC != null) {
				enteredIC.setSelected(false);
				enteredIC = null;
			}
			for (InputConnector ic : connectables) {
				if (ic.contains(movingMousePoint)) {
					enteredIC = ic;
					enteredIC.setSelected(true);
					break;
				}
			}
			repaint();
		} else {
			Point mp = e.getPoint();
			for (Component c : elements) {
				for (Connector ct : c.getConnectors()) {
					if (ct instanceof OutputConnector) {
						OutputConnector oc = (OutputConnector) ct;
						if (oc.contains(mp)) {
							if (enteredOC != null)
								enteredOC.setSelected(false);
							enteredOC = oc;
							enteredOC.setSelected(true);
							repaint();
							return;
						}
					}
				}
			}
			if (enteredOC != null)
				enteredOC.setSelected(false);
			enteredOC = null;
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (selectedOC != null) {
			if (enteredIC != null) {
				try {
					selectedOC.addConnected(enteredIC);
					selectedOC.setSelected(false);
					selectedOC = null;
					enteredIC.setSelected(false);
					enteredIC = null;
					repaint();
				} catch (ConnectException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			} else {
				selectedOC.setSelected(false);
				selectedOC = null;
				repaint();
			}
		} else if (e.isPopupTrigger()) {
			for (Component ele : elements) {
				if (ele.contains(e.getPoint())) {
					setSelected(ele);
					ele.showMenu(e.getX(), e.getY());
					changed();
					break;
				}
			}
		} else {
			mxSave = e.getX();
			mySave = e.getY();
			Component tmp = null;
			for (Component ele : elements) {
				if (ele.isInside(mxSave, mySave)) {
					tmp = ele;
					break;
				}
			}
			setSelected(tmp);
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (enteredOC != null) {
			this.setSelectedOC(enteredOC, enteredOC.getParent());
			enteredOC = null;
		} else if (e.isPopupTrigger()) {
			for (Component ele : elements) {
				if (ele.contains(e.getPoint())) {
					setSelected(ele);
					ele.showMenu(e.getX(), e.getY());
					changed();
					break;
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(Color.black);

		int mx = 0, my = 0;
		for (Component e : elements) {
			e.draw((Graphics2D) g);

			int x = e.getX() + e.getOWidth() * 2;
			int y = e.getY() + e.getOHeight() * 2;

			if (x > mx)
				mx = x;
			if (y > my)
				my = y;
			adjustSize(mx, my);
		}

		hvLines = new ArrayList<HVLine>();

		for (Component e : elements) {
			Color color1 = Color.black;
			if (e.isSelected())
				color1 = Color.blue;
			for (Connector c : e.getConnectors()) {
				if (c instanceof OutputConnector) {
					for (Connector to : ((OutputConnector) c).getConnected()) {
						Color color = color1;
						if (e.isSelected() == false
								&& to.getParent().isSelected())
							color = Color.red;
						if (lineType == LineType.direct)
							new DirectLine(c, to).draw(g2d, color);
						else if (lineType == LineType.smart)
							new SmartLine(c, to).draw(g2d, color);
						else if (lineType == LineType.intelligent) {
							IntelligentLine cln = new IntelligentLine(to, c);
							cln.draw(g2d, color);
							hvLines.addAll(cln.getHvLines());
						} else {
							new Inteline(c, to).draw(g2d, color);
						}
					}
				}
			}
		}

		if (selectedOC != null && movingMousePoint != null) {
			g2d.setColor(Color.magenta);
			g2d.drawLine(selectedOC.getXY().x, selectedOC.getXY().y,
					selectedOC.getDirectP().x, selectedOC.getDirectP().y);
			g2d.drawLine(selectedOC.getDirectP().x, selectedOC.getDirectP().y,
					movingMousePoint.x, movingMousePoint.y);
		}
	}

	@SuppressWarnings("unchecked")
	public void readFrom(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		List<Component> eles = (List<Component>) ois.readObject();
		params = (List<Parameter>) ois.readObject();
		ois.close();
		setElements(eles);
	}

	public void remove(Component e) {
		e.disconnect();
		elements.remove(e);
		repaint();
		// connectablesDialog = new EPConnectablesDialog(this, null);
		changed();
	}

	public void saveTo(File file) throws IOException {
		ObjectOutputStream dos = new ObjectOutputStream(new FileOutputStream(
				file));
		dos.writeObject(elements);
		dos.writeObject(params);
		dos.close();
	}

	public void setAdjustListener(ActionListener adjustListener) {
		this.adjustListener = adjustListener;
	}

	public void setElements(List<Component> eles) {
		elements = new ArrayList<Component>();

		for (Component cp : eles) {
			cp.setSelected(false);
			this.add2(cp);
			cp.recreate();
		}

		repaint();
	}

	public void setListener(ActionListener listener) {
		this.listener = listener;
	}

	private void setSelected(Component e) {
		if (selected != null)
			selected.setSelected(false);
		selected = e;
		if (selected != null)
			selected.setSelected(true);
		repaint();
	}

	public void setSelectedOC(OutputConnector selectedOC, Component comp) {
		if (this.selectedOC != null)
			this.selectedOC.setSelected(true);

		this.selectedOC = selectedOC;
		this.selectedOC.setSelected(true);

		connectables = new ArrayList<InputConnector>();

		for (Component e : getElements()) {
			if (e == comp)
				continue;
			if (e instanceof InputStub)
				continue;
			for (Connector c : e.getConnectors()) {
				if (c instanceof InputConnector)
					if (!((InputConnector) c).isConnected())
						connectables.add((InputConnector) c);
			}
		}
	}

	public void setTime(double mint, double maxt, double dt) throws Exception {
		if (mint >= maxt)
			throw new Exception("mint should be smaller than maxt!");
		if ((maxt - mint) / dt > maxSteps)
			throw new Exception("dt should be large enough!");

		this.mint = mint;
		this.maxt = maxt;
		this.dt = dt;
	}

	public void simulate() {
		pd.setMinMax(0, (int) ((maxt - mint) / dt));
		pd.append("\n\n" + new Date() + "\nwait, calculateing...");

		for (Component e : elements) {
			e.reset();
			if (e instanceof SubSystem)
				((SubSystem) e).setDt(dt);
			if (e instanceof DynaSys)
				((DynaSys) e).setDt(dt);
			else if (e instanceof ApproxDif)
				((ApproxDif) e).setDt(dt);
			else if (e instanceof Integration)
				((Integration) e).setDt(dt);
		}

		int pv = 1;
		for (double t = mint; t <= maxt; t += dt) {
			pd.setValue(pv++);
			for (Component e : elements) {
				if (e instanceof SubSystem)
					e.execute(t);
			}
			for (Component e : elements) {
				if (e instanceof DynaSys || e instanceof ApproxDif
						|| e instanceof Integration)
					e.execute(t);
			}
			for (Component e : elements) {
				if (e instanceof FuncOne || e instanceof FuncTwo)
					e.execute(t);
			}
			for (Component e : elements) {
				if (e instanceof Sub || e instanceof Add
						|| e instanceof Multiplexer || e instanceof Mult)
					e.execute(t);
			}
			for (Component e : elements) {
				if (e instanceof Source)
					e.execute(t);
			}
			for (Component e : elements) {
				if (e instanceof Plot || e instanceof StateSpaceTrajectory)
					e.execute(t);
			}
		}

		pd.setValue(0);
		pd.setVisible(false);
	}

	public void zoom(double scale) {
		for (Component cp : elements) {
			cp.scale(scale);
			int x = (int) (cp.getX() * scale);
			int y = (int) (cp.getY() * scale);
			cp.setXY(x, y);
		}
		repaint();
	}
}
