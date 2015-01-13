package plg.gui.panels;

import java.awt.Color;
import java.awt.Dimension;

import plg.gui.config.ConfigurationSet;

public class Console extends MainWindowPanel {

	private static final long serialVersionUID = -8707628278434660233L;
	
	protected static final String KEY_CONSOLE_VISIBLE = "KEY_CONSOLE_VISIBLE";
	protected static final boolean DEFAULT_VISIBILITY = false;
	protected static final int HEIGHT = 200;

	public Console(ConfigurationSet conf) {
		super(conf);
		setPreferredSize(new Dimension(0, HEIGHT));
		setBackground(Color.black);
		setVisible(conf.getBoolean(KEY_CONSOLE_VISIBLE, DEFAULT_VISIBILITY));
	}
	
	@Override
	public void setVisible(boolean visible) {
		conf.setBoolean(KEY_CONSOLE_VISIBLE, visible);
		super.setVisible(visible);
	}
}
