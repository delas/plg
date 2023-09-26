package plg.gui.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import plg.utils.Logger;

/**
 * Collection of utilities methods to work with images.
 * 
 * @author Andrea Burattin
 */
public class ImageUtils {
	
	/**
	 * This method load a resource as an image. The basic utility of this method
	 * is to wrap the exceptions, so it can be used to load static fields. This
	 * method also scales the image to the required dimensions
	 * 
	 * @param imageFile the resource to load
	 * @param width the target width
	 * @param height the target height
	 * @return the loaded image, or <tt>null</tt> if the resource is not
	 * available
	 */
	public static ImageIcon loadImage(String imageFile, int width, int height) {
		try {
//			return new ImageIcon(ImageIO.read(ImageUtils.class.getClassLoader().getResourceAsStream(imageFile)).getScaledInstance(width, height, BufferedImage.SCALE_FAST));
			return new ImageIcon(ImageUtils.class.getClassLoader().getResource(imageFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method load a resource as an image. The basic utility of this method
	 * is to wrap the exceptions, so it can be used to load static fields.
	 * 
	 * @param imageFile the resource to load
	 * @return the loaded image, or <tt>null</tt> if the resource is not
	 * available
	 */
	public static ImageIcon loadImage(String imageFile) {
		try {
			return new ImageIcon(ImageUtils.class.getClassLoader().getResource(imageFile));
		} catch (Exception e) {
			System.err.println("An error has been reported while loading a picture (but should be fine, `" + e.getMessage() + "').");
		}
		return null;
	}
	
	/**
	 * Given an image as input this method will create a buffered image
	 * containing the same image but with the rounded corners.
	 * 
	 * @param image the original image
	 * @param cornerRadius the radius of the rounded corner
	 * @return a buffered image with the rouded corners
	 */
	public static BufferedImage makeRoundedCorner(Image image, int cornerRadius) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2 = output.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
		return output;
	}
	
	/**
	 * Given an image, it is resized so to fit the new dimensions. The given
	 * dimensions, actually, are the maximum ones because the original image
	 * aspect ratio is kept.
	 * 
	 * @param original the original image
	 * @param width the maximum image width
	 * @param height the maximum image height
	 * @return a buffered image with the resized image
	 */
	public static BufferedImage scaleKeepingRatio(BufferedImage original, int width, int height) {
		int imgWidth = original.getWidth();
		int imgHeight = original.getHeight();
		double scalex = (double)width/imgWidth;
		double scaley = (double)height/imgHeight;
		double ratio = Math.min(scalex, scaley);
		int imgNewWidth = (int) (imgWidth*ratio);
		int imgNewHeight = (int) (imgHeight*ratio);
		
		BufferedImage output = new BufferedImage(imgNewWidth, imgNewHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = output.createGraphics();
		g2.drawImage(original, 0, 0, imgNewWidth, imgNewHeight, null);
		g2.dispose();
		
		return output;
	}
	
	/**
	 * Given an image, this method extracts a color that is calculated as the
	 * average of the colors of the image
	 * 
	 * @param image the given image
	 * @return the "average" color
	 */
	public static Color getAverageColor(BufferedImage image) {
		if (image == null) {
			return null;
		}
		Image i = image.getScaledInstance(1, 1, BufferedImage.SCALE_SMOOTH);
		int[] pixels = new int[1];
		PixelGrabber pg = new PixelGrabber(i, 0, 0, 1, 1, pixels, 0, 1);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) { }
		int  c = pixels[0];
		int  red = (c & 0x00ff0000) >> 16;
		int  green = (c & 0x0000ff00) >> 8;
		int  blue = c & 0x000000ff;
		return new Color(red, green, blue);
	}
	
	
	/**
	 * This method returns the color that is the average of two
	 * 
	 * @param color1 the first color
	 * @param color2 the second color
	 * @return the average color
	 */
	public static Color getColorAverage(Color color1, Color color2) {
		int r = (color1.getRed() + color2.getRed()) / 2;
		int g = (color1.getGreen() + color2.getGreen()) / 2;
		int b = (color1.getBlue() + color2.getBlue()) / 2;
		return new Color(r, g, b);
	}
	
	/**
	 * This method returns the average color of the border of an image
	 * 
	 * @param image the image to be considered
	 * @param borderSize the size of the border for calculating the average
	 * @return the average color of the border
	 */
	public static Color getAverageBorderColor(BufferedImage image, int borderSize) {
		if (image == null) {
			return null;
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		if (width < borderSize || height < borderSize) {
			borderSize = Math.min(width, height);
		}
		
		Color north = getAverageColor(image.getSubimage(0, 0, width, borderSize));
		Color east = getAverageColor(image.getSubimage(width - borderSize, 0, borderSize, height));
		Color south = getAverageColor(image.getSubimage(0, height - borderSize, width, borderSize));
		Color west = getAverageColor(image.getSubimage(0, 0, borderSize, height));
		return getColorAverage(getColorAverage(north, south), getColorAverage(east, west));
	}
}
