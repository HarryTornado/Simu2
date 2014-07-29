package component;

import model.ConnectException;
import model.OrderException;

@SuppressWarnings("serial")
public class OutputStub extends Stub {
	public OutputStub() {
		super(60, 30, "y");
	}

	@Override
	public void execute(double t) {
		try {
			if (input.getSource() != null)
				output.getDoc().setOutput(input.getInput());
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setDim(int dim) {
		try {
			output.setDim(dim);
			output.getDoc().setDim(dim);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
