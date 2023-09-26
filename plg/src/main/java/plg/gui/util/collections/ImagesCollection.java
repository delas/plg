package plg.gui.util.collections;

import javax.swing.ImageIcon;

import plg.gui.util.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Collection of images used throughout the application
 * 
 * @author Andrea Burattin
 */
public class ImagesCollection {

	// General PLG icons
//	public static final ImageIcon PLG_ICON = ImageUtils.loadImage("/icons/plg.png");
	public static final ImageIcon PLG_ICON_SCALED = ImageUtils.loadImage("icons/application-icon.png", 32, 32);
	public static final ImageIcon ERROR_ICON = ImageUtils.loadImage("icons/error.png");
	public static final ImageIcon HELP_ICON = ImageUtils.loadImage("icons/famfamfam/book_open.png");
	public static final ImageIcon UPDATES_ICON = ImageUtils.loadImage("icons/updates.png");
	
	// Toolbar icons
	public static final ImageIcon ICON_NEW = ImageUtils.loadImage("icons/famfamfam/page_add.png");
	public static final ImageIcon ICON_OPEN = ImageUtils.loadImage("icons/famfamfam/folder_page.png");
	public static final ImageIcon ICON_DELETE = ImageUtils.loadImage("icons/famfamfam/page_delete.png");
	public static final ImageIcon ICON_EVOLVE = ImageUtils.loadImage("icons/famfamfam/page_refresh.png");
	public static final ImageIcon ICON_SAVE = ImageUtils.loadImage("icons/famfamfam/disk.png");
	public static final ImageIcon ICON_LOG = ImageUtils.loadImage("icons/famfamfam/table_go.png");
	public static final ImageIcon ICON_STREAM = ImageUtils.loadImage("icons/famfamfam/transmit_go.png");
	public static final ImageIcon ICON_CONSOLE = ImageUtils.loadImage("icons/famfamfam/application_xp_terminal.png");
	
	// Other icons
	public static final ImageIcon ICON_PLAY = ImageUtils.loadImage("icons/famfamfam/control_play_blue.png");
	public static final ImageIcon ICON_STOP = ImageUtils.loadImage("icons/famfamfam/control_stop_blue.png");
}
