package ager;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;

public class Blinkenlights extends JFrame {
	private MCMap map;

	public Blinkenlights(MCMap map) {
		super("Blinkenlights");
		this.map = map;
		setSize(800, 800);
		setVisible(true);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 800, 800);
		for (MCAFile f : map.files) {
			for (Chunk[] chs : f.chunks) { for (Chunk ch : chs) {
				if (ch == null) { continue; }
				g.setColor(map.loadedChunks.contains(ch) ? Color.GREEN : Color.RED);
				g.fillRect(ch.globalChunkX * 2 + 400, ch.globalChunkZ * 2 + 400, 2, 2);
				//System.out.println((ch.globalChunkX * 2 + 400) + " " +  (ch.globalChunkZ * 2 + 400));
			}}
		}
	}
}
