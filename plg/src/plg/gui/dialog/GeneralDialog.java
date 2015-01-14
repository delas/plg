package plg.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

/**
 * 
 *
 * @author Andrea Burattin
 */
public class GeneralDialog extends JDialog {

	private static final long serialVersionUID = -3969755125462223910L;
	
	protected static int WIDTH = 615;
	protected static int HEIGHT = 500;
	
	protected JPanel titlePanel;
	protected JPanel bodyPanel;
	protected JPanel footerPanel;
	protected JPanel footerButtonsPanel;
	protected JButton cancelButton;
	
	protected String title;
	protected String help;
	
	public GeneralDialog(JFrame owner, String title, String help) {
		super(owner);
		
		this.title = title;
		this.help = help;
		
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(owner);
		
		placeComponents();
	}
	
	protected void placeComponents() {
		// title
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		JLabel helpLabel = new JLabel(help);
		helpLabel.setFont(helpLabel.getFont().deriveFont(Font.PLAIN));
		
		titlePanel = new JPanel(new GridBagLayout());
		titlePanel.setBorder(BorderFactory.createEmptyBorder());
		titlePanel.setPreferredSize(new Dimension(500, 70));
		titlePanel.setBackground(Color.white);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 15, 5, 15);
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		titlePanel.add(titleLabel, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 15, 5, 15);
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		titlePanel.add(helpLabel, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// body
		bodyPanel = new JPanel();
		
		// footer
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeneralDialog.this.dispose();
			}
		});
		
		footerButtonsPanel = new JPanel();
		footerButtonsPanel.setLayout(new BoxLayout(footerButtonsPanel, BoxLayout.X_AXIS));
		footerButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		
		footerPanel = new JPanel(new BorderLayout());
		footerPanel.setPreferredSize(new Dimension(500, 60));
		footerPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
		footerPanel.add(footerButtonsPanel, BorderLayout.EAST);
		
		addFooterButton(cancelButton, false);
		addFooterButton(new JButton("OK"), true);
		
		// add everything
		setLayout(new BorderLayout());
		add(titlePanel, BorderLayout.NORTH);
		add(bodyPanel, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.SOUTH);
	}
	
	protected void addFooterButton(JButton button, boolean isDefault) {
		footerButtonsPanel.add(Box.createHorizontalStrut(10));
		footerButtonsPanel.add(button);
		
		if (isDefault) {
			button.requestFocus();
			getRootPane().setDefaultButton(button);
		}
	}
	

	public static void main(String args[]) {
		JFrame f = new JFrame("test");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setLocation(300, 300);
		f.setVisible(true);
		GeneralDialog d = new GeneralDialog(f, "New Process", "Use this dialog to set the new process parameters.");
		d.setVisible(true);
	}
}
