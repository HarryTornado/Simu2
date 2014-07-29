package connector.intelligentLine;

public class Cord {
	public final int i;
	public final int j;
	public static int maxI;
	public static int maxJ;

	public Cord(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public Cord left() {
		if (i <= 0)
			return null;
		return new Cord(i - 1, j);
	}

	public Cord right() {
		if (i >= maxI - 1)
			return null;
		return new Cord(i + 1, j);
	}

	public Cord top() {
		if (j <= 0)
			return null;
		return new Cord(i, j - 1);
	}

	public Cord bottom() {
		if (j >= maxJ - 1)
			return null;
		return new Cord(i, j + 1);
	}

	public String toString() {
		return "(" + i + "," + j + ")";
	}
}
