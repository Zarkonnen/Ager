package ager;

public final class BytePt3Stack {
	byte[] data;
	int headPtr = -3;
	public int x, y, z;

	public BytePt3Stack(final int capacity) {
		data = new byte[3 * capacity];
	}
	
	public int size() { return headPtr / 3 + 1; }
	public int capacity() { return data.length / 3; }
	public boolean isEmpty() { return headPtr == -3; }
	public void clear() { headPtr = -3; }
	public void push(int x, int y, int z) {
		headPtr += 3;
		if (headPtr == data.length) {
			byte[] newData = new byte[data.length * 2];
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
		}
		data[headPtr    ] = (byte) x;
		data[headPtr + 1] = (byte) y;
		data[headPtr + 2] = (byte) z;
	}
	public void pop() {
		x = data[headPtr    ];
		y = data[headPtr + 1];
		z = data[headPtr + 2];
		headPtr -= 3;
	}
	public void get(final int index) {
		x = data[index * 3    ];
		y = data[index * 3 + 1];
		z = data[index * 3 + 2];
	}
	public void compact() {
		byte[] newData = new byte[headPtr + 3];
		System.arraycopy(data, 0, newData, 0, newData.length);
		data = newData;
	}
	public void compactTo(final int cap) {
		byte[] newData = new byte[Math.max(headPtr + 3, cap * 3)];
		System.arraycopy(data, 0, newData, 0, newData.length);
		data = newData;
	}
}
