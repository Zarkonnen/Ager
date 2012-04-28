/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import org.jnbt.NBTInputStream;

public class Ager {
	public static void main(String[] args) throws Exception {
		/*File f = new File("/Users/zar/Desktop/Fishcakes/region/r.0.0.mca");
		int[] sectorOffsets = new int[32 * 32];
		int[] sectorSizes = new int[32 * 32];
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));
		for (int i = 0; i < 32 * 32; i++) {
			for (int j = 0; j < 3; j++) {
				sectorOffsets[i] <<= 8;
				sectorOffsets[i] |= fis.read();
			}
			sectorSizes[i] = fis.read();
		}
				
		fis.close();*/
		new MCAFile(new File("/Users/zar/Desktop/Fishcakes/region/"), 0, 0);
	}
}
