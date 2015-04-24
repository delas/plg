package plg.gui.util.collections;

import javax.swing.ImageIcon;

import plg.gui.util.ImageUtils;

/**
 * Collection of images used throughout the application
 * 
 * @author Andrea Burattin
 */
public class ImagesCollection {

	// General PLG icons
	public static final ImageIcon PLG_ICON = ImageUtils.loadImage("/plg/resources/icons/plg.png");
	public static final ImageIcon PLG_ICON_SCALED = ImageUtils.loadImage("/plg/resources/icons/application-icon.png", 32, 32);
	public static final ImageIcon ERROR_ICON = ImageUtils.loadImage("/plg/resources/icons/error.png");
	public static final ImageIcon HELP_ICON = ImageUtils.loadImage("/plg/resources/icons/famfamfam/book_open.png");
	
	// Toolbar icons
	public static final ImageIcon ICON_NEW = ImageUtils.loadImage("/plg/resources/icons/famfamfam/page_add.png");
	public static final ImageIcon ICON_OPEN = ImageUtils.loadImage("/plg/resources/icons/famfamfam/folder_page.png");
	public static final ImageIcon ICON_DELETE = ImageUtils.loadImage("/plg/resources/icons/famfamfam/page_delete.png");
	public static final ImageIcon ICON_EVOLVE = ImageUtils.loadImage("/plg/resources/icons/famfamfam/page_refresh.png");
	public static final ImageIcon ICON_SAVE = ImageUtils.loadImage("/plg/resources/icons/famfamfam/disk.png");
	public static final ImageIcon ICON_LOG = ImageUtils.loadImage("/plg/resources/icons/famfamfam/table_go.png");
	public static final ImageIcon ICON_STREAM = ImageUtils.loadImage("/plg/resources/icons/famfamfam/transmit_go.png");
	public static final ImageIcon ICON_CONSOLE = ImageUtils.loadImage("/plg/resources/icons/famfamfam/application_xp_terminal.png");
	
	// Other icons
	public static final ImageIcon ICON_PLAY = ImageUtils.loadImage("/plg/resources/icons/famfamfam/control_play_blue.png");
	public static final ImageIcon ICON_STOP = ImageUtils.loadImage("/plg/resources/icons/famfamfam/control_stop_blue.png");
}
