package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dlx.DLX;
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
 * 			1.1 11/27/2014
 * 			1, Used new class DLX to replace DancingLinks.
 *			2, Initialized board outside DLX.
 */
public class DisplayDLX extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	/**
	 * DLX instance.
	 * 
	 */
	private DLX dlx;
		
	/**
	 * The solution got from DLX.solve. For every tile, the first element is the number of 
	 * the tile, others are positions.
	 * 
	 */
	private List<List<List<Integer>>> solution;
	
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
	JCheckBox cbRmSymm;
	JButton bSolveAll;
	JButton bSolveStep;
	JButton bSolveTrail;
	
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
	private static JMenuBar mBar;
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
	private static final int displayPos[] = { 200, 5 };
	private static final int gridWidth = 4;

	private int sizeBlock;
	private int sizeTile;
	/**
	 * The origin point from the left-top of the board to pDisplay panel.
	 */
	private int origin[] = { 20, 20 };
	private int originTile[] = { 20 + gridWidth / 2, 20 + gridWidth / 2 };

	private Calculate calculate = null;
	
	private class Calculate extends SwingWorker<List<List<List<Integer>>>, Void>{

		@Override
		protected List<List<List<Integer>>> doInBackground() {
			
			bSolveAll.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			/* Before every new process must reset DLX (DLX.config is also reset, DON'T reset enable options)*/
			dlx.Config.reset();
			dlx.preProcess();		
			cbExtra.setSelected(dlx.Config.isEnableExtra());			
			
			tResultInfo.setText("Calculating...");
			List<List<List<Integer>>> s = dlx.solve();
			return s;
		}
		@Override
		protected void done(){
			bSolveAll.setEnabled(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			try {
				solution = get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("The background task has been canceled!");
			}
			numOfSolution = solution.size();
													
			/* After geting the numofSolution, set the min and max of the slider. */
			slider.setMinimum(1);
			slider.setMaximum(numOfSolution);
			slider.setValue(1);
			
			if(numOfSolution == 0)
				tResultInfo.setText("No solutions!");
			else if(numOfSolution == 1)
				tResultInfo.setText("Only 1 solution!");
			else
				tResultInfo.setText(numOfSolution+" solutions!");
		}
		
	}
	
	public DisplayDLX() {
		super(null);
		setBackground(Color.WHITE);

		this.setLocation(0, 0);
		this.setSize(810, 520);
		this.setOpaque(true);
		this.setVisible(true);
		this.setFocusable(true);

		setupMenu();
		setupControlPanel();
		setupDisplay();
	}

	/**
	 * Setup DLX instance.
	 * 
	 * @param dls
	 */
	public void setDLX(DLX d) {
		dlx = d;
	}
	
	public void setupControlPanel() {

		pControl = new JPanel();
		pControl.setBackground(Color.WHITE);
		pControl.setLocation(5, 5);
		pControl.setSize(190, 460);
		pControl.setOpaque(true);
		pControl.setVisible(true);
		pControl.setFocusable(true);
		pControl.setLayout(null);
		/*
		 * this.setBorder(BorderFactory.createCompoundBorder(
		 * BorderFactory.createTitledBorder(""),
		 * BorderFactory.createEmptyBorder(5,5,5,5)));
		 */

		/* Initialize control sub-panel. */
		pConfig = new JPanel();
		pConfig.setBackground(Color.WHITE);
		pConfig.setLocation(5, 5);
		pConfig.setSize(180, 270);
		pConfig.setOpaque(true);
		pConfig.setVisible(true);
		pConfig.setFocusable(true);
		pConfig.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Control"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		pConfig.setLayout(null);

		cbEnableSpin = new JCheckBox("Enable spin");
		cbEnableSpin.setBackground(Color.WHITE);
		cbEnableSpin.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		cbEnableSpin.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cbEnableSpin.setSelected(false);
		cbEnableSpin.setSize(160, 30);
		cbEnableSpin.setLocation(10, 20);
		cbEnableSpin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (cbEnableSpin.isSelected())
					dlx.Config.setEnableSpin(true);
				else
					dlx.Config.setEnableSpin(false);
			}

		});
		pConfig.add(cbEnableSpin);

		cbEnableSpinFlip = new JCheckBox("Enable spin + flip");
		cbEnableSpinFlip.setBackground(Color.WHITE);
		cbEnableSpinFlip.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		cbEnableSpinFlip.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cbEnableSpinFlip.setSelected(false);
		cbEnableSpinFlip.setSize(160, 30);
		cbEnableSpinFlip.setLocation(10, 50);
		cbEnableSpinFlip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (cbEnableSpinFlip.isSelected())
					dlx.Config.setEnableSpinFlip(true);
				else
					dlx.Config.setEnableSpinFlip(false);
			}

		});
		pConfig.add(cbEnableSpinFlip);

		cbExtra = new JCheckBox("Extra blocks");
		cbExtra.setBackground(Color.WHITE);
		cbExtra.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		cbExtra.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cbExtra.setSelected(false);
		cbExtra.setSize(160, 30);
		cbExtra.setLocation(10, 80);
		cbExtra.setEnabled(false);
		pConfig.add(cbExtra);

		
		cbRmSymm = new JCheckBox("Remove symmetry");
		cbRmSymm.setBackground(Color.WHITE);
		cbRmSymm.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		cbRmSymm.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cbRmSymm.setSelected(false);
		cbRmSymm.setSize(160, 30);
		cbRmSymm.setLocation(10, 110);
		cbRmSymm.setEnabled(true);
		pConfig.add(cbRmSymm);
				
		
		bSolveAll = new JButton("Get all solutions");
		bSolveAll.setBackground(Color.WHITE);
		bSolveAll.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		bSolveAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				calculate = new Calculate();
				calculate.execute();
			}

		});
		bSolveAll.setSize(160, 30);
		bSolveAll.setLocation(10, 150);
		pConfig.add(bSolveAll);

		
		bSolveStep = new JButton("Solve & display steps");
		bSolveStep.setBackground(Color.WHITE);
		bSolveStep.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		bSolveStep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				calculate = new Calculate();
				calculate.execute();
			}

		});
		bSolveStep.setSize(160, 30);
		bSolveStep.setLocation(10, 190);
		pConfig.add(bSolveStep);
		
		bSolveTrail = new JButton("Solve & display trails");
		bSolveTrail.setBackground(Color.WHITE);
		bSolveTrail.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		bSolveTrail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				calculate = new Calculate();
				calculate.execute();
			}

		});
		bSolveTrail.setSize(160, 30);
		bSolveTrail.setLocation(10, 230);
		pConfig.add(bSolveTrail);	
		
		pControl.add(pConfig);
		
		
		
		
		
		
		

		/* Initialize result sub-panel. */
		pResult = new JPanel();
		pResult.setBackground(Color.WHITE);
		pResult.setSize(180, 180);
		pResult.setLocation(5, 280);
		pResult.setOpaque(true);
		pResult.setVisible(true);
		pResult.setFocusable(true);
		pResult.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Result"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pResult.setLayout(null);

		tResultInfo = new JLabel("Press button to solve");
		tResultInfo.setBackground(Color.WHITE);
		tResultInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		// tResultInfo.setPreferredSize(new Dimension(60, 30));
		tResultInfo.setSize(new Dimension(160, 30));
		tResultInfo.setLocation(10, 20);
		tResultInfo.setVisible(true);
		pResult.add(tResultInfo);

		tIndex = new JTextField(" index", 10);
		tIndex.setBackground(Color.WHITE);
		tIndex.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		// tIndex.setPreferredSize(new Dimension(80, 25));
		tIndex.setSize(new Dimension(80, 30));
		// tIndex.setMaximumSize(new Dimension(80, 25));
		tIndex.setVisible(true);
		tIndex.setLocation(10, 50);
		pResult.add(tIndex);

		bShowResult = new JButton("Show!");
		bShowResult.setBackground(Color.WHITE);
		bShowResult.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		bShowResult.setSize(80, 30);
		bShowResult.setLocation(90, 50);
		bShowResult.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int id = Integer.parseInt(tIndex.getText());
				if (id >= 1 && id <= numOfSolution) {
					cleanTiles();
					displayResults(id - 1);

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
		bPre.setBackground(Color.WHITE);
		bPre.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		bPre.setSize(80, 30);
		bPre.setLocation(10, 80);
		bPre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int cur = Integer.parseInt(tIndex.getText());
				if (cur > 1) {
					cur--;
					tIndex.setText(Integer.toString(cur));
					cleanTiles();
					displayResults(cur - 1);
					/* Set slider. */
					slider.setValue(cur);
				}
			}

		});
		pResult.add(bPre);

		bNext = new JButton("Next");
		bNext.setBackground(Color.WHITE);
		bNext.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		bNext.setSize(80, 30);
		bNext.setLocation(90, 80);
		bNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int cur = Integer.parseInt(tIndex.getText());
				if (cur < numOfSolution) {
					cur++;
					tIndex.setText(Integer.toString(cur));
					cleanTiles();
					displayResults(cur - 1);
					/* Set slider. */
					slider.setValue(cur);
				}

			}

		});
		pResult.add(bNext);

		slider = new JSlider();
		slider.setBackground(Color.WHITE);
		slider.setSize(160, 30);
		slider.setLocation(10, 110);
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
		bPlay.setBackground(Color.WHITE);
		bPlay.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		bPlay.setSize(160, 30);
		bPlay.setLocation(10, 140);
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
										displayResults(i);
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
		pDisplay.setBackground(Color.WHITE);
		pDisplay.setLayout(null);
		pDisplay.setLocation(displayPos[0], displayPos[1]);
		pDisplay.setSize(displaySize[0], displaySize[1]);
		pDisplay.setOpaque(true);
		pDisplay.setVisible(true);
		pDisplay.setFocusable(true);

		this.add(pDisplay);
	}

	public void setupMenu() {
		mBar = new JMenuBar();
		mBar.setBackground(Color.WHITE);
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
	
	/**
	 * Initiate the board from input and draw the board.
	 */
	public void setupBoard(char[][] b) {

		/* Initialize board array. */
		board = new char[b.length][b[0].length];
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				board[i][j] = b[i][j];
			}
		}
		
		/*
		 * Initialize board related size.
		 * There should be some redundancy for the sizeblock, or the edges cannot
		 * be drawn.
		 */
		sizeBlock = Math.min(
				(displaySize[0] - origin[0] - 5) / board[0].length,
				(displaySize[1] - origin[1] - 5) / board.length)
				- gridWidth;
		sizeTile = sizeBlock + gridWidth;
		System.out.println("Size of block: " + sizeBlock);
		
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

//		System.out.println(set);
	}

	public void displayResults(int id) {

		List<List<Integer>> pos = solution.get(id);
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
	
	/**
	 * This is a map between the assigned number of the board in DLX and the real position
	 * for drawing when there are holes in the board.
	 * If there are no holes this function does nothing
	 */
	private void setPosMap() {
		posMap = new int[board.length * board[0].length];
		for (int j = 0; j < posMap.length; j++)
			posMap[j] = j;

		/* This list is for board with missing blocks. */
		List<Integer> missing = new ArrayList<Integer>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == ' ') 
					missing.add(i * board[0].length + j);
			}
		}

		for (int i = 0; i < missing.size(); i++) {
			for (int j = missing.get(i) - i; j < posMap.length; j++) 
				posMap[j]++;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == miExit)
			System.exit(0);

		if (e.getSource() == miAbout) {
			JOptionPane.showMessageDialog(null, "Tiling Puzzle v1.0\n"
					+ "Date: 11/24/2014\n" + "Designed by Dawei Fan and Deyuan Guo",
					"About", JOptionPane.INFORMATION_MESSAGE);
		}
		if (e.getSource() == miRead) {
			/* Read a ASCII file and solve it. */
			File file = null;
			if (fc.showOpenDialog(DisplayDLX.this) == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				System.out.println(file.getAbsolutePath());
			}
			// not select any files
			else {
				JOptionPane.showConfirmDialog(null, "No file is selected!",
						"Warning", JOptionPane.CLOSED_OPTION,
						JOptionPane.WARNING_MESSAGE);
			}

			/* Delete all panels in the pDisplay if there are. */
			pDisplay.removeAll();			
			/* Reset configuration . */
			cbEnableSpin.setSelected(false);
			cbEnableSpinFlip.setSelected(false);
			
			DataFileParser dfp = new DataFileParser(file.getAbsolutePath());
			/* Extract puzzle pieces, board are included in this list. */
			List<Tile> tileList = dfp.ExtractTiles();
			/* Get the board and the remained is tileList. */
			Tile board = tileList.get(0);
			tileList.remove(0);

			/* Initiate a new DLX Solver and set it. */
			DLX dlx = new DLX(board, tileList);
			setDLX(dlx);
			
			/* Initialize the board and posMap. */ 
			setupBoard(board.data);
			setPosMap();
			repaint();

		}
	}
		
	public static void createAndShowGUI() {

		JFrame frame = new JFrame("Puzzle solver");
		frame.setContentPane(new DisplayDLX());		
		frame.setJMenuBar(mBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(frameSize[0], frameSize[1]);
		frame.setLocation(framePos[0], framePos[1]);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		/*
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				createAndShowGUI();
			}
			
		});

	}

}
