package ager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

public class Chunk {
	CompoundTag t;
	
	public Chunk(InputStream is, byte[] underlyingData, int offset) throws IOException {
		int length = 0;
		for (int i = 0; i < 4; i++) {
			length <<= 8;
			length |= is.read();
		}
		int compression = is.read();
		is = new ByteArrayInputStream(underlyingData, offset + 5, length);
		switch (compression) {
			case 2: // zlib
				is = new InflaterInputStream(is);
				break;
			default:
				throw new RuntimeException("Unsupported compression mode: " + compression);
		}
		
		NBTInputStream nis = new NBTInputStream(is);
		t = (CompoundTag) nis.readTag();
		for (String k : t.getValue().keySet()) {
			System.out.println(k);
		}
	}
}
