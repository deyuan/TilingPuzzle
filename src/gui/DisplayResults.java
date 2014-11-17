package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import puzzle.Puzzle;
import util.Tile;

/**
 * A class to display the result in a GUI. The DisplayResults instance has two panels, a control panel 
 * and a tiles display panel. The control panel includes two parts, configuration and result display.
 * Previously Control panel is programmed in a stand-alone class. 
 * 
 * @author David
 * @version 1.0
 * 			11/16/2014
 * 
 */
public class DisplayResults extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	/**
	 * A puzzle instance is included.
	 */
	private Puzzle puzzle;
	/**
	 * A positions list to store the result.
	 */
	private List<List<int[]>> positions;
	/**
	 * The number of possible solutions. 
	 */
	private int numOfSolution;
		
	/** 
	 * Control panel.
	 */
	private JPanel pControl;
		
	/**
	 * Display panel to show tiles.
	 */
	private JPanel pDisplay;
	
	
	/**
	 * Configure panel
	 */
	JPanel pConfig;
	JCheckBox cbEnableSpin;		
	JButton bSolve;
	/** 
	 * Result panel.
	 */
	JPanel pResult;
	JLabel tResultInfo;
	JTextField tIndex;
	JButton bShowResult;
	
	
	/**
	 * Size parameters.
	 */
	private static final int frameSize[] = {820, 540};
	private static final int framePos[] = {400, 20};
	private static final int displaySize[] = {600, 480};
	private static final int displayPos[] = {0, 0};
	private static final int gridWidth = 6;
	
	private int sizeBlock;
	private int sizeTile;
	/**
	 * The origin point from the left-top of the board to pDisplay panel.
	 */
	private int origin[] = {20, 20};
	private int originTile[] = {20+gridWidth/2, 20+gridWidth/2};
	
	/**
	 * @deprecated
	 * 
	 */
	public DisplayResults(char b[][], List<Tile> t, List<List<int[]>> p, boolean s){		
		super(null);	
	}	
	
	public DisplayResults(Puzzle p){
		
		super(null);
		this.puzzle = p;

		sizeBlock = Math.min((displaySize[0]-origin[0])/puzzle.getBoard().length, (displaySize[1]-origin[1])/puzzle.getBoard()[0].length)-gridWidth;
		sizeTile = sizeBlock + gridWidth;
		System.out.println("Size of block: "+ sizeBlock);
		
		this.setLocation(0, 0);
		this.setSize(frameSize[0]-20, frameSize[1]-20);
		this.setOpaque(true);
		this.setVisible(true);
		this.setFocusable(true);
		setupControlPanel();
		setupDisplay();
		setupBoard();		
	}	
	
	public void setupControlPanel(){


		
		pControl = new JPanel();
		pControl.setLocation(10, 10);
		pControl.setSize(120, 300);
		pControl.setOpaque(true);
		pControl.setVisible(true);
		pControl.setFocusable(true);
		pControl.setLayout(new BoxLayout(pControl, BoxLayout.Y_AXIS));
/*		
		this.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(""),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
  */                      
		
		/* Initialize control sub-panel. */
		pConfig = new JPanel();
		pConfig.setLocation(0, 0);
		pConfig.setSize(100, 100);
		pConfig.setOpaque(true);
		pConfig.setVisible(true);
		pConfig.setFocusable(true);
		pConfig.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Control"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
		pConfig.setLayout(new BoxLayout(pConfig, BoxLayout.Y_AXIS));
		
		cbEnableSpin = new JCheckBox("Enable Spin");
		cbEnableSpin.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		cbEnableSpin.setSelected(false);
		cbEnableSpin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(cbEnableSpin.isSelected()) puzzle.setEnableSpin(true);
				else puzzle.setEnableSpin(false);					
			}
			
		});
		pConfig.add(cbEnableSpin);
		
		bSolve = new JButton("Solve it!");
		bSolve.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				positions = puzzle.solve();
				numOfSolution = positions.size();
				if(numOfSolution == 0)
					tResultInfo.setText("No solutions!");
				else
					tResultInfo.setText(numOfSolution+" solutions!");
			}
			
		});
		pConfig.add(bSolve);		
		pControl.add(pConfig);
		
		/* Initialize result sub-panel. */
		pResult = new JPanel();
		pResult.setLocation(0, 100);
		pResult.setSize(100, 300);
		pResult.setOpaque(true);
		pResult.setVisible(true);
		pResult.setFocusable(true);
		pResult.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Result"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
		pResult.setLayout(new BoxLayout(pResult, BoxLayout.Y_AXIS));
		
		tResultInfo = new JLabel("        ");
		pResult.add(tResultInfo);
		
		tIndex = new JTextField("");
		tIndex.setSize(30, 15);
		tIndex.setSize(30, 10);
		tIndex.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		pResult.add(tIndex);
		
		bShowResult = new JButton("Show!");
//		bShowResult.setHorizontalAlignment(arg0);
		bShowResult.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int id = Integer.parseInt(tIndex.getText());
				if(id>=1&&id<=numOfSolution){
					if(puzzle.getEnableSpin()==true)
						displayTilesSpin(id-1);
					else
						displayTiles(id-1);
				}
					
				else{
					System.err.println("Out of Range!");
				}
			}
			
		});
		
		
		pResult.add(bShowResult);		
		pControl.add(pResult);
		
		this.add(pControl);
	}
	
	public void setupDisplay(){
		pDisplay = new JPanel();
		pDisplay.setLayout(null);
		pDisplay.setLocation(120, 0);
		pDisplay.setSize(displaySize[0], displaySize[1]);	
		pDisplay.setOpaque(true);
		pDisplay.setVisible(true);
		pDisplay.setFocusable(true);

		this.add(pDisplay);
	}
	
	
	public void setupBoard(){
		
		int w = puzzle.getBoard().length;
		int l = puzzle.getBoard()[0].length;
		System.out.println("Width: "+w+" Length: "+l);

		/* First is x, second is y. */
		int sizeGridH[] = {l*(sizeBlock+gridWidth)+gridWidth, gridWidth};
		int sizeGridV[] = {gridWidth, w*(sizeBlock+gridWidth)+gridWidth};
		JPanel gridH[] = new JPanel[w+1];
		JPanel gridV[] = new JPanel[l+1];
		
		for(int i = 0; i<=w; i++){
			gridH[i] = new JPanel();
			gridH[i].setBackground(Color.black);
			gridH[i].setSize(sizeGridH[0], sizeGridH[1]);
			gridH[i].setLocation(origin[0], origin[1]+(sizeBlock+gridWidth)*i);
			gridH[i].setOpaque(true);
			gridH[i].setVisible(true);
			pDisplay.add(gridH[i]);
		}
		
		for(int j = 0; j<=l; j++){
			gridV[j] = new JPanel();
			gridV[j].setBackground(Color.black);
			gridV[j].setSize(sizeGridV[0], sizeGridV[1]);
			gridV[j].setLocation(origin[0]+(sizeBlock+gridWidth)*j, origin[1]);
			gridV[j].setOpaque(true);
			gridV[j].setVisible(true);
			pDisplay.add(gridV[j]);		
		}			
	}
	
	private void displayTiles(int id){
		
		int number = puzzle.getTileList().size(); 
		Color color[] = {Color.cyan, Color.blue, Color.green, Color.red};
		List<int[]> pos = positions.get(id);
		
		if(id >= numOfSolution){
			System.err.println("Index of solutions out of range!");
			return;
		}
		
		for(int i = 0; i<number; i++){		
			for(int j = 0; j<puzzle.getTileList().get(i).data.length; j++){
				for(int k = 0; k<puzzle.getTileList().get(i).data[0].length; k++){
					if(puzzle.getTileList().get(i).data[j][k] != ' '){
						JPanel block = new JPanel();
						block.setBackground(color[i]);
						block.setSize(sizeTile, sizeTile);
						int x = originTile[0]+(pos.get(i)[1]+k)*sizeTile;
						int y = originTile[1]+(pos.get(i)[0]+j)*sizeTile;
						block.setLocation(x, y);
						block.setOpaque(true);
						block.setVisible(true);
						pDisplay.add(block);
					}
				}
			}
		}
	}
	
	private void displayTilesSpin(int id){
		
		int number = puzzle.getTileList().size(); 
		Color color[] = {Color.cyan, Color.blue, Color.green, Color.red};
		List<int[]> pos = positions.get(id);
		
		if(id >= numOfSolution){
			System.err.println("Index of solutions out of range!");
			return;
		}
				
		for(int i = 0; i<number; i++){
			
			for(int j = 0; j<puzzle.getTileList().get(i).pattern.get(pos.get(i)[2]).length; j++){
				for(int k = 0; k<puzzle.getTileList().get(i).pattern.get(pos.get(i)[2])[0].length; k++){
					if(puzzle.getTileList().get(i).pattern.get(pos.get(i)[2])[j][k] != ' '){
						JPanel block = new JPanel();
						block.setBackground(color[i]);
						block.setSize(sizeTile, sizeTile);
						int x = originTile[0]+(pos.get(i)[1]+k)*sizeTile;
						int y = originTile[1]+(pos.get(i)[0]+j)*sizeTile;
						block.setLocation(x, y);
						block.setOpaque(true);
						block.setVisible(true);
						pDisplay.add(block);
					}
				}
			}
		}
	}
	
	public void displayResults(int id){
		if(puzzle.getEnableSpin())
			displayTilesSpin(id);
		else
			displayTiles(id);
	}
	
	public void createAndShowGUI() {
		
		JFrame frame = new JFrame("Puzzle solver"); 
		Container contentPane = frame.getContentPane(); 
		contentPane.add(this);
		frame.setLayout(new BorderLayout());	
		frame.setSize(frameSize[0], frameSize[1]);
		frame.setLocation(framePos[0], framePos[1]);
		frame.setResizable(true); 
		frame.setVisible(true); 
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void repaint(){
		
	}
	
}
