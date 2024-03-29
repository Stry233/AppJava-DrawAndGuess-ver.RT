package edu.rpi.cs.csci4963.u22.cheny63.project.drawAndGuess.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.rpi.cs.csci4963.u22.cheny63.project.drawAndGuess.control.Controller;
import edu.rpi.cs.csci4963.u22.cheny63.project.drawAndGuess.tools.ImageUtility;
import edu.rpi.cs.csci4963.u22.cheny63.project.drawAndGuess.tools.SystemCheck;

/** 
 *  OVERVIEW: 
 * 	<b>GameSessionFrame</b> is an frame showing the current game state when
 *  a session is started
 *
 * @author Yuetian Chen
 * @version <b>1.0</b> rev. 0
 */
public class GameSessionFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private Controller controller;
	
	// chessboard info
	private DrawBoard board;
	private Color[][] drawContent = new Color[80][80];
	
	// panels
	private OpaqueJPanel operations;           // store drawboard operations
	private OpaqueJPanel chatRoom;             // store chat
	protected VerticalTimerPanel timer;        // store time
	private java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
	private ChatBoxPanel chat;
	private DrawBoardButton exit;
	
	// special param
	private double scale = SystemCheck.isWindows() ? 0.06 : 0.06;
	
	/**
	 * helper function for updateing the current chat history info
	 */
	public void updateStats() {
		this.chat.updateChat();
	}
	
	/**
	 * helper function: triggered when there is a time sync needed
	 * @param timeInterval the current time to be synced
	 */
	public void timerStart(int timeInterval) {
		this.timer.updateTime(timeInterval);
	}
	
	/**
	 * helper function: set the prompting at left corner of the drawboard
	 * @param secretWord the first real answer of the word
	 * @param secretHint the second category of the word
	 */
	public void setPrompt(String secretWord, String secretHint) {
		this.board.setPrompt(secretWord, secretHint);
		this.chat.updateCurrentGuessing();
	}
	
	/**
	 * helper function for activating the drawing function of the drawboard
	 */
	public void activate() {
		this.board.activate();
		for (Component button: operations.getComponents())
			button.setEnabled(true);
	}
	
	/**
	 * helper function for deactivating the drawing function of the drawboard
	 */
	public void deactivate() {
		this.board.deactivate();
		for (Component button: operations.getComponents())
			button.setEnabled(false);
	}
	
	/**
	 * helper function for clearing the chessboard operation is set from remote
	 */
	public void clear() {
		this.board.clear(false);
	}
	
	/**
	 * helper function for set the specific entry color of the drawboard
	 * @param x the x position of the entry
	 * @param y the y position of the entry
	 * @param targetColor the color to be assigned at (x, y)
	 */
	public void setEntryColor(int x, int y, Color targetColor) {
		this.board.setEntryColor(x, y, targetColor, false);
	}
	
	/**
	 * helper function for customize cursor color change when enter this frame and interacting
	 */
	private void initCursorStrategy() {
		java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
		Image image = ImageUtility.resizeIcon(toolkit.getImage("./res/gui/cursor/normal.png"), new Dimension(10, 10));
		Cursor newCursor = toolkit.createCustomCursor(image , new Point(0, 0), "");
		this.setCursor (newCursor);
	}
	
	/**
	 * helper function used for constructor to init all operations pattern and button in this frame for
	 * the draw board	
	 * @param controller the controller to be assigned in further operation
	 */
	private void initOperations(Controller controller) {
		java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
		this.operations.setLayout(new GridBagLayout());
		GridBagConstraints gridBagCons = new GridBagConstraints();
		this.operations.setOpaque(true);
		int buttonWidth = (int)(toolkit.getScreenSize().width * scale);
		DrawBoardButton pencil = new DrawBoardButton(new ImageIcon(ImageUtility.resizeIcon(toolkit.getImage("./res/gui/gameSession/pencil.png"), 
								 new Dimension(buttonWidth, buttonWidth))), true);
		DrawBoardButton eraser = new DrawBoardButton(new ImageIcon(ImageUtility.resizeIcon(toolkit.getImage("./res/gui/gameSession/eraser.png"), 
				 				 new Dimension(buttonWidth, buttonWidth))), true);
		DrawBoardButton restore = new DrawBoardButton(new ImageIcon(ImageUtility.resizeIcon(toolkit.getImage("./res/gui/gameSession/restore.png"), 
				 				 new Dimension(buttonWidth, buttonWidth))), true);
		this.exit = new DrawBoardButton(new ImageIcon(ImageUtility.resizeIcon(toolkit.getImage("./res/gui/gameSession/exit.png"), 
 								 new Dimension((int)(toolkit.getScreenSize().width * scale/1741*1321), (int)(toolkit.getScreenSize().width *scale)))), true);
		
		// set action to button
		exit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(GameSessionFrame.this, "Do you want to quit?", 
				    "Are you sure", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) 
					controller.onClose();
			}
        });
		pencil.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				GameSessionFrame.this.board.setStroke(Color.BLACK);
			}
        });
		eraser.addActionListener(new java.awt.event.ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				GameSessionFrame.this.board.setStroke(new Color(251, 251, 251));
			}
        });
		restore.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (JOptionPane.showConfirmDialog(GameSessionFrame.this, "Do you want to clear the board?", 
    				    "Are you sure", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) 
            			GameSessionFrame.this.board.clear(true);
			}
        });
		
		gridBagCons.gridx = 1;
		this.operations.add(pencil);
		gridBagCons.gridx = 2;
		this.operations.add(eraser);
		gridBagCons.gridx = 3;
		this.operations.add(restore);
		gridBagCons.gridx = 4;
		gridBagCons.anchor = GridBagConstraints.EAST;
		gridBagCons.weightx = 10;
		// deactivate all
		for (Component button: operations.getComponents())
			button.setEnabled(false);		
	}
	
	/**
	 * helper function triggered by constructor for arranging all GUI in this frame
	 * @param controller the controller to be assigned in further operation
	 * @throws FontFormatException when system is unable to find the correct format of font 
	 * @throws IOException when system is unable to find font
	 */
	private void generateGUI(Controller controller) throws FontFormatException, IOException {
		// set full screen
		GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = graphics.getDefaultScreenDevice();
        initCursorStrategy();
        
        // init panel
        this.operations = new OpaqueJPanel();                   // store drawboard operations
        OpaqueJPanel allOperations = new OpaqueJPanel();        // store drawboard operations
        this.chatRoom = new OpaqueJPanel();                     // store chat
        this.timer = new VerticalTimerPanel();                        // store time
        OpaqueJPanel chessboardAndTools = new OpaqueJPanel();   // store drawboard operations
		this.chat = new ChatBoxPanel(this.controller, this);
        initOperations(controller);
        this.board = new DrawBoard(drawContent, -1, controller);
        allOperations.add(operations);
        allOperations.add(exit);
        
        // start arrange
     	this.setBackground(new Color(32, 130, 147));
     	GameSessionPanel allContent = new GameSessionPanel();
     	allContent.setLayout(new BorderLayout());
     	chessboardAndTools.setLayout(new GridBagLayout());
     	GridBagConstraints gridBagCons = new GridBagConstraints();
     	
     	this.chatRoom.add(chat);
     	gridBagCons.gridx = 0;
     	gridBagCons.gridy = 1;
     	chessboardAndTools.add(board, gridBagCons);
     	gridBagCons.gridy = 0;
     	gridBagCons.anchor = GridBagConstraints.WEST;
     	chessboardAndTools.add(allOperations, gridBagCons);
     	chessboardAndTools.setPreferredSize(new Dimension((int)(toolkit.getScreenSize().width * 0.6), 
     			                                          (int)(toolkit.getScreenSize().width * 0.6)));
     	// this.timer.setBounds(0, 0, 50, 1000);
     	// this.timer.add(chessboardAndTools);
     	allContent.add(this.timer, BorderLayout.WEST);
     	allContent.add(chessboardAndTools, BorderLayout.CENTER);
     	allContent.add(this.chatRoom, BorderLayout.EAST);
     	
     	this.add(allContent);

        //Display the window.       
		this.setLocationRelativeTo(null); // set window centre
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.setPreferredSize(new Dimension(600, 600));
	    this.setResizable(false);
	    if (SystemCheck.isWindows())
	    	device.setFullScreenWindow(this);
	    else
	    	this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    
	    this.setVisible(true);
	}

	/**
	 * Constructor for init the game session frame with every variable ready
	 * @param controller the controller to be assigned in further operation
	 */
	public GameSessionFrame(Controller controller) {
		super("Start a game - Draw and Guess");
		this.controller = controller;
		try {
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			generateGUI(controller);
		} catch (Exception e) { // case: cannot get resource
			JOptionPane.showMessageDialog(this, 
			"Fail to load game resource: please check res folder", 
			"Oops...", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		
	}
}