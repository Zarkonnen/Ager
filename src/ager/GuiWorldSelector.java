package ager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import org.apache.commons.io.FileUtils;

public class GuiWorldSelector extends JFrame {
	public static void main(String[] args) {
		new GuiWorldSelector().setVisible(true);
	}
	
	JPanel p;
		JLabel helpL;
		JScrollPane worldListSP;
			JList worldList;
		JPanel pathP;
			JLabel pathL;
			JButton selectB;
		JPanel infoPanel;
			JComboBox modeCB;
			JComboBox playersCB;
			JComboBox agingCB;
			JLabel infoL;
		JPanel bottomPanel;
			JButton cancelB;
			JButton selectOtherB;
			JButton convertB;
	
	File selectedFile;
	static ImageIcon worldIcon;
	
	static {
		try {
			worldIcon = new ImageIcon(ImageIO.read(GuiWorldSelector.class.getResourceAsStream("world.png")), "Minecraft World");
		} catch (Exception e) {}
	}
		
	public GuiWorldSelector() {
		super("Agifier");
		final GuiWorldSelector gws = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(p = new JPanel(new BorderLayout()));
		p.setLayout(new BorderLayout());
		p.setBorder(new EmptyBorder(5, 5, 5, 5));
		p.add(helpL = new JLabel("Select world to process. The world will be copied and then aged."), BorderLayout.NORTH);
			helpL.setBorder(new EmptyBorder(0, 5, 5, 5));
		p.add(worldListSP = new JScrollPane(), BorderLayout.CENTER);
			worldListSP.setViewportView(worldList = new JList(new WorldListModel()));
				worldList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent lse) {
						updateDisplay();
					}});
				worldList.setCellRenderer(new DefaultListCellRenderer() {
					@Override
					public Component getListCellRendererComponent(JList jlist, Object o, int i, boolean bln, boolean bln1) {
						JLabel l = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
						l.setIcon(worldIcon);
						return l;
					}
				});
		pathP = new JPanel();
			pathP.setBorder(new BevelBorder(BevelBorder.LOWERED));
			pathP.setLayout(new GridBagLayout());
			GridBagConstraints gc;
			gc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
			pathP.add(pathL = new JLabel("?"), gc);
			if (worldIcon != null) {
				pathL.setIcon(worldIcon);
			}
			gc = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
			pathP.add(selectB = new JButton("Select from list..."), gc);
				selectB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						selectedFile = null;
						updateDisplay();
					}
				});
			gc = new GridBagConstraints(0, 2, 1, 1, 100.0, 100.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
			pathP.add(new JPanel(), gc);
		p.add(infoPanel = new JPanel(), BorderLayout.EAST);
			infoPanel.setLayout(new GridBagLayout());
			gc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
			infoPanel.add(modeCB = new JComboBox(new String[] { "Keep Game Mode", "Survival", "Hardcore", "Creative" }), gc);
			gc = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
			infoPanel.add(playersCB = new JComboBox(new String[] { "Leave Players Where They Are", "Reset Players", "Equip Players as Explorers"}), gc);
			gc = new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
			infoPanel.add(agingCB = new JComboBox(new String[] { "No Aging", "Age for Years", "Age for Decades", "Age for Centuries", "Age for Millennia" }), gc);
				agingCB.setSelectedIndex(1);
				agingCB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						updateDisplay();
					}
				});
			gc = new GridBagConstraints(0, 3, 1, 1, 100.0, 100.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
			//infoPanel.add(new JPanel(), gc);
			infoPanel.add(infoL = new JLabel("<html>Select world...</html>"), gc);
		p.add(bottomPanel = new JPanel(), BorderLayout.SOUTH);
			bottomPanel.add(cancelB = new JButton("Cancel"));
				cancelB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						System.exit(0);
					}
				});
			bottomPanel.add(selectOtherB = new JButton("Convert other..."));
				selectOtherB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						JFileChooser jfc = new JFileChooser();
						jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
							final SavesFilter sf = new SavesFilter();
							
							@Override
							public boolean accept(File file) {
								file = new File(file.getAbsolutePath()); // Unwrap the MyFile.
								return file.isDirectory() || isAnvil(file);
							}

							@Override
							public String getDescription() {
								return "Minecraft saves";
							}
						});
						jfc.setFileSystemView(new MyFSV());
						jfc.setFileView(new MyFV());
						jfc.showOpenDialog(gws);
						if (jfc.getSelectedFile() != null && isAnvil(jfc.getSelectedFile())) {
							selectedFile = new File(jfc.getSelectedFile().getAbsolutePath());
							updateDisplay();
						}
					}
				});
			bottomPanel.add(convertB = new JButton("Convert!"));
				convertB.setEnabled(false);
				convertB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						AgerThread t = new AgerThread(modeCB.getSelectedIndex(), playersCB.getSelectedIndex(), agingCB.getSelectedIndex(), selectedFile);
						gws.setVisible(false);
						new Thread(t, "Aging").start();
					}
				});
		setSize(500, 500);
	}
	
	void updateDisplay() {
		if (selectedFile != null) {
			p.remove(worldListSP);
			p.add(pathP, BorderLayout.CENTER);
			pathL.setText(selectedFile.getPath());
		} else {
			p.remove(pathP);
			p.add(worldListSP, BorderLayout.CENTER);
		}
		repaint();
		
		convertB.setEnabled(targetFile() != null);
		
		if (targetFile() != null) {
			long sz = FileUtils.sizeOfDirectory(targetFile());
			long runningTime = 2 + sz * agingCB.getSelectedIndex() / 1024 / 1024 / 2;
			long requiredMemory = sz * 20;
			String availableSpace = "?";
			boolean notEnoughSpace = false;
			try {
				File tmp = File.createTempFile("check", "kkkk");
				long avSp = tmp.getUsableSpace();
				availableSpace = FileUtils.byteCountToDisplaySize(avSp);
				if (avSp < requiredMemory) {
					notEnoughSpace = true;
					convertB.setEnabled(false);
				}
				tmp.delete();
			} catch (Exception e) {}
			infoL.setText("<html>" +
					"World size: " + FileUtils.byteCountToDisplaySize(sz) + "<br>" +
					"Estimated processing time: " + runningTime + " minutes" + "<br>" +
					"Required free HD space for caching: " + FileUtils.byteCountToDisplaySize(requiredMemory) + "<br>" +
					(notEnoughSpace ? "<font color=\"red\">" : "") + availableSpace + " available<br>" +
					"</html>");
		}
	}
	
	File targetFile() {
		if (selectedFile != null) { return new File(selectedFile.getAbsolutePath()); }
		WorldListEntry f = (WorldListEntry) worldList.getSelectedValue();
		return f != null && f.anvil ? f.worldF : null;
	}
	
	class MyF extends File {
		public MyF(String path) {
			super(path);
		}
		
		public MyF(File f) {
			this(f.getAbsolutePath());
		}
		
		@Override
		public boolean isDirectory() {
			return super.isDirectory() && !isAnvil(this);
		}
	}
	
	class MyFV extends FileView {
		@Override
		public Icon getIcon(File file) {
			file = new File(file.getAbsolutePath());
			if (isAnvil(file)) {
				Icon ic = super.getIcon(file);
				if (ic == null) {
					return worldIcon;
				}
			}
			return super.getIcon(file);
		}
	}
	
	class MyFSV extends FileSystemView {
		private final FileSystemView inner = FileSystemView.getFileSystemView();

		@Override
		public Boolean isTraversable(File file) {
			return inner.isTraversable(file);
		}

		@Override
		public boolean isRoot(File file) {
			return inner.isRoot(file);
		}

		@Override
		public boolean isParent(File file, File file1) {
			return inner.isParent(file, file1);
		}

		@Override
		public boolean isHiddenFile(File file) {
			return inner.isHiddenFile(file);
		}

		@Override
		public boolean isFloppyDrive(File file) {
			return inner.isFloppyDrive(file);
		}

		@Override
		public boolean isFileSystemRoot(File file) {
			return inner.isFileSystemRoot(file);
		}

		@Override
		public boolean isFileSystem(File file) {
			return inner.isFileSystem(file);
		}

		@Override
		public boolean isDrive(File file) {
			return inner.isDrive(file);
		}

		@Override
		public boolean isComputerNode(File file) {
			return inner.isComputerNode(file);
		}

		@Override
		public String getSystemTypeDescription(File file) {
			return inner.getSystemTypeDescription(file);
		}

		@Override
		public Icon getSystemIcon(File file) {
			file = new File(file.getAbsolutePath());
			if (isAnvil(file)) {
				return worldIcon;
			}
			return inner.getSystemIcon(file);
		}

		@Override
		public String getSystemDisplayName(File file) {
			return inner.getSystemDisplayName(file);
		}

		@Override
		public File[] getRoots() {
			File[] files = inner.getRoots();
			for (int i = 0; i < files.length; i++) {
				files[i] = new MyF(files[i]);
			}
			return files;
		}

		@Override
		public File getParentDirectory(File file) {
			return new MyF(inner.getParentDirectory(file));
		}

		@Override
		public File getHomeDirectory() {
			return new MyF(inner.getHomeDirectory());
		}

		@Override
		public File[] getFiles(File file, boolean bln) {
			File[] files = inner.getFiles(file, bln);
			for (int i = 0; i < files.length; i++) {
				files[i] = new MyF(files[i]);
			}
			return files;
		}

		@Override
		public File getDefaultDirectory() {
			return new MyF(inner.getDefaultDirectory());
		}

		@Override
		public File getChild(File file, String string) {
			return new MyF(inner.getChild(file, string));
		}

		@Override
		public File createNewFolder(File file) throws IOException {
			return new MyF(inner.createNewFolder(file));
		}

		@Override
		public File createFileObject(String string) {
			return new MyF(inner.createFileObject(string));
		}

		@Override
		public File createFileObject(File file, String string) {
			return new MyF(inner.createFileObject(file, string));
		}
	}
	
	class WorldListModel extends AbstractListModel {
		@Override
		public int getSize() {
			return savesFolder().listFiles(new SavesFilter()).length;
		}

		@Override
		public Object getElementAt(int i) {
			File[] fs = savesFolder().listFiles(new SavesFilter());
			return i < fs.length ? new WorldListEntry(fs[i], isAnvil(fs[i])) : null;
		}
	}
	
	static boolean isAnvil(File f) {
		return new File(f, "region").exists() && new File(f, "region").isDirectory() && new File(f, "region").list(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return name.endsWith(".mca");
			}
		}).length > 0;
	}
	
	class WorldListEntry {
		final File worldF;
		final boolean anvil;
		@Override
		public String toString() { return worldF.getName() + (!anvil ? " (Old Save Version!)" : ""); }

		public WorldListEntry(File worldF, boolean anvil) {
			this.worldF = worldF;
			this.anvil = anvil;
		}
	}
	
	class SavesFilter implements FileFilter {
		@Override
		public boolean accept(File file) {
			if (!file.isDirectory()) { return false; }
			if (file.isHidden())     { return false; }
			if (!new File(file, "region").exists()) { return false; }
			return true;
		}
	}
	
	static File savesFolder() {
		//Windows
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			return new File(System.getenv("APPDATA") + "\\.minecraft\\saves\\");
		}

		//Mac
		if (System.getProperty("os.name").toLowerCase().contains("mac os x")) {
			return new File(System.getProperty("user.home") + "/Library/Application Support/minecraft/saves/");
		}

		//Linux
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			return new File(System.getProperty("user.home") + "/.minecraft/saves/");
		}
		return new File("");
	}
}
