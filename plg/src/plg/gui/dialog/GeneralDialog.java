package plg.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.SpringLayout;

import plg.gui.util.SpringUtilities;

/**
 * 
 *
 * @author Andrea Burattin
 */
public abstract class GeneralDialog extends JDialog {

	private static final long serialVersionUID = -3969755125462223910L;
	
	protected static int WIDTH = 615;
	protected static int HEIGHT = 500;
	
	private JPanel bodyPanelContainer;
	protected JPanel titlePanel;
	protected JPanel bodyPanel;
	protected JPanel footerPanel;
	protected JPanel footerButtonsPanel;
	protected JButton cancelButton;
	
	protected String title;
	protected String help;
	
	/**
	 * 
	 * @param owner
	 * @param title
	 * @param help
	 */
	public GeneralDialog(JFrame owner, String title, String help) {
		super(owner);
		
		this.title = title;
		this.help = help;
		
		setTitle(title);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(owner);
		
		placeComponents();
	}
	
	/**
	 * 
	 */
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
		bodyPanel = new JPanel(new SpringLayout());
		bodyPanelContainer = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(15, 5, 15, 5);
		bodyPanelContainer.add(bodyPanel, c);
		
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
		
		// add everything
		setLayout(new BorderLayout());
		add(titlePanel, BorderLayout.NORTH);
		add(bodyPanelContainer, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	protected JLabel prepareFieldLabel(String text) {
		JLabel l = new JLabel(text, JLabel.TRAILING);
		l.setFont(l.getFont().deriveFont(Font.PLAIN));
		return l;
	}
	
	protected void insertBodySeparator(int height) {
		bodyPanel.add(prepareFieldLabel(""));
		bodyPanel.add(Box.createVerticalStrut(height));
	}
	
	/**
	 * This method configures the spring layout of the body panel with all the
	 * configuration widgets. To be called after all widgets have been added to
	 * the panel.
	 */
	protected void layoutBody() {
		// lay out the panel
		SpringUtilities.makeCompactGrid(bodyPanel,
				(bodyPanel.getComponentCount() / 2), 2, // rows, cols
				0, 0, // initX, initY
				5, 10); //xPad, yPad
	}
	
	/**
	 * 
	 * @param button
	 * @param isDefault
	 */
	protected void addFooterButton(JButton button, boolean isDefault) {
		footerButtonsPanel.add(Box.createHorizontalStrut(10));
		footerButtonsPanel.add(button);
		
		if (isDefault) {
			button.requestFocus();
			getRootPane().setDefaultButton(button);
		}
	}
}
