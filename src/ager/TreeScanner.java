package ager;

public class TreeScanner {
	public static void run(int x, int y, int z, MCMap m) {
		int up = 12;
		int other = 4;
		int t = m.getBlockType(x, y, z);
		if (t == Types.Cobblestone) {
			int[][][] tree = new int[up][other * 2][other * 2]; // yzx
			int[][][] treeData = new int[up][other * 2][other * 2]; // yzx
			for (int dy = 1; dy < up + 1; dy++) { for (int dz = -other; dz < other; dz++) { for (int dx = -other; dx < other; dx++) {
				int ty = dy - 1;
				int tz = dz + other;
				int tx = dx + other;
				int ny = y + dy;
				int nz = z + dz;
				int nx = x + dx;
				int nt = m.getBlockType(nx, ny, nz);
				if (nt == Types.Wood || nt == Types.Leaves || nt == Types.Vines) {
					tree[ty][tz][tx] = nt;
					treeData[ty][tz][tx] = m.getData(nx, ny, nz);
				} else {
					tree[ty][tz][tx] = -1;
					treeData[ty][tz][tx] = -1;
				}
			}}}
			
			System.out.println("{");
			for (int ay = 0; ay < up; ay++) {
				System.out.println("\t{");
				for (int az = 0; az < other * 2; az++) {
					System.out.print("\t\t{");
					for (int ax = 0; ax < other * 2; ax++) {
						System.out.print(tree[ay][az][ax] + ", ");
					}
					System.out.println("},");
				}
				System.out.println("\t},");
			}
			System.out.println("};");
			
			System.out.println("{");
			for (int ay = 0; ay < up; ay++) {
				System.out.println("\t{");
				for (int az = 0; az < other * 2; az++) {
					System.out.print("\t\t{");
					for (int ax = 0; ax < other * 2; ax++) {
						System.out.print(treeData[ay][az][ax] + ", ");
					}
					System.out.println("},");
				}
				System.out.println("\t},");
			}
			System.out.println("};");
		}
	}
}
