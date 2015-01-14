package plg.gui.widgets;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import plg.gui.util.ImageUtils;

/**
 * This class can be used to create rounded panel with a custom background.
 *
 * @author Andrea Burattin
 */
public class RoundedPanel extends JPanel {

	private static final long serialVersionUID = -2094839410151037318L;
	protected SoftReference<BufferedImage> buffer = null;
	private BufferedImage background = null;
	private BACKGROUND_POLICY backgroundPolicy = BACKGROUND_POLICY.SCALED;
	private int borderRadius = 0;
	private int margin = 0;
	
	/** 
	 * This enumeration describes the possible policy for painting the
	 * background
	 */
	public enum BACKGROUND_POLICY {
		/**
		 * The background is not scaled and centered to the panel
		 */
		CENTERED,
		/**
		 * The background is scaled (keeping the aspect ration) to the panel
		 * dimensions
		 */
		SCALED,
		/**
		 * The background is not scaled and tiled to fill the panel
		 */
		TILED
	};
	
	/**
	 * Default constructor
	 */
	public RoundedPanel() {
		this(30);
	}
	
	/**
	 * Class constructor, which creates a new rounded panel with the provided
	 * border radius
	 * 
	 * @param borderRadius the border radius of the newly created panel
	 */
	public RoundedPanel(int borderRadius) {
		this.borderRadius = borderRadius;
		this.margin = (int) Math.sqrt(borderRadius) * 2;
		this.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
	}
	
	/**
	 * This method sets the border radius
	 * 
	 * @param borderRadius
	 */
	public void setBorderRadius(int borderRadius) {
		this.borderRadius = borderRadius;
	}
	
	/**
	 * This method gets the current border radius
	 * 
	 * @return
	 */
	public int getBorderRadius() {
		return this.borderRadius;
	}
	
	/**
	 * This method sets the background image
	 * 
	 * @param background
	 */
	public void setBackgroundImage(BufferedImage background) {
		this.background = background;
		repaint();
	}
	
	/**
	 * This method gets the current background painting policy
	 * 
	 * @return
	 */
	public BACKGROUND_POLICY getBackgroundPolicy() {
		return this.backgroundPolicy;
	}
	
	/**
	 * This method sets the background painting policy
	 * 
	 * @param backgroundPolicy
	 * @see BACKGROUND_POLICY
	 */
	public void setBackgroundPolicy(BACKGROUND_POLICY backgroundPolicy) {
		this.backgroundPolicy = backgroundPolicy;
	}
	

	@Override
	public void paintComponent(Graphics g) {
		int height = this.getHeight();
		int width = this.getWidth();
		buffer = new SoftReference<BufferedImage>(new BufferedImage(width, height, BufferedImage.TRANSLUCENT));
		
		// create new back buffer
		if (isOpaque()) {
			Graphics2D g2d = buffer.get().createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, borderRadius, borderRadius));
			g2d.setComposite(AlphaComposite.SrcAtop);
			
			g2d.setColor(getBackground());
			g2d.fillRect(0, 0, width, height);
			if (this.background != null) {
				if (backgroundPolicy.equals(BACKGROUND_POLICY.SCALED)) {
					
					BufferedImage resizedBakcground = ImageUtils.scaleKeepingRatio(background, getWidth(), getHeight());
					int offsetX = (getWidth() - resizedBakcground.getWidth()) / 2;
					int offsetY = (getHeight() - resizedBakcground.getHeight()) / 2;
					g2d.drawImage(resizedBakcground, offsetX, offsetY, resizedBakcground.getWidth(), resizedBakcground.getHeight(), null);
					
				} else if (backgroundPolicy.equals(BACKGROUND_POLICY.CENTERED)) {
					
					int offsetX = (getWidth() - background.getWidth()) / 2;
					int offsetY = (getHeight() - background.getHeight()) / 2;
					g2d.drawImage(background, offsetX, offsetY, background.getWidth(), background.getHeight(), null);
					
				} else if (backgroundPolicy.equals(BACKGROUND_POLICY.TILED)) {
					
					int imgWidth = background.getWidth();
					int imgHeight = background.getHeight();
					for (int x = 0; x < width; x += imgWidth) {
						for (int y = 0; y < height; y += imgHeight) {
							g2d.drawImage(background, x, y, background.getWidth(), background.getHeight(), null);
						}
					}
					
				}
			}
			
			// paint stuff
			g2d.dispose();
			Rectangle clip = g.getClipBounds();
			g.drawImage(buffer.get(), clip.x, clip.y, clip.x + clip.width, clip.y + clip.height,
					clip.x, clip.y, clip.x + clip.width, clip.y + clip.height, null);
		}
	}
	
	/*public static void main(String[] args) throws IOException {
		JFrame jf = new JFrame("Rounded Panel");
		RoundedPanel p = new RoundedPanel(30);
		p.setBackground(Color.BLACK);
		p.setBackgroundImage(ImageIO.read(new File("resources/test.jpg")));
		p.setBackgroundPolicy(BACKGROUND_POLICY.CENTERED);
		
		JPanel p1 = new JPanel();
		p1.setBorder(new EmptyBorder(10, 10, 10, 10));
		p1.setLayout(new BorderLayout());
		p1.add(p);
		
		jf.setLayout(new BorderLayout());
		jf.add(p1, BorderLayout.CENTER);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.setMinimumSize(new Dimension(200, 200));
		jf.setVisible(true);
	}*/
}
