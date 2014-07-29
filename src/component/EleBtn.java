package component;

import javax.swing.JButton;

/**
 * @author   jyk
 */
@SuppressWarnings("serial")
public class EleBtn extends JButton {
	/**
	 * @uml.property  name="cls"
	 */
	@SuppressWarnings("rawtypes")
	private Class cls;

	public EleBtn(@SuppressWarnings("rawtypes") Class cls) {
		this.cls = cls;
	}

	/**
	 * @return
	 * @uml.property  name="cls"
	 */
	@SuppressWarnings("rawtypes")
	public Class getCls() {
		return cls;
	}
}
