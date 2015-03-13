package plg.gui.util;

import javax.swing.ImageIcon;

/**
 * Collection of images used throughout the application
 * 
 * @author Andrea Burattin
 */
public class ImagesCollection {

	// General PLG icons
	public static final ImageIcon PLG_ICON = ImageUtils.loadImage("resources/icons/plg.png");
	public static final ImageIcon PLG_ICON_SCALED = ImageUtils.loadImage("resources/icons/application-icon.png", 32, 32);
	
	// Toolbar icons
	public static final ImageIcon ICON_NEW = ImageUtils.loadImage("resources/icons/famfamfam/page_add.png");
	public static final ImageIcon ICON_OPEN = ImageUtils.loadImage("resources/icons/famfamfam/folder_page.png");
	public static final ImageIcon ICON_SAVE = ImageUtils.loadImage("resources/icons/famfamfam/disk.png");
	public static final ImageIcon ICON_LOG = ImageUtils.loadImage("resources/icons/famfamfam/table_go.png");
	public static final ImageIcon ICON_STREAM = ImageUtils.loadImage("resources/icons/famfamfam/transmit_go.png");
	public static final ImageIcon ICON_CONSOLE = ImageUtils.loadImage("resources/icons/famfamfam/application_xp_terminal.png");
}
