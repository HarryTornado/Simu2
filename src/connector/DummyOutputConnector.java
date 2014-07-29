package connector;

import component.Component;

@SuppressWarnings("serial")
public class DummyOutputConnector extends OutputConnector {
	private OutputConnector op;

	public OutputConnector getOp() {
		return op;
	}

	public DummyOutputConnector(OutputConnector op, Component.Position pos) {
		super(op.getParent().getName(), pos);

		this.op = op;
	}
}
