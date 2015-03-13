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
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import plg.gui.config.ConfigurationSet;
import plg.gui.util.SpringUtilities;

/**
 * This abstract class describes a general dialog in PLG. The dialog created
 * extending this class can be used, for example, to insert 
 * parameters.
 * 
 * <p><strong>Attention:</strong> classes extending this type are required to
 * configure the {@link #returnedValue} variable, in order to properly let the
 * framework identify the value prompted by the user. The default value for such
 * variable is set to <tt>CANCEL</tt>.
 *
 * @author Andrea Burattin
 */
public abstract class GeneralDialog extends JDialog {

	private static final long serialVersionUID = -3969755125462223910L;
	
	protected static int WIDTH = 615;
	protected static int HEIGHT = 580;
	
	protected ConfigurationSet configuration = null;
	protected RETURNED_VALUES returnedValue = RETURNED_VALUES.CANCEL;
	
	protected JPanel titlePanel;
	protected JPanel bodyPanel;
	protected JPanel bodyPanelContainer;
	protected JPanel footerPanel;
	protected JPanel footerButtonsPanel;
	protected JButton cancelButton;
	protected String title;
	protected String help;
	
	/**
	 * This enumeration describes the possible values that a dialog is allowed
	 * to return. The default value is <tt>CANCEL</tt>,
	 *
	 * @author Andrea Burattin
	 */
	public enum RETURNED_VALUES {
		CANCEL,
		SUCCESS
	};
	
	/**
	 * Dialog constructor
	 * 
	 * @param owner the owner of the dialog
	 * @param title the title of the dialog
	 * @param help a short text describing the dialog content
	 * @param configuration the configuration set to use for the dialog
	 */
	public GeneralDialog(JFrame owner, String title, String help, ConfigurationSet configuration) {
		super(owner);
		
		this.title = title;
		this.help = help;
		this.configuration = configuration;
		
		setTitle(title);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(owner);
		setModal(true);
		
		placeComponents();
		
		// register the key pressed for ESC button
		getRootPane().getInputMap(
				JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				"ESC_KEY_PRESSED");
		getRootPane().getActionMap().put(
				"ESC_KEY_PRESSED",
				new AbstractAction() {
					private static final long serialVersionUID = 244032072103715616L;
					public void actionPerformed(ActionEvent event) {
						GeneralDialog.this.dispatchEvent(new WindowEvent(GeneralDialog.this, WindowEvent.WINDOW_CLOSING));
					}
				});
	}
	
	/**
	 * This method configures the graphical components of the dialog
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
		
		JScrollPane bodyPanelScroller = new JScrollPane(bodyPanelContainer);
		bodyPanelScroller.setBorder(BorderFactory.createEmptyBorder());
		
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
		add(bodyPanelScroller, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * This utility method generates a new label for the "form" of the body
	 * 
	 * @param text
	 * @return
	 */
	protected JLabel prepareFieldLabel(String text) {
		JLabel l = new JLabel(text, JLabel.TRAILING);
		l.setFont(l.getFont().deriveFont(Font.PLAIN));
		return l;
	}
	
	/**
	 * This method automatically inserts a vertical separator into the body
	 * 
	 * @param height the height of the separator
	 */
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
	 * This method inserts a new button into the dialog footer
	 * 
	 * @param button the button to insert
	 * @param isDefault whether the provided button is supposed to be the
	 * default for the dialog
	 */
	protected void addFooterButton(JButton button, boolean isDefault) {
		footerButtonsPanel.add(Box.createHorizontalStrut(10));
		footerButtonsPanel.add(button);
		
		if (isDefault) {
			button.requestFocus();
			getRootPane().setDefaultButton(button);
		}
	}
	
	/**
	 * This method returns the values "returned" by the current dialog
	 * 
	 * @return the value returned by this dialog
	 */
	public RETURNED_VALUES returnedValue() {
		return returnedValue;
	}
}
