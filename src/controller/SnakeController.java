package controller;

import model.*;
import persistence.*;
import java.util.*;
import java.time.*;
import java.io.*;

public class SnakeController implements Runnable, Serializable
{
	// Generated serial version UID
	private static final long serialVersionUID = -5902958830750265135L;
	private static final int MOUSE_SPAWN_DELAY = 6;
	private transient GameSettings settings;
	private int mouseSpawn;
	private int gridWidth;
	private int gridHeight;
	private int startX;
	private int startY;
	private Grid grid;
	private Snake snake;
	private ArrayList<Mouse> mice;
	private boolean paused;
	private boolean gameOver;
	private boolean turned;
	private Direction bufferedTurn;
	private transient Thread control;
	private ArrayList<SnakeObserver> observers;

	public SnakeController(int gridWidth, int gridHeight, GameSettings set)
	{
		observers = new ArrayList<SnakeObserver>();
		paused = true;
		gameOver = false;
		turned = false;
		bufferedTurn = null;
		settings = set;

		// Initialize the grid
		grid = new Grid(gridWidth, gridHeight);
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;

		// Place the snake
		placeSnake();

		// Place a mouse
		mice = new ArrayList<Mouse>();
		placeMouse();

		// Set up the control thread
		control = new Thread(this);
		control.start();
	}

	public boolean isPaused()
	{
		return paused;
	}

	public Snake getSnake()
	{
		return snake;
	}

	public Grid getGrid()
	{
		return grid;
	}

	public boolean isGameOver()
	{
		return gameOver;
	}

	private void moveSnake()
	{
		// Move the snake a number of steps determined by its speed
		for(int i = 0; i < snake.getSpeed(); i++)
		{
			// Get new snake position
			snake.move();

			// Update the grid
			ArrayList<SnakeSegment> snakeSegments = snake.getSnakeSegments();
			SnakeSegment growth = null;

			for(SnakeSegment segment : snakeSegments)
			{
				try
				{
					Thing collided = grid.placeThing(segment);

					if(collided != null)
					{
						if(collided instanceof SnakeSegment)
						{
							// Game over because the snake collided with its own tail
							handleGameOver();
							break;
						}
						else if(collided instanceof Mouse)
						{
							// Increase the snake's length
							growth = snake.grow();

							// Remove the mouse
							mice.remove(collided);
						}
					}

					grid.clearPosition(segment.getPreviousPosition());
				}
				catch(OutOfBoundsException oobe)
				{
					// Game over because the snake went off the edge
					handleGameOver();
					return;
				}
			}

			if(growth != null)
			{
				try
				{
					grid.placeThing(growth);
				}
				catch(OutOfBoundsException oobe)
				{
					// This shouldn't happen, so print stack trace and exit
					oobe.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	public void moveMouseRandomly(Mouse toMove)
	{
		// Get valid moves
		ArrayList<Direction> moves = getValidMoves(toMove);

		if(moves.size() != 0)
		{
			Direction d;

			if(moves.size() == 1)
			{
				d = moves.get(0);
			}
			else
			{
				// Choose a direction
				int selection = (int) (Math.random() * moves.size());
				d = moves.get(selection);
			}

			Coordinates start = toMove.getPosition();
			Coordinates end = null;

			switch(d)
			{
				case UP:
					end = new Coordinates(start.getX(), start.getY() - 1);
					break;
				case LEFT:
					end = new Coordinates(start.getX() - 1, start.getY());
					break;
				case RIGHT:
					end = new Coordinates(start.getX() + 1, start.getY());
					break;
				case DOWN:
					end = new Coordinates(start.getX(), start.getY() + 1);
					break;
			}

			try
			{
				toMove.setPosition(end);
				grid.placeThing(toMove);
				grid.clearPosition(start);
			}
			catch(OutOfBoundsException oobe)
			{
				// This shouldn't happen, so print stack trace and exit
				oobe.printStackTrace();
				System.exit(1);
			}
		}
	}

	public ArrayList<Direction> getValidMoves(Thing toMove)
	{
		ArrayList<Direction> moves = new ArrayList<Direction>();
		Coordinates pos = toMove.getPosition();
		if(pos.getY() > 0)
		{
			// Check the cell above
			Thing t = grid.checkPosition(new Coordinates(pos.getX(), pos.getY() - 1));

			if(t == null)
			{
				// Up is a valid move
				moves.add(Direction.UP);
			}
		}
		if(pos.getY() < gridHeight - 1)
		{
			// Check the cell below
			Thing t = grid.checkPosition(new Coordinates(pos.getX(), pos.getY() + 1));

			if(t == null)
			{
				// Down is a valid move
				moves.add(Direction.DOWN);
			}
		}
		if(pos.getX() > 0)
		{
			// Check the cell to the left
			Thing t = grid.checkPosition(new Coordinates(pos.getX() - 1, pos.getY()));

			if(t == null)
			{
				// Left is a valid move
				moves.add(Direction.LEFT);
			}
		}
		if(pos.getX() < gridWidth - 1)
		{
			// Check the cell to the right
			Thing t = grid.checkPosition(new Coordinates(pos.getX() + 1, pos.getY()));

			if(t == null)
			{
				// Right is a valid move
				moves.add(Direction.RIGHT);
			}
		}

		return moves;
	}

	public boolean saveGame(String fileName)
	{
		// Make sure that the game is paused
		if(!paused)
		{
			paused = true;
		}

		return DataManager.saveGame(fileName, this);
	}

	public void loadGame(String fileName)
	{
		// Make sure that the game is paused
		if(!paused)
		{
			paused = true;
		}

		// Load the game state
		SnakeController loaded = DataManager.loadGame(fileName);

		if(loaded != null)
		{
			// Copy the loaded state into the running controller
			this.grid = loaded.grid;
			this.bufferedTurn = loaded.bufferedTurn;
			this.gameOver = loaded.gameOver;
			this.gridHeight = loaded.gridHeight;
			this.gridWidth = loaded.gridWidth;
			this.mice = loaded.mice;
			this.snake = loaded.snake;
			this.turned = loaded.turned;
			this.startX = loaded.startX;
			this.startY = loaded.startY;
			this.paused = true;

			// Update observers
			updateObservers();
		}
	}

	public void sprintSnake(boolean sprint)
	{
		snake.sprint(sprint);
	}

	private void placeSnake()
	{
		// Start the snake as close to the middle of the grid as possible (skew up and left if width and/or height are even)
		// 5/2 = 2 5%2 = 1 2 + 1 - 1 = 2
		// 4/2 = 2 4%2 = 0 2 - 1 = 1
		startX = ((gridWidth / 2) + (gridWidth % 2)) - 1;
		startY = ((gridHeight / 2) + (gridHeight % 2)) - 1;
		snake = new Snake(new Coordinates(startX, startY));
		ArrayList<SnakeSegment> segments = snake.getSnakeSegments();
		try
		{
			grid.placeThing(segments.get(0));
		}
		catch(OutOfBoundsException oobe)
		{
			// This shouldn't happen, so print stack trace and exit
			oobe.printStackTrace();
			System.exit(1);
		}
	}

	private void placeMouse()
	{
		int x = -1;
		int y = -1;
		Mouse newMouse = null;

		// Ensure that the mouse is placed at an empty location in the grid
		do
		{
			// Randomly select a position to place the mouse at
			// Using x * y to generate a number from 0 to (xy - 1)
			int rand = (int) (Math.random() * (gridHeight * gridWidth));
			x = rand % gridWidth;
			y = rand / gridHeight;
			newMouse = new Mouse(new Coordinates(x, y));
		}
		while(grid.checkPosition(new Coordinates(x, y)) != null);
		try
		{
			grid.placeThing(newMouse);
			mice.add(newMouse);
			mouseSpawn = 0;
		}
		catch(OutOfBoundsException oobe)
		{
			// This shouldn't happen, so print stack trace and exit
			oobe.printStackTrace();
			System.exit(1);
		}
	}

	private void handleGameOver()
	{
		// Stop snake from moving
		pauseGame();

		// Set the game over flag
		gameOver = true;

		// Update observers
		updateObservers();

	}

	public void reset()
	{
		// Clear the game over flag
		gameOver = false;

		// Clear the field
		grid.reset();

		// Create a new snake
		placeSnake();

		// Reset the mice
		mice.removeAll(mice);

		// Place a mouse
		placeMouse();
	}

	public void startGame()
	{
		// Resume the control thread
		paused = false;
	}

	public void pauseGame()
	{
		// Pause the control thread
		paused = true;
	}

	public void turnSnake(Direction d)
	{
		// Don't let the user turn while the game is paused
		if(!paused)
		{
			// Don't let the user turn twice in a given tick
			if(!turned)
			{
				snake.changeHeading(d);
				turned = true;
			}
			else
			{
				// Store the extra input for the next tick
				bufferedTurn = d;
			}
		}
	}

	public void addObserver(SnakeObserver so)
	{
		observers.add(so);
	}

	public void updateObservers()
	{
		for(SnakeObserver so : observers)
		{
			so.update();
		}
	}

	@Override
	public void run()
	{
		LocalDateTime currentTime;
		LocalDateTime previousTime = LocalDateTime.now().minusHours(1);

		// Tick every 175 milliseconds
		Duration delta = Duration.ofMillis(175);

		// Run until program exit
		while(true)
		{
			// Get the current time
			currentTime = LocalDateTime.now();

			// Don't tick if the game is paused if not enough time has passed
			if(!paused && currentTime.compareTo(previousTime.plus(delta)) >= 0)
			{
				// Set the time of this tick
				previousTime = currentTime;

				// If no mouse is on the board then place a mouse
				if(mice.size() == 0)
				{
					placeMouse();
				}
				else if(mice.size() < settings.getMouseLimit())
				{
					// Otherwise, place a new mouse after the delay period
					if(mouseSpawn++ > MOUSE_SPAWN_DELAY)
					{
						placeMouse();
					}
				}
				
				// Periodically move mice in random directions
				for(Mouse m: mice)
				{
					int rand = (int)(Math.random() * 10);
					if(rand < 3)
					{
						moveMouseRandomly(m);
					}
				}

				// If there is a buffered turn and the snake hasn't turned yet, then do that now
				if(!turned && bufferedTurn != null)
				{
					turnSnake(bufferedTurn);
					bufferedTurn = null;
				}

				// Move the snake
				moveSnake();
				turned = false;

				// Update observers
				updateObservers();
			}
		}
	}
}