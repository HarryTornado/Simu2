package connector;

import model.ConnectException;
import component.Component;

@SuppressWarnings("serial")
public class DummyInputConnector extends InputConnector {
	private InputConnector ip;

	public InputConnector getIp() {
		return ip;
	}

	public DummyInputConnector(InputConnector ip, Component.Position pos) {
		super(ip.getParent().getName(), pos);
		this.ip = ip;
	}
	
	@Override
	public void setDim(int dim) throws ConnectException {
		super.setDim(dim);
		ip.setDim(dim);
	}
}
