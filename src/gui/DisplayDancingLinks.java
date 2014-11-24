package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import puzzle.DancingLinks;
import util.DataFileParser;
import util.Tile;

/**
 * A class to display the result in a GUI. The DisplayResults instance has two
 * panels, a control panel and a tiles display panel. The control panel includes
 * two parts, configuration and result display. Previously Control panel is
 * programmed in a stand-alone class.
 *
 * @author David
 * @version 1.0 11/16/2014
 *
 */
public class DisplayDancingLinks extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * A dancinglinks instance is included.
	 */
	private DancingLinks dancinglinks;
	/**
	 * A positions list to store the result.
	 */
	private List<List<List<Integer>>> positions;
	/**
	 * The number of possible solutions.
	 */
	private int numOfSolution;

	private char board[][];

	private final Color color[] = { Color.cyan, Color.blue, Color.green,
			Color.red, Color.yellow, new Color(46, 139, 87),
			new Color(148, 0, 211), new Color(135, 51, 36), Color.magenta,
			Color.gray, Color.pink, new Color(175, 255, 225),
			new Color(130, 175, 190) };

	/**
	 * Control panel.
	 */
	private JPanel pControl;

	/**
	 * Display panel to show tiles.
	 */
	private JPanel pDisplay;

	/**
	 * Use to map solution from DancingLinks class to actual position. In order
	 * to reduce the time complexity to O(1), an array is used.
	 */
	private int posMap[];
	/**
	 * Configure panel
	 */
	JPanel pConfig;
	JCheckBox cbEnableSpin;
	JCheckBox cbEnableSpinFlip;
	JCheckBox cbExtra;
	JButton bSolve;
	/**
	 * Result panel.
	 */
	JPanel pResult;
	JLabel tResultInfo;
	JTextField tIndex;
	JButton bShowResult;
	JButton bPre;
	JButton bNext;
	JButton bPlay;
	JSlider slider;

	/* Menu related */
	/**
	 * Declare of menu variables. b- for button, t- for text field, l- for
	 * label; m- for menu, mi- for menu item;
	 *
	 */
	private JMenuBar mBar;
	private JMenu mFile;
	private JMenu mHelp;
	private JMenuItem miExit;
	private JMenuItem miRead;
	private JMenuItem miAbout;

	private JFileChooser fc;

	/**
	 * For autoplaying all solutions.
	 */
	private boolean isRunning = false;

	/**
	 * For playing all solutions.
	 */
	private boolean isThread = false;

	/**
	 * Size parameters.
	 */
	private static final int frameSize[] = { 820, 540 };
	private static final int framePos[] = { 400, 20 };
	private static final int displaySize[] = { 600, 480 };
	private static final int displayPos[] = { 0, 0 };
	private static final int gridWidth = 4;

	private int sizeBlock;
	private int sizeTile;
	/**
	 * The origin point from the left-top of the board to pDisplay panel.
	 */
	private int origin[] = { 20, 20 };
	private int originTile[] = { 20 + gridWidth / 2, 20 + gridWidth / 2 };

	/**
	 * @deprecated This constructer has been depreciated. DancingLinks are built
	 *             by selecting a file in GUI menu.
	 *
	 * @param d
	 * @param b
	 */
	@Deprecated
	public DisplayDancingLinks(DancingLinks d, Tile b) {

		super(null);
		this.dancinglinks = d;
		board = new char[b.data.length][b.data[0].length];
		for (int i = 0; i < b.data.length; i++) {
			for (int j = 0; j < b.data[0].length; j++) {
				board[i][j] = b.data[i][j];
			}
		}

		/*
		 * There should be some redundancy for the sizeblock, or the edge cannot
		 * be drawn.
		 */
		sizeBlock = Math.min(
				(displaySize[0] - origin[0] - 5) / board[0].length,
				(displaySize[1] - origin[1] - 5) / board.length)
				- gridWidth;
		sizeTile = sizeBlock + gridWidth;
		System.out.println("Size of block: " + sizeBlock);

		this.setLocation(0, 0);
		this.setSize(frameSize[0] - 20, frameSize[1] - 20);
		this.setOpaque(true);
		this.setVisible(true);
		this.setFocusable(true);

		setupMenu();
		setupControlPanel();
		setupDisplay();
		setupBoard();

		setPosMap();
	}

	public DisplayDancingLinks() {
		super(null);

		this.setLocation(0, 0);
		this.setSize(frameSize[0] - 20, frameSize[1] - 20);
		this.setOpaque(true);
		this.setVisible(true);
		this.setFocusable(true);

		setupMenu();
		setupControlPanel();
		setupDisplay();

	}

	public void setupDancinglinks(DancingLinks dls) {
		this.dancinglinks = dls;
		board = new char[dls.getBoard().data.length][dls.getBoard().data[0].length];
		for (int i = 0; i < dls.getBoard().data.length; i++) {
			for (int j = 0; j < dls.getBoard().data[0].length; j++) {
				board[i][j] = dls.getBoard().data[i][j];
			}
		}

		/*
		 * There should be some redundancy for the sizeblock, or the edge cannot
		 * be drawn.
		 */
		sizeBlock = Math.min(
				(displaySize[0] - origin[0] - 5) / board[0].length,
				(displaySize[1] - origin[1] - 5) / board.length)
				- gridWidth;
		sizeTile = sizeBlock + gridWidth;
		System.out.println("Size of block: " + sizeBlock);

		setupBoard();
		setPosMap();
		repaint();
	}

	public void setupControlPanel() {

		pControl = new JPanel();
		pControl.setLocation(10, 10);
		pControl.setSize(150, 355);
		pControl.setOpaque(true);
		pControl.setVisible(true);
		pControl.setFocusable(true);
		// pControl.setLayout(new BoxLayout(pControl, BoxLayout.Y_AXIS));
		pControl.setLayout(null);
		/*
		 * this.setBorder(BorderFactory.createCompoundBorder(
		 * BorderFactory.createTitledBorder(""),
		 * BorderFactory.createEmptyBorder(5,5,5,5)));
		 */

		/* Initialize control sub-panel. */
		pConfig = new JPanel();
		pConfig.setLocation(0, 0);
		pConfig.setSize(150, 160);
		pConfig.setOpaque(true);
		pConfig.setVisible(true);
		pConfig.setFocusable(true);
		pConfig.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Control"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		pConfig.setLayout(null);

		cbEnableSpin = new JCheckBox("Enable spin");
		cbEnableSpin.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cbEnableSpin.setSelected(false);
		cbEnableSpin.setSize(100, 40);
		cbEnableSpin.setLocation(10, 20);
		cbEnableSpin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (cbEnableSpin.isSelected())
					dancinglinks.setEnableSpin(true);
				else
					dancinglinks.setEnableSpin(false);
			}

		});
		pConfig.add(cbEnableSpin);

		cbEnableSpinFlip = new JCheckBox("Enable spin + flip");
		cbEnableSpinFlip.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cbEnableSpinFlip.setSelected(false);
		cbEnableSpinFlip.setSize(130, 40);
		cbEnableSpinFlip.setLocation(10, 50);
		cbEnableSpinFlip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (cbEnableSpinFlip.isSelected())
					dancinglinks.setEnableSpinFlip(true);
				else
					dancinglinks.setEnableSpinFlip(false);
			}

		});
		pConfig.add(cbEnableSpinFlip);

		cbExtra = new JCheckBox("Extra blocks");
		cbExtra.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cbExtra.setSelected(false);
		cbExtra.setSize(130, 40);
		cbExtra.setLocation(10, 80);
		cbExtra.setEnabled(false);
		cbExtra.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (cbExtra.isSelected())
					dancinglinks.setEnableSpin(true);
				else
					dancinglinks.setEnableSpin(false);
			}

		});
		pConfig.add(cbExtra);

		bSolve = new JButton("Solve it!");
		bSolve.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				/* Before every calculation clear cntSolution and position list. */
				dancinglinks.setCntSolution(0);
				dancinglinks.setFail(false);

				/*
				 * Run pre-process only after spin and flip setting and before
				 * solving.
				 */
				dancinglinks.preprocess();
				cbExtra.setSelected(dancinglinks.isEnableExtra());
				tResultInfo.setText("Calculating...");
				dancinglinks.solve();
				positions = dancinglinks.getPosition();
				numOfSolution = positions.size();

				/*
				 * After geeting the numofSolution, set the min and max of the
				 * slider.
				 */
				slider.setMinimum(1);
				slider.setMaximum(numOfSolution);
				slider.setValue(1);

				// dancinglinks.printPositions();
				if (numOfSolution == 0)
					tResultInfo.setText("No solutions!");
				else if (numOfSolution == 1)
					tResultInfo.setText("Only 1 solution!");
				else
					tResultInfo.setText(numOfSolution + " solutions!");
			}

		});
		bSolve.setSize(80, 26);
		bSolve.setLocation(15, 120);

		pConfig.add(bSolve);
		pControl.add(pConfig);

		/* Initialize result sub-panel. */
		pResult = new JPanel();
		pResult.setSize(150, 184);
		pResult.setLocation(0, 170);
		pResult.setOpaque(true);
		pResult.setVisible(true);
		pResult.setFocusable(true);
		pResult.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Result"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pResult.setLayout(null);

		tResultInfo = new JLabel("Press button to solve");
		// tResultInfo.setPreferredSize(new Dimension(60, 30));
		tResultInfo.setSize(new Dimension(140, 30));
		tResultInfo.setLocation(10, 20);
		tResultInfo.setVisible(true);
		pResult.add(tResultInfo);

		tIndex = new JTextField(" index", 10);
		// tIndex.setPreferredSize(new Dimension(80, 25));
		tIndex.setSize(new Dimension(50, 26));
		// tIndex.setMaximumSize(new Dimension(80, 25));
		tIndex.setVisible(true);
		tIndex.setLocation(10, 50);
		pResult.add(tIndex);

		bShowResult = new JButton("Show!");
		bShowResult.setSize(72, 26);
		bShowResult.setLocation(68, 50);
		bShowResult.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int id = Integer.parseInt(tIndex.getText());
				if (id >= 1 && id <= numOfSolution) {
					cleanTiles();
					displayTiles(id - 1);

					/* Set slider. */
					slider.setValue(id);
				}

				else {
					System.err.println("Out of Range!");
					JOptionPane.showMessageDialog(null, "Index out of range!",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}

		});
		pResult.add(bShowResult);

		bPre = new JButton("Prev");
		bPre.setSize(60, 26);
		bPre.setLocation(10, 84);
		bPre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int cur = Integer.parseInt(tIndex.getText());
				if (cur > 1) {
					cur--;
					tIndex.setText(Integer.toString(cur));
					cleanTiles();
					displayTiles(cur - 1);
					/* Set slider. */
					slider.setValue(cur);
				}
			}

		});
		pResult.add(bPre);

		bNext = new JButton("Next");
		bNext.setSize(60, 26);
		bNext.setLocation(80, 84);
		bNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int cur = Integer.parseInt(tIndex.getText());
				if (cur < numOfSolution) {
					cur++;
					tIndex.setText(Integer.toString(cur));
					cleanTiles();
					displayTiles(cur - 1);
					/* Set slider. */
					slider.setValue(cur);
				}

			}

		});
		pResult.add(bNext);

		slider = new JSlider();
		slider.setSize(140, 26);
		slider.setLocation(5, 120);
		slider.setExtent(0);
		slider.setMinimum(1);
		slider.setMaximum(100);
		slider.setSnapToTicks(false);
		slider.setPaintTicks(false);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				tIndex.setText(Integer.toString(slider.getValue()));
			}

		});

		pResult.add(slider);

		bPlay = new JButton("Autoplay all solutions");
		bPlay.setSize(130, 26);
		bPlay.setLocation(10, 146);
		bPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				System.out.println("Is running: " + isRunning);
				System.out.println("There is a thread? " + isThread);

				if (!isRunning) {
					isRunning = true;
					bPlay.setText("Stop");
					if (!isThread) {
						isThread = true;
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub

								while (tIndex.getText() != ""
										&& Integer.parseInt(tIndex.getText()) < numOfSolution) {
									if (isRunning) {
										int i = Integer.parseInt(tIndex
												.getText());
										cleanTiles();
										displayTiles(i);
										slider.setValue(i + 1);
										try {
											Thread.sleep(500);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}
								}
								isThread = false;
								isRunning = false;
								bPlay.setText("Autoplay all solutions");
								slider.setValue(1);
							}
						}).start();

					}
				}

				/* If now it is running, stop it! */
				else {
					isRunning = false;
					bPlay.setText("Autoplay all solutions");

				}
			}
		});
		pResult.add(bPlay);
		pControl.add(pResult);
		this.add(pControl);
	}

	public void setupDisplay() {
		pDisplay = new JPanel();
		pDisplay.setLayout(null);
		pDisplay.setLocation(160, 0);
		pDisplay.setSize(displaySize[0], displaySize[1]);
		pDisplay.setOpaque(true);
		pDisplay.setVisible(true);
		pDisplay.setFocusable(true);

		this.add(pDisplay);
	}

	public void setupMenu() {
		mBar = new JMenuBar();
		mBar.setOpaque(true);

		mFile = new JMenu("File");
		mFile.setMnemonic(KeyEvent.VK_F);

		miRead = new JMenuItem("Read...");
		miRead.setMnemonic(KeyEvent.VK_R);
		miRead.addActionListener(this);
		mFile.add(miRead);

		miExit = new JMenuItem("Exit");
		miExit.setMnemonic(KeyEvent.VK_E);
		miExit.addActionListener(this);
		mFile.add(miExit);

		mHelp = new JMenu("Help");
		mHelp.setMnemonic(KeyEvent.VK_H);
		miAbout = new JMenuItem("About");
		miAbout.setMnemonic(KeyEvent.VK_A);
		miAbout.addActionListener(this);
		mHelp.add(miAbout);

		mBar.add(mFile);
		mBar.add(mHelp);

		fc = new JFileChooser();
		fc.setCurrentDirectory(new File(".\\testcases"));

	}

	public void setupBoard() {

		/* There are n kind of colors in the board. */
		Set<Character> set = new HashSet<Character>();

		int w = board.length;
		int l = board[0].length;
		System.out.println("Width: " + w + " Length: " + l);

		/* First is x, second is y. */
		int sizeGridH[] = { l * (sizeBlock + gridWidth) + gridWidth, gridWidth };
		int sizeGridV[] = { gridWidth, w * (sizeBlock + gridWidth) + gridWidth };
		JPanel gridH[] = new JPanel[w + 1];
		JPanel gridV[] = new JPanel[l + 1];

		/* Setup horizontal and vertical grids */
		for (int i = 0; i <= w; i++) {
			gridH[i] = new JPanel();
			gridH[i].setBackground(Color.black);
			gridH[i].setSize(sizeGridH[0], sizeGridH[1]);
			gridH[i].setLocation(origin[0], origin[1] + (sizeBlock + gridWidth)
					* i);
			gridH[i].setOpaque(true);
			gridH[i].setVisible(true);
			pDisplay.add(gridH[i]);
		}

		for (int j = 0; j <= l; j++) {
			gridV[j] = new JPanel();
			gridV[j].setBackground(Color.black);
			gridV[j].setSize(sizeGridV[0], sizeGridV[1]);
			gridV[j].setLocation(origin[0] + (sizeBlock + gridWidth) * j,
					origin[1]);
			gridV[j].setOpaque(true);
			gridV[j].setVisible(true);
			pDisplay.add(gridV[j]);
		}

		/* Setup missing blocks if there are any. */
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < l; j++) {
				if (board[i][j] == ' ') {

					JPanel block = new JPanel();
					block.setBackground(Color.BLACK);
					block.setSize(sizeTile, sizeTile);
					int x = originTile[0] + (j) * sizeTile;
					int y = originTile[1] + (i) * sizeTile;
					block.setLocation(x, y);
					/* Set it to transparent to display the chars on a board. */
					block.setOpaque(true);
					block.setVisible(true);
					pDisplay.add(block);
				} else
					set.add(board[i][j]);
			}
		}
		Object o[] = set.toArray();

		for (int s = 1; s < set.size(); s++) {
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < l; j++) {
					if (board[i][j] == (char) o[s]) {
						JLabel block = new JLabel(
								Character.toString((char) o[s]));
						block.setSize(sizeTile / 2, sizeTile / 2);
						block.setFont(new Font(block.getFont().getName(),
								Font.BOLD, 28));
						int x = originTile[0] + (j) * sizeTile + sizeTile / 3;
						int y = originTile[1] + (i) * sizeTile + sizeTile / 3;
						block.setLocation(x, y);
						block.setOpaque(false);
						block.setVisible(true);
						pDisplay.add(block);
					} else
						set.add(board[i][j]);
				}
			}
		}

		System.out.println(set);
	}

	private void displayTiles(int id) {

		List<List<Integer>> pos = positions.get(id);
		int number = pos.size();

		if (id >= numOfSolution) {
			System.err.println("Index of solutions out of range!");
			return;
		}

		for (int i = 0; i < number; i++) {
			List<Integer> tilePos = new ArrayList<Integer>();
			tilePos = pos.get(i);
			Color c = color[tilePos.get(0)];
			for (int j = 1; j < tilePos.size(); j++) {
				JPanel block = new JPanel();
				block.setBackground(c);
				block.setSize(sizeTile, sizeTile);
				int x = originTile[0]
						+ (posMap[tilePos.get(j)] % (board[0].length))
						* sizeTile;
				int y = originTile[0]
						+ (posMap[tilePos.get(j)] / (board[0].length))
						* sizeTile;
				block.setLocation(x, y);
				block.setOpaque(true);
				block.setVisible(true);
				pDisplay.add(block);

			}
		}
		pDisplay.repaint();

	}

	private void cleanTiles() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				/* Avoid deleting the grids. */
				int x = originTile[0] + j * sizeTile + gridWidth + 2;
				int y = originTile[0] + i * sizeTile + gridWidth + 2;

				if (board[i][j] != ' ') {
					Component t = pDisplay.getComponentAt(x, y);
					pDisplay.remove(t);
				}
			}
		}
		pDisplay.repaint();
	}

	private void setPosMap() {
		posMap = new int[board.length * board[0].length];
		for (int j = 0; j < posMap.length; j++)
			posMap[j] = j;

		/* This list is for board with missing blocks. */
		List<Integer> missing = new ArrayList<Integer>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == ' ') {
					missing.add(i * board[0].length + j);
				}
			}
		}
		System.out.println(missing);

		for (int i = 0; i < missing.size(); i++) {
			for (int j = missing.get(i) - i; j < posMap.length; j++) {
				posMap[j]++;
			}
		}
		System.out.println(Arrays.toString(posMap));
	}

	public void displayResults(int id) {
		displayTiles(id);
	}

	public void createAndShowGUI() {

		JFrame frame = new JFrame("Puzzle solver");
		Container contentPane = frame.getContentPane();
		contentPane.add(this);
		frame.setJMenuBar(mBar);

		frame.setLayout(new BorderLayout());
		frame.setSize(frameSize[0], frameSize[1]);
		frame.setLocation(framePos[0], framePos[1]);
		frame.setResizable(false);
		frame.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == miExit)
			System.exit(0);

		if (e.getSource() == miAbout) {
			JOptionPane.showMessageDialog(null, "Puzzle v1.0\n"
					+ "Date: 11/22/2014\n" + "Author: Dawei Fan, Deyuan Guo",
					"About", JOptionPane.INFORMATION_MESSAGE);
		}
		if (e.getSource() == miRead) {
			/* Read a ASCII file and solve it. */
			File file = null;
			if (fc.showOpenDialog(DisplayDancingLinks.this) == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				System.out.println(file.getAbsolutePath());
			}
			// not select any files
			else {
				JOptionPane.showConfirmDialog(null, "No file is selected!",
						"Warning", JOptionPane.CLOSED_OPTION,
						JOptionPane.WARNING_MESSAGE);
			}

			DataFileParser dfp = new DataFileParser(file.getAbsolutePath());
			/* Extract puzzle pieces, board are included in this list. */
			List<Tile> tileList = dfp.ExtractTiles();
			/* Get the board and the remained is tileList. */
			Tile board = tileList.get(0);
			tileList.remove(0);

			/* Dancing Links Tiling Puzzle Solver */
			DancingLinks dl = new DancingLinks(board, tileList);

			/* Clear previous blosks if there are any. */
			if (this.pDisplay.getComponentCount() > 0) {
				pDisplay.removeAll();
				pDisplay.repaint();
			}
			/* clear previous display, kind of reset. */
			cbExtra.setSelected(false);
			cbEnableSpin.setSelected(false);
			cbEnableSpinFlip.setSelected(false);
			tResultInfo.setText("Press button to solve");
			tIndex.setText("index");

			/* Set all parameters and display the board. */
			this.setupDancinglinks(dl);
			System.out.println("Extra? " + dl.isEnableExtra());
		}
	}
}
