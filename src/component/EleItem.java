package component;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class EleItem extends JMenuItem {
	@SuppressWarnings("unchecked")
	private Class cls;

	@SuppressWarnings("unchecked")
	public EleItem(Class cls) {
		super(cls.getSimpleName());
		this.cls = cls;
	}

	@SuppressWarnings("unchecked")
	public Class getCls() {
		return cls;
	}
}
