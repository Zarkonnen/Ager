package ager;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class ConversionWindow extends JFrame implements Loggr {
	JProgressBar pb;
	JLabel label;
	JButton cancelB;

	public ConversionWindow() {
		super("Processing World");
		Container c = getContentPane();
		c.setLayout(new GridBagLayout());
		GridBagConstraints gc;
		gc = new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
		c.add(pb = new JProgressBar(), gc);
			pb.setIndeterminate(true);
		gc = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
		c.add(label = new JLabel("Processing..."), gc);
		gc = new GridBagConstraints(1, 1, 1, 1, 0.1, 1.1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
		c.add(cancelB = new JButton("Cancel"), gc);
			cancelB.setToolTipText("Cancel processing. The partly processed world will be available but may look weird.");
			cancelB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					System.exit(0);
				}
			});
		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void log(String l) {
		label.setText(l);
	}

	@Override
	public void error(String l) {
		label.setText(l);
	}

	@Override
	public void error(Exception e) {
		File f = new File("Agifier error report " + System.currentTimeMillis() + ".txt");
		JOptionPane.showMessageDialog(this, "Processing error: " + e.getMessage() + ". Please send " + f.getAbsolutePath() + " to david.stark@zarkonnen.com.");
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(f));
			e.printStackTrace(ps);
			ps.flush();
			ps.close();
		} catch (Exception ee) {}
		System.exit(1);
	}
}
