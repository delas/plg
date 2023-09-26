package plg.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import plg.generator.IProgressVisualizer;
import plg.gui.util.HumanTimeFormatter;
import plg.utils.SetUtils;

/**
 * This panel represents a progress panel, useful to perform long operations
 * and give the user some progress feedback
 * 
 * @author Andrea Burattin
 */
public class Progress extends JPanel implements IProgressVisualizer {

	private static final long serialVersionUID = 2964186837553703486L;
	private static final int UPDATE_PROGRESS_INTERVAL = 1000;
	
	protected JProgressBar progress = new JProgressBar();
	protected JLabel progressLabel = new JLabel("Please wait...");
	final protected JLabel ETALabel = new JLabel("some time to wait...");
	
	protected int min = 0;
	protected int max = 100;
	protected int value = 0;
	
	protected long startTime = 0;
	protected Timer etaUpdater;
	
	/**
	 * Protected constructor. The standard way to get a progress is via the
	 * {@link ProgressStack#askForNewProgress()} method.
	 */
	protected Progress() {
		ETALabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(progress, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5, 5, 15, 5);
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(progressLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(5, 5, 15, 5);
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(ETALabel, c);
		
		setVisible(false);
	}
	
	@Override
	public void setMinimum(int minimum) {
		this.min = minimum;
		this.value = minimum;
		this.progress.setMinimum(minimum);
		this.progress.setValue(minimum);
		setIndeterminate(false);
	}

	@Override
	public void setMaximum(int maximum) {
		this.max = maximum;
		this.progress.setMaximum(maximum);
	}

	@Override
	public void inc() {
		this.value++;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					progress.setValue(value);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setIndeterminate(final boolean indeterminate) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					progress.setIndeterminate(indeterminate);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setText(String status) {
		progressLabel.setText(status);
	}

	@Override
	public void start() {
		startTime = System.currentTimeMillis();
		etaUpdater = new Timer();
		etaUpdater.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!progress.isIndeterminate() && value > 0) {
					long time = System.currentTimeMillis() - startTime;
					long eta = time * (max - value) / value;
					ETALabel.setText("About " + HumanTimeFormatter.formatTime(eta) + " remaining");
				} else {
					ETALabel.setText(SetUtils.getRandom(waitingSentences));
				}
			}
		}, 0, UPDATE_PROGRESS_INTERVAL);
		setVisible(true);
	}
	
	@Override
	public void finished() {
		etaUpdater.cancel();
		setVisible(false);
	}
}
