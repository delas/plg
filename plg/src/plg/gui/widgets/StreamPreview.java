package plg.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import plg.exceptions.IllegalSequenceException;
import plg.generator.log.SimulationConfiguration;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.io.exporter.GraphvizBPMNExporter;
import plg.model.Process;
import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.StreamBuffer;
import plg.stream.model.StreamEvent;
import plg.stream.model.Streamer;
import plg.utils.Pair;

public class StreamPreview extends JPanel {

	private static final long serialVersionUID = -4021692478818224626L;
	private SoftReference<BufferedImage> buffer = null;
	private StreamBuffer streamBuffer;
	private StreamConfiguration streamConfiguration;
	
	public StreamPreview(StreamBuffer streamBuffer, StreamConfiguration streamConfiguration) {
		this.streamBuffer = streamBuffer;
		this.streamConfiguration = streamConfiguration;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		int height = this.getHeight();
		int width = this.getWidth();
		
		// create new back buffer
		buffer = new SoftReference<BufferedImage>(new BufferedImage(width, height, BufferedImage.TRANSLUCENT));
		Graphics2D g2d = buffer.get().createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// font stuff
		Font newFont = g2d.getFont().deriveFont(11f);
		g2d.setFont(newFont);
		FontMetrics fm = g2d.getFontMetrics();
		
		// background
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		
		// draw the channels
		g2d.setColor(Color.DARK_GRAY);
		int channelHeight = Math.min(height / streamConfiguration.maximumParallelInstances, 26);
		for (int i = 0; i < streamConfiguration.maximumParallelInstances; i++) {
			int y = (channelHeight * i);
			g2d.fillRect(0, y, width, channelHeight - 1);
		}
		
		// draw the vertical lines
		g2d.setColor(Color.BLACK);
		int verticalColumnsWidth = width / 6;
		for (int i = 0; i < 6; i++) {
			int x = verticalColumnsWidth * i;
			g2d.fillRect(x, 0, 1, height);
		}
		
		// proceed only if the stream buffer is not empty
		if (!streamBuffer.isEmpty()) {
			
			// get the timestamp of the first event
			long firstEventTime = Long.MAX_VALUE;
			for (int i = 0; i < streamConfiguration.maximumParallelInstances; i++) {
				if (!streamBuffer.get(i).isEmpty()) {
					firstEventTime = Math.min(firstEventTime, streamBuffer.get(i).peek().getDate().getTime());
				}
			}
			
			// draw the events
			int eventsIncluded = 0;
			boolean allEventsIncluded = true;
			int eventSize = channelHeight - 8;
			if (eventSize < 5) {
				eventSize = channelHeight - 2;
			}
			// compute events coordinates
			float maxEventsPerPoint = 0;
			Map<Pair<Integer, Integer>, Integer> pointsCoords = new HashMap<Pair<Integer,Integer>, Integer>();
			for (int i = 0; i < streamConfiguration.maximumParallelInstances; i++) {
				int y = (channelHeight * i) + (channelHeight / 2) - (eventSize / 2);
				ConcurrentLinkedDeque<StreamEvent> queue = streamBuffer.get(i);
				for (StreamEvent e : queue) {
					long eventTime = e.getDate().getTime();
					long timeRelative = (long) ((eventTime - firstEventTime) * streamConfiguration.timeMultiplier);
					int x = (int) (width * timeRelative / 60000);
					if (x <= width) {
						// manage multiple events into the same point
						Pair<Integer, Integer> coords = new Pair<Integer, Integer>(x, y);
						int points = 1;
						if (pointsCoords.containsKey(coords)) {
							points += pointsCoords.get(coords);
						}
						pointsCoords.put(coords, points);
						maxEventsPerPoint = Math.max(maxEventsPerPoint, points);
						eventsIncluded++;
					} else {
						allEventsIncluded = false;
					}
				}
			}
			// draw the actual pixels
			for (Pair<Integer, Integer> coords : pointsCoords.keySet()) {
				int x = coords.getFirst();
				int y = coords.getSecond();
				
				// compute the shade of the point
				float prop = 0;
				if (maxEventsPerPoint > 1) {
					prop = ((pointsCoords.get(coords) - 1f) / (maxEventsPerPoint - 1f));
				}
				// draw the circle with the provided color
				g2d.setColor(Color.getHSBColor(0f, prop, 1f));
				g2d.fillOval(x, y, eventSize, eventSize);
			}
			
			// draw scale
			if (maxEventsPerPoint > 1) {
				g2d.setPaint(new GradientPaint(0, 0, Color.red, 40, 0, Color.white));
				g2d.fillRect(3, height-10, 40, 7);
				
				g2d.drawString("" + maxEventsPerPoint, 0, height - 10);
				g2d.drawString("1", 40, height - 10);
			}
			
			// draw final strings
			String eventsIncludedText = eventsIncluded + " events in the first minute";
			if (allEventsIncluded) {
				eventsIncludedText = "At least " + eventsIncludedText + " (estimated value)";
			}
			g2d.setColor(new Color(0, 0, 0, 200));
			g2d.fillRect(width - fm.stringWidth(eventsIncludedText) - 4, height - fm.getHeight(), fm.stringWidth(eventsIncludedText) + 2, fm.getHeight() - 2);
			g2d.setColor(Color.WHITE);
			g2d.drawString(eventsIncludedText, width - fm.stringWidth(eventsIncludedText) - 2, height - 4);
		}
		
		// final paint stuff
		g2d.dispose();
		Rectangle clip = g.getClipBounds();
		g.drawImage(buffer.get(), clip.x, clip.y, clip.x + clip.width, clip.y + clip.height,
				clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, null);
	}
	
	
	public static void main(String[] args) throws IllegalSequenceException {
		final StreamConfiguration sc = new StreamConfiguration();
		sc.maximumParallelInstances = 15;
		sc.timeMultiplier = 0.005;
		
		Process p = new Process("test");
//		StartEvent start = p.newStartEvent();
//		Task A = p.newTask("A"); p.newSequence(start, A);
//		EndEvent end = p.newEndEvent(); p.newSequence(A, end);
		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
		GraphvizBPMNExporter e = new GraphvizBPMNExporter();
		e.exportModel(p, "C:\\Users\\Andrea\\Desktop\\model.dot");
		Streamer s = new Streamer(sc, p, new SimulationConfiguration());
		StreamBuffer sb = s.getBuffer();
//		StreamBuffer sb = new StreamBuffer(sc);
//		XTrace t1 = XLogHelper.createTrace("t1");
//		XLogHelper.insertEvent(t1, "a", new Date(115, 0, 1));
//		XLogHelper.insertEvent(t1, "b", new Date(115, 0, 1));
//		XLogHelper.insertEvent(t1, "b", new Date(115, 0, 1));
//		XLogHelper.insertEvent(t1, "c", new Date(115, 0, 2));
//		XLogHelper.insertEvent(t1, "c", new Date(115, 0, 2));
//		XTrace t2 = XLogHelper.createTrace("t2");
//		XLogHelper.insertEvent(t2, "a", new Date(115, 0, 1));
//		XLogHelper.insertEvent(t2, "b", new Date(115, 0, 2));
//		XLogHelper.insertEvent(t2, "c", new Date(115, 0, 3));
////		XTrace t3 = XLogHelper.createTrace("t3");
////		XLogHelper.insertEvent(t3, "a", new Date(115, 0, 1));
////		XLogHelper.insertEvent(t3, "b", new Date(115, 0, 2));
////		XLogHelper.insertEvent(t3, "c", new Date(115, 0, 3));
//		
//		sb.enqueueTrace(t1);
//		sb.enqueueTrace(t2);
////		sb.enqueueTrace(t3);
		
		final StreamPreview sp = new StreamPreview(sb, sc);
		
		final JSlider sl = new JSlider(1, 50);
		sl.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				double val = (double) sl.getValue() / 10000d;
				//System.out.println(val);
				sc.timeMultiplier = val;
				sp.updateUI();
			}
		});
		sl.setValue(20);
		
		JFrame f = new JFrame();
		f.setLocation(500, 300);
		f.setSize(400, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("test buffer preview");
		f.setLayout(new BorderLayout());
		f.add(sp, BorderLayout.CENTER);
		f.add(sl, BorderLayout.SOUTH);
		f.setVisible(true);
	}
}
