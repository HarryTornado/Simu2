package component;

import java.awt.Point;

import model.OrderException;

@SuppressWarnings("serial")
public class InputStub extends Stub {
	private int dim;

	public int getDim() {
		return dim;
	}

	public InputStub() {
		super(60, 30, "u");
		setDim(1);
	}

	@Override
	public void setDim(int dim) {
		this.dim = dim;
		super.setDim(dim);
	}

	@Override
	public void showDialog(Point point) {
		InputStubEditor editor = new InputStubEditor(this);
		editor.setLocation(point);
		editor.showDialog();
		if (editor.isOk()) {
			setName(editor.getName());
			setDim(editor.getDim());
		}
	}

	@Override
	public void execute(double t) {
		try {
			if (input.getDic() != null && input.getDic().getSource() != null)
				output.setOutput(input.getDic().getInput());
		} catch (OrderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
