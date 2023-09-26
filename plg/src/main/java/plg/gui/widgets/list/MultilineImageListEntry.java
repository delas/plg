package plg.gui.widgets.list;

import javax.swing.Icon;

/**
 * This class describes an entry of a multiline list with an image
 *
 * @author Andrea Burattin
 */
public class MultilineImageListEntry {
	
	private int id;
	private Icon icon;
	private String firstLine;
	private String secondLine;
	private Object item;
	
	/**
	 * Basic constructor
	 * 
	 * @param id
	 * @param icon
	 * @param firstLine
	 */
	public MultilineImageListEntry(int id, Icon icon, String firstLine) {
		this(id, icon, firstLine, "", null);
	}
	
	/**
	 * Basic constructor
	 * 
	 * @param id
	 * @param icon
	 * @param firstLine
	 * @param secondLine
	 */
	public MultilineImageListEntry(int id, Icon icon, String firstLine, String secondLine) {
		this(id, icon, firstLine, secondLine, null);
	}
	
	/**
	 * Basic constructor
	 * 
	 * @param id
	 * @param icon
	 * @param firstLine
	 * @param secondLine
	 * @param item
	 */
	public MultilineImageListEntry(int id, Icon icon, String firstLine, String secondLine, Object item) {
		this.id = id;
		this.icon = icon;
		this.firstLine = firstLine;
		this.secondLine = secondLine;
		this.item = item;
	}

	/**
	 * This method returns the logical item associated to this entry
	 * 
	 * @return the item
	 */
	public Object getItem() {
		return item;
	}

	/**
	 * This method sets the logical item for this entry
	 * 
	 * @param item the item to set
	 */
	public void setItem(Object item) {
		this.item = item;
	}

	/**
	 * This method returns the entry id
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * This method returns the icon associated to this entry
	 * 
	 * @return the icon
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * This method returns the first line of this entry
	 * 
	 * @return the firstLine
	 */
	public String getFirstLine() {
		return firstLine;
	}

	/**
	 * This method returns the second line of this entry
	 * 
	 * @return the secondLine
	 */
	public String getSecondLine() {
		return secondLine;
	}
	
	@Override
	public String toString() {
		return this.firstLine + "\n" + this.secondLine;
	}
	
	/*@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		
		JFrame jf = new JFrame("Multiline Image List Entry");
		jf.setLayout(new BorderLayout());
		
		//Icon icon = new ImageIcon(ImageUtils.makeRoundedCorner(ImageIO.read(new File("resources/icons/plg.png")).getScaledInstance(48, 48, BufferedImage.SCALE_FAST), 10));
		Icon icon = new ImageIcon(ImageIO.read(new File("resources/icons/plg.png")).getScaledInstance(48, 48, BufferedImage.SCALE_FAST));
		
		DefaultListModel<MultilineImageListEntry> dlm = new DefaultListModel<MultilineImageListEntry>();
		dlm.addElement(new MultilineImageListEntry(1, icon, "prova", "altra prova"));
		dlm.addElement(new MultilineImageListEntry(2, icon, "prova", "altra prova"));
		dlm.addElement(new MultilineImageListEntry(3, icon, "prova", "altra prova"));
		dlm.addElement(new MultilineImageListEntry(4, icon, "prova", "altra prova"));
		
		JList<MultilineImageListEntry> list = new JList<MultilineImageListEntry>(dlm);
		list.setCellRenderer(new MultilineImageListEntryRenderer());
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBackground(Color.BLACK);
		p.add(list, BorderLayout.CENTER);
		
		jf.add(p, BorderLayout.CENTER);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jf.setMinimumSize(new Dimension(500, 500));
		jf.setVisible(true);
	}*/
}
