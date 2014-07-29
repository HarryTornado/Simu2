package connector;

import model.ConnectException;
import Jama.Matrix;

import component.Component;
import component.Component.Position;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public class InputConnector extends Connector {
	/**
	 * @uml.property name="name"
	 */
	private String name;
	private OutputConnector source;
	private String[] vNames;
	/**
	 * @uml.property name="connected"
	 */
	private boolean connected = false;
	public Component.Position pos;
	private Component parent;
	private DummyInputConnector dic;
	/**
	 * @uml.property name="dim"
	 */
	private int dim;

	public InputConnector(String name, Component.Position pos) {
		this.name = name;
		this.pos = pos;
	}

	public void connect(OutputConnector oi) throws ConnectException {
		// setExported(false);
		source = oi;
		connected = true;
		getParent().setDim(oi.getDim());
//		parent.update();
	}

	public void disconnect() {
		connected = false;
		if (source != null) {
			source.disconnectFrom(this);
			source = null;
		}
		try {
			setDim(0);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		parent.update();
	}

	public void disconnectFromSource() {
		source = null;
		try {
			setDim(0);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connected = false;
//		parent.update();
	}

	String[] getInputVariableNames() {
		return vNames;
	}

	void setInputVariableNames(String[] vNames) {
		this.vNames = vNames;
	}

	public Matrix getInput() {
		if (source != null)
			return source.getOutput();
		else if (dic != null)
			return dic.getInput();
		return null;
	}

	/**
	 * @return
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 * @uml.property name="connected"
	 */
	public boolean isConnected() {
		return connected;
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public void setPosition(Component.Position pos) {
		this.pos = pos;
	}

	/**
	 * @return
	 * @uml.property name="parent"
	 */
	public Component getParent() {
		return parent;
	}

	/**
	 * @param parent
	 * @uml.property name="parent"
	 */
	public void setParent(Component parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return parent.getName() + "." + this.name;
	}

	/**
	 * @return
	 * @uml.property name="source"
	 */
	public Connector getSource() {
		return source;
	}

	/**
	 * @return
	 * @uml.property name="dim"
	 */
	@Override
	public int getDim() {
		return dim;
	}

	/**
	 * @param dim
	 * @throws ConnectException
	 * @uml.property name="dim"
	 */
	@Override
	public void setDim(int dim) throws ConnectException {
		this.dim = dim;
		this.getParent().setDim(dim);
	}

	public DummyInputConnector getDic() {
		return dic;
	}

	public void setDic(DummyInputConnector dic) {
		this.dic = dic;
	}
}
