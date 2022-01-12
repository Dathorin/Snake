package view;

import controller.*;
import model.*;
import persistence.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SnakeGUI extends JFrame implements SnakeObserver
{
	// Generated serial version UID
	private static final long serialVersionUID = -5201514648181367561L;
	private SnakeController control;
	private GameSettings settings;
	boolean rebinding;
	boolean settingsSaved;

	// Components
	private JPanel pnlGraphics;
	private JLabel lblScore;
	private JLabel lblHighScore;
	private JTextField tfScore;
	private JTextField tfHighScore;
	private JButton btnSave;
	private JButton btnLoad;
	private JButton btnOptions;
	private JPanel pnlScore;
	private JPanel pnlButtons;

	private int gridHeight = 40;
	private int gridWidth = 40;

	public SnakeGUI()
	{
		super("Snake");

		// Load the game settings
		settings = new GameSettings();
		
		// Instantiate the controller
		control = new SnakeController(gridWidth, gridHeight, settings);
		rebinding = false;

		// Register the view as an observer
		control.addObserver(this);

		// Register a key event dispatcher to process user input
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent(KeyEvent e)
			{
				if(e.getID() == KeyEvent.KEY_PRESSED)
				{
					if(!rebinding)
					{
						handleGameKeyPress(e);
						e.consume();
						return true;
					}
					else
					{
						return false;
					}
				}
				else if(e.getID() == KeyEvent.KEY_RELEASED)
				{
					if(!rebinding)
					{
						if(handleGameKeyRelease(e))
						{
							e.consume();
							return true;
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}

				}
				else
				{
					return false;
				}
			}
		});

		this.setLayout(new GridBagLayout());

		// Set up the components
		pnlGraphics = new JPanel()
		{
			private static final long serialVersionUID = -8527118029202715591L;

			protected void paintComponent(Graphics g)
			{
				int cellWidth = 15;
				int cellHeight = 15;

				if(control.getGrid() == null)
				{
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, (int) this.getSize().getWidth(), (int) this.getSize().getHeight());
				}
				else
				{
					Thing[][] grid = control.getGrid().getArray();
					for(int i = 0; i < grid.length; i++)
					{
						for(int j = 0; j < grid[0].length; j++)
						{
							if(grid[i][j] instanceof Mouse)
							{
								g.setColor(Color.GRAY);
							}
							else if(grid[i][j] instanceof SnakeSegment)
							{
								g.setColor(Color.GREEN);
							}
							else
							{
								g.setColor(Color.BLACK);
							}
							g.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);

							if(grid[i][j] != null)
							{
								g.setColor(Color.BLACK);
								g.drawRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
							}
						}
					}
				}
			}
		};
		pnlGraphics.setPreferredSize(new Dimension(600, 600));

		pnlScore = new JPanel(new GridLayout(2, 2));
		pnlScore.setPreferredSize(new Dimension(150, 40));

		lblHighScore = new JLabel("High Score");
		lblHighScore.setPreferredSize(new Dimension(75, 20));

		tfHighScore = new JTextField();
		tfHighScore.setEditable(false);
		tfHighScore.setPreferredSize(new Dimension(60, 20));
		tfHighScore.setText(settings.getHighScore() + "");

		lblScore = new JLabel("Score");
		lblScore.setPreferredSize(new Dimension(75, 20));

		tfScore = new JTextField();
		tfScore.setEditable(false);
		tfScore.setPreferredSize(new Dimension(80, 20));

		pnlScore.add(lblHighScore);
		pnlScore.add(lblScore);
		pnlScore.add(tfHighScore);
		pnlScore.add(tfScore);

		pnlButtons = new JPanel(new GridLayout());
		pnlButtons.setPreferredSize(new Dimension(300, 20));

		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Pause the game
				control.pauseGame();

				// Get the file name to use
				String fileName = JOptionPane.showInputDialog(rootPane, "Please enter the name for your save file", "Save game", JOptionPane.QUESTION_MESSAGE);

				// Don't allow the user to enter a file extension
				if(fileName.indexOf('.') != -1)
				{
					fileName = fileName.substring(0, fileName.indexOf('.'));
				}

				// Save the game state
				control.saveGame(fileName);
			}
		});
		btnSave.setPreferredSize(new Dimension(100, 20));

		btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Pause the game
				control.pauseGame();

				// Load game
				loadGame();
			}
		});
		btnLoad.setPreferredSize(new Dimension(100, 20));

		btnOptions = new JButton("Options");
		btnOptions.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{				
				// Open the options frame
				OptionsFrame of = new OptionsFrame(settings);
				of.addWindowListener(new WindowListener()
				{
					@Override
					public void windowOpened(WindowEvent e)
					{
						// Freeze snake GUI
						btnSave.setEnabled(false);
						btnLoad.setEnabled(false);
						btnOptions.setEnabled(false);
						rebinding = true;
						settingsSaved = false;
						control.pauseGame();
					}

					@Override
					public void windowClosing(WindowEvent e)
					{
						// Enable snake GUI
						btnSave.setEnabled(true);
						btnLoad.setEnabled(true);
						btnOptions.setEnabled(true);
						rebinding = false;
						
						// Save the game settings
						if(!settingsSaved)
						{
							DataManager.saveGameSettings(settings);
							settingsSaved = true;
						}
						
					}

					@Override
					public void windowClosed(WindowEvent e)
					{
						// Enable snake GUI
						btnSave.setEnabled(true);
						btnLoad.setEnabled(true);
						rebinding = false;
						
						// Save the game settings
						if(!settingsSaved)
						{
							DataManager.saveGameSettings(settings);
							settingsSaved = true;
						}
					}

					@Override
					public void windowIconified(WindowEvent e)
					{
						// Method unused
					}

					@Override
					public void windowDeiconified(WindowEvent e)
					{
						// Method unused
					}

					@Override
					public void windowActivated(WindowEvent e)
					{
						// Method unused
					}

					@Override
					public void windowDeactivated(WindowEvent e)
					{
						// Method unused
					}
				});
			}
		});
		btnOptions.setPreferredSize(new Dimension(100, 20));

		pnlButtons.add(btnSave);
		pnlButtons.add(btnLoad);
		pnlButtons.add(btnOptions);

		// Manage constraints and add the components
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 6;
		c.gridheight = 5;
		add(pnlGraphics, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		add(pnlButtons, c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 5;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.EAST;
		add(pnlScore, c);

		// Set frame options
		setSize(620, 680);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Display the initial game state
		update();
	}

	public static void main(String[] args)
	{
		DataManager.initDirectories();
		SnakeGUI sg = new SnakeGUI();
		sg.setVisible(true);
	}

	private void handleGameKeyPress(KeyEvent e)
	{
		// Get the pressed key
		int pressed = e.getKeyCode();

		// Compare the key bindings for actions
		if(pressed == settings.getSprintKey())
		{
			control.sprintSnake(true);
		}
		else if(pressed == settings.getUpKey())
		{
			control.turnSnake(Direction.UP);
		}
		else if(pressed == settings.getLeftKey())
		{
			control.turnSnake(Direction.LEFT);
		}
		else if(pressed == settings.getRightKey())
		{
			control.turnSnake(Direction.RIGHT);
		}
		else if(pressed == settings.getDownKey())
		{
			control.turnSnake(Direction.DOWN);
		}
		else if(pressed == settings.getPauseKey())
		{
			if(control.isPaused())
			{
				control.startGame();
			}
			else
			{
				control.pauseGame();
			}
		}
	}

	private boolean handleGameKeyRelease(KeyEvent e)
	{
		// Check for sprint key release
		int keyCode = e.getKeyCode();

		if(keyCode == settings.getSprintKey())
		{
			control.sprintSnake(false);
			return true;
		}

		return false;
	}

	private boolean loadGame()
	{
		boolean load = false;

		// Ask the user which save to load
		String[] saves = DataManager.getSaves();
		int selection = JOptionPane.showOptionDialog(rootPane, "Please select the save file to load", "Load game", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, saves, saves[0]);

		// Don't try to load if the user cancelled
		if(selection != -1)
		{
			String fileName = saves[selection];
			control.loadGame(fileName);
			load = true;
		}

		return load;
	}

	public void update()
	{
		pnlGraphics.repaint();
		int score = control.getSnake().getLength();
		tfScore.setText(score + "");

		// Check for game over
		if(control.isGameOver())
		{
			// Check for new high score
			int highScore = Integer.parseInt(tfHighScore.getText());

			String message = "";
			if(score > highScore)
			{
				message = "New high score!\nYour final score was: " + score + "\nPlay again?";

				// Update the high score
				tfHighScore.setText(score + "");
				settings.setHighScore(score);
				DataManager.saveGameSettings(settings);
			}
			else
			{
				message = "Your final score was: " + score + "\nPlay again?";
			}

			// Inform user that game is over and ask whether or not to play again
			int cont = JOptionPane.showOptionDialog(rootPane, message, "Game over!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "New game", "Load game", "Exit" },
					null);

			if(cont == 0)
			{
				// Reset and start again
				control.reset();
			}
			else if(cont == 1)
			{
				// Handle game load
				if(!loadGame())
				{
					// Exit
					System.exit(0);
				}
			}
			else
			{
				// Exit
				System.exit(0);
			}
		}
	}
}