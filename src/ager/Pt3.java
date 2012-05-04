package ager;

public final class Pt3 {
	final int x;
	final int y;
	final int z;

	public Pt3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Pt3)) {
			return false;
		}
		Pt3 p2 = (Pt3) o2;
		return x == p2.x && y == p2.y && z == p2.z;
	}

	@Override
	public int hashCode() {
		return x + 16 * z + 256 * y;
	}	
}
