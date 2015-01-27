package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;
import plg.gui.widgets.list.MultilineImageListEntry;
import plg.gui.widgets.list.MultilineImageListEntryRenderer;
import plg.model.Process;
import plg.utils.Logger;

public class ProcessesList extends MainWindowPanel {

	private static final long serialVersionUID = 3733893133192755973L;
	
	// list configuration
	protected static final int WIDTH = 300;
	
	// list item configuration
	protected static Icon ITEM_ICON = null;
	protected static final Color ITEM_SELECTED_BACKGROUND = Color.lightGray;
	protected static final Border ITEM_BORDER = BorderFactory.createEmptyBorder(7, 5, 7, 5);
	protected static final Border ITEM_SELECTED_BORDER = ITEM_BORDER;
	
	private DefaultListModel<MultilineImageListEntry> dlm;
	private JList<MultilineImageListEntry> list;
	private int idCurrentProcess = -1;

	public ProcessesList(ConfigurationSet conf) {
		super(conf);
		// populate item icon
		if (ITEM_ICON == null) {
			try {
				ITEM_ICON = new ImageIcon(ImageIO.read(new File("resources/icons/plg.png")).getScaledInstance(48, 48, BufferedImage.SCALE_FAST));
			} catch (IOException e) { }
		}
		
		this.dlm = new DefaultListModel<MultilineImageListEntry>();
		this.list = new JList<MultilineImageListEntry>(dlm);
		this.list.setCellRenderer(new MultilineImageListEntryRenderer<>());
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = list.getSelectedIndex();
				if (index >= 0) {
					MultilineImageListEntry entry = dlm.get(index);
					if (entry.getId() != idCurrentProcess) {
						Process p = (Process) entry.getItem();
						ApplicationController.instance().processes().visualizeProcess(p);
						idCurrentProcess = entry.getId();
					}
				}
			}
		});
		this.list.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					int index = list.getSelectedIndex();
					if (index >= 0) {
						MultilineImageListEntry entry = dlm.get(index);
						dlm.remove(index);
						if (dlm.getSize() == 0) {
							list.clearSelection();
							ApplicationController.instance().processes().visualizeProcess(null);
						} else {
							if (index == 0) {
								list.setSelectedIndex(0);
							} else {
								list.setSelectedIndex(index - 1);
							}
						}
						Logger.instance().info("Removed process with id " + entry.getId());
					}
				}
			}
			
			@Override
			public void keyTyped(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) { }
		});
		
		setPreferredSize(new Dimension(WIDTH, 0));
		setMinimumSize(new Dimension(WIDTH, 0));
		setLayout(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void storeNewProcess(int id, String firstLine, String secondLine, Process process) {
		MultilineImageListEntry element = new MultilineImageListEntry(id, ITEM_ICON, firstLine, secondLine, process);
		dlm.insertElementAt(element, 0);
		list.setSelectedIndex(0);
	}
}
