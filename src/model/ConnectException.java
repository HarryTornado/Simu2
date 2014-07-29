package model;

import connector.Connector;

@SuppressWarnings("serial")
public class ConnectException extends Exception {
	public ConnectException(Connector oc, int dim) {
		super("Old connection: " + oc.getDim() + ", new Connection: " + dim);
	}
}
