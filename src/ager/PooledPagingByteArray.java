package ager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PooledPagingByteArray {
	public static class Pool {
		private RandomAccessFile raf;
		private long offset = 0;

		public Pool() throws IOException {
			raf = new RandomAccessFile(File.createTempFile("bpp", null), "rw");
		}
		
		public PooledPagingByteArray getArray(int size) { return new PooledPagingByteArray(size, this); }
	}
	
	public final Pool pool;
	public byte[] array;
	public final int size;
	private long offset = -1;
	
	private PooledPagingByteArray(int size, Pool pool) { this.size = size; this.pool = pool; }
	
	public byte[] get() throws IOException {
		if (array == null) {
			pageIn();
		}
		return array;
	}
	
	public void pageIn() throws IOException {
		if (offset == -1) {
			array = new byte[size];
		} else {
			pool.raf.seek(offset);
			pool.raf.read(array);
		}
	}
	
	public void pageOut() throws IOException {
		if (offset == -1) {
			offset = pool.offset;
			pool.offset += size;
		}
		pool.raf.seek(offset);
		pool.raf.write(array);
	}
	
	public static void main(String[] args) throws IOException {
		Pool p = new Pool();
		long ms = System.currentTimeMillis();
		PooledPagingByteArray[] pbas = new PooledPagingByteArray[10000];
		for (int i = 0; i < 10000; i++) { pbas[i] = p.getArray(4096); }
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10000; j++) {
				pbas[j].get();
			}
			for (int j = 0; j < 10000; j++) {
				pbas[j].pageOut();
			}
		}
		System.out.println(System.currentTimeMillis() - ms);
	}
}
