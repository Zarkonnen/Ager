package ager;

import java.util.ArrayList;
import java.util.Iterator;

public class CoordMap<T> implements Iterable<T> {
	private Object[][] map = new Object[1][1];
	private ArrayList<T> list = new ArrayList<T>();
	private int xOffset, yOffset;
	
	public T get(int x, int y) {
		int nx = x + xOffset;
		int ny = y + yOffset;
		if (ny < 0 || ny >= map.length || nx < 0 || nx >= map[ny].length) {
			return null;
		}
		return (T) map[ny][nx];
	}
	
	public void put(int x, int y, T o) {
		int nx = x + xOffset;
		int ny = y + yOffset;
		if (ny < 0) {
			Object[][] newMap = new Object[map.length - ny][map[0].length];
			System.arraycopy(map, 0, newMap, -ny, map.length);
			map = newMap;
			yOffset -= ny;
			put(x, y, o);
			return;
		}
		if (ny >= map.length) {
			Object[][] newMap = new Object[ny + 1][map[0].length];
			System.arraycopy(map, 0, newMap, 0, map.length);
			map = newMap;
			put(x, y, o);
			return;
		}
		if (nx < 0) {
			Object[][] newMap = new Object[map.length][map[0].length - nx];
			for (int i = 0; i < map.length; i++) {
				System.arraycopy(map, 0, newMap[i], -nx, map[0].length);
			}
			map = newMap;
			xOffset -= nx;
			put(x, y, o);
		}
		if (nx >= map[0].length) {
			Object[][] newMap = new Object[map.length][nx + 1];
			for (int i = 0; i < map.length; i++) {
				System.arraycopy(map, 0, newMap[i], 0, map.length);
			}
			map = newMap;
			put(x, y, o);
		}
		map[ny][nx] = o;
	}

	@Override
	public Iterator<T> iterator() { return list.iterator(); }
}
