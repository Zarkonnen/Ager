package ager;

import com.jcraft.jzlib.DeflaterOutputStream;
import com.jcraft.jzlib.InflaterInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import unknown.Tag;

public class Chunk {
	public Tag t;
	Tag[] sections = new Tag[16];
	
	public Chunk(InputStream is) throws IOException {
		t = Tag.readFrom(is);
		Tag[] sArray = (Tag[]) t.findTagByName("Level").findTagByName("Sections").getValue();
		for (Tag section : sArray) {
			this.sections[(Byte) section.findTagByName("Y").getValue()] = section;
		}
	}
	
	public void removeLighting() {
		byte[] empty = new byte[2048];
		
		for (int y = 0; y < 16; y++) {
			if (sections[y] != null) {
				System.arraycopy(empty, 0, (byte[]) sections[y].findTagByName("BlockLight").getValue(), 0, 2048);
			}
		}
	}
	
	public int getBlockType(int x, int y, int z) {
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		return ((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)];
	}
	
	public void setBlockType(byte type, int x, int y, int z) {
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)] = type;
	}
	
	public static int getNybble(byte b, int offset) {
		int val = b < 0 ? b + 256 : b;
		return offset == 0 ? (val % 16) : (val / 16);
	}
	
	public static byte setNybble(byte b, int offset, int value) {
		int val = b < 0 ? b + 256 : b;
		int result = offset == 0 ? ((val / 16) * 16 + value) : (val % 16 + value * 16);
		if (result > 127) {
			result -= 256;
		}
		return (byte) result;
	}
	
	public int getSkyLight(int x, int y, int z) {
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
		return getNybble(b, addr % 2);
	}
	
	public void setSkyLight(byte light, int x, int y, int z) {
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
		((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2] = setNybble(b, addr % 2, light);
	}
}
