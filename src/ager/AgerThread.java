package ager;

import java.awt.Desktop;
import java.io.File;
import java.util.Random;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;

public class AgerThread implements Runnable {
	public int gameMode;
	public boolean hardcore;
	public int iters;
	public int players;
	public File originalWorld;

	public AgerThread(int gameMode, int players, int iters, File originalWorld) {
		this.gameMode = gameMode;
		this.players = players;
		this.iters = new int[] { 0, 1, 2, 4, 20 }[iters];
		this.originalWorld = originalWorld;
	}
	
	@Override
	public void run() {
		try {
			ConversionWindow cv = new ConversionWindow();
			cv.setVisible(true);
			cv.setLocationRelativeTo(null);
			Ager.log = cv;
			Ager.log.log("Copying world");
			File f2 = null;
			int i = 2;
			do {
				f2 = new File(originalWorld.getParentFile(), originalWorld.getName() + " " + i);
				i++;
			} while (f2.exists());

			FileUtils.copyDirectory(originalWorld, f2);
			Ager.log.log("Loading world");
			MCMap m = new MCMap(f2, (int) (Math.min(Runtime.getRuntime().maxMemory() / 500000, 100000000)));
			switch (gameMode) {
				case 0: // no change
					break;
				case 1: // survival
					m.setGameType(0, false);
					break;
				case 2: // hardcore
					m.setGameType(0, true);
					break;
				case 3: // creative
					m.setGameType(1, false);
					break;
			}
			switch (players) {
				case 0: // no change
					break;
				case 1: // reset
					m.killPlayers();
					break;
				case 2: // explorers
					m.makePlayersAdventurers(new Random());
					break;
			}
			Ager.process(m, new Random(), null, iters);
			cv.dispose();
			JOptionPane.showMessageDialog(null, "Processed. The aged world is called " + f2.getName());
			System.exit(0);
		} catch (Exception e) {
			Ager.log.error(e);
		}
	}
}
