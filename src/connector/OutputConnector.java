package connector;

import java.util.ArrayList;
import java.util.List;

import model.ConnectException;
import model.OrderException;
import Jama.Matrix;

import component.Component;
import component.Component.Position;

/**
 * @author    jyk
 */
@SuppressWarnings("serial")
public class OutputConnector extends Connector {
	private Matrix data;
	/**
	 * @uml.property  name="name"
	 */
	private String name;
	public Component.Position pos;
	private Component parent;
	private DummyOutputConnector doc;
	
	/**
	 * @uml.property  name="connected"
	 */
	private List<InputConnector> connected = new ArrayList<InputConnector>();
	/**
	 * @uml.property  name="dim"
	 */
	private int dim;

	/**
	 * @param dim
	 * @throws ConnectException
	 * @uml.property  name="dim"
	 */
	public void setDim(int dim) throws ConnectException {
		this.dim = dim;
		for (InputConnector c : connected) {
			if (c.getDim() != dim)
				c.setDim(dim);
		}
	}

	/**
	 * @return
	 * @uml.property  name="dim"
	 */
	public int getDim() {
		return dim;
	}

	/**
	 * @return
	 * @uml.property  name="parent"
	 */
	public Component getParent() {
		return parent;
	}

	public void addConnected(InputConnector c) throws ConnectException {
		c.setDim(dim);
		c.connect(this);
		connected.add(c);
	}

	public void disconnectFrom(Connector c) {
		connected.remove(c);
	}

	public void disconnect() {
		for (InputConnector c : connected) {
			c.disconnectFromSource();
		}
		connected = new ArrayList<InputConnector>();
	}

	/**
	 * @param  parent
	 * @uml.property  name="parent"
	 */
	public void setParent(Component parent) {
		this.parent = parent;
	}

	public OutputConnector(String name, Component.Position pos) {
		this.name = name;
		this.pos = pos;
	}

	public boolean connectable(InputConnector input) {
		return getDim() == input.getDim();
	}

	public void setOutput(Matrix data) throws OrderException {
		if (dim != data.getRowDimension())
			throw new OrderException(this, data);
		this.data = data;
	}

	public Matrix getOutput() {
		return data;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param  name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	public void setPosition(Component.Position pos) {
		this.pos = pos;
	}

	// public abstract void execute(double t);

	/**
	 * @return
	 * @uml.property  name="connected"
	 */
	public List<InputConnector> getConnected() {
		return connected;
	}

	@Override
	public String toString() {
		return getParent().getName() + "." + getName();
	}

	public DummyOutputConnector getDoc() {
		return doc;
	}

	public void setDoc(DummyOutputConnector doc) {
		this.doc = doc;
	}
}
