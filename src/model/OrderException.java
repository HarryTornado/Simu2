package model;

import Jama.Matrix;
import connector.Connector;

@SuppressWarnings("serial")
public class OrderException extends Exception {
	public OrderException(Connector oc, Matrix d) {
		super("Connector Dim: " + oc.getDim() + ", Matrix Dim:"
				+ d.getRowDimension());
	}
}
