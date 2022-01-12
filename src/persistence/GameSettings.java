package persistence;

import java.awt.event.*;
import java.io.*;

public class GameSettings implements Serializable
{
	// Generated serial version UID
	private static final long serialVersionUID = -8677258149999858718L;
	
	// Keybindings
	private int up;
	private int left;
	private int right;
	private int down;
	private int pause;
	private int sprint;

	// Game settings
	private int mouseLimit = 4;

	// Game data
	private int highScore = 0;

	public GameSettings()
	{
		// Attempt to load the config file
		GameSettings loaded = DataManager.loadGameSettings();

		// If no configuration exists
		if(loaded == null)
		{
			// Use default settings
			up = KeyEvent.VK_W;
			left = KeyEvent.VK_A;
			right = KeyEvent.VK_D;
			down = KeyEvent.VK_S;
			pause = KeyEvent.VK_SPACE;
			sprint = KeyEvent.VK_SHIFT;
			mouseLimit = 4;
			highScore = 0;

			// Save the current settings
			DataManager.saveGameSettings(this);
		}
		else
		{
			// Otherwise copy the loaded settings
			up = loaded.up;
			left = loaded.left;
			right = loaded.right;
			down = loaded.down;
			pause = loaded.pause;
			sprint = loaded.sprint;
			mouseLimit = loaded.mouseLimit;
			highScore = loaded.highScore;
		}
	}

	public int getHighScore()
	{
		return highScore;
	}

	public void setHighScore(int score)
	{
		highScore = score;
	}

	public int getMouseLimit()
	{
		return mouseLimit;
	}

	public void setMouseLimit(int lim)
	{
		mouseLimit = lim;
	}

	public int getUpKey()
	{
		return up;
	}

	public void setUpKey(int code)
	{
		up = code;
	}

	public int getLeftKey()
	{
		return left;
	}

	public void setLeftKey(int code)
	{
		left = code;
	}

	public int getDownKey()
	{
		return down;
	}

	public void setDownKey(int code)
	{
		down = code;
	}

	public int getRightKey()
	{
		return right;
	}

	public void setRightKey(int code)
	{
		right = code;
	}

	public int getPauseKey()
	{
		return pause;
	}

	public void setPauseKey(int code)
	{
		pause = code;
	}

	public int getSprintKey()
	{
		return sprint;
	}

	public void setSprintKey(int code)
	{
		sprint = code;
	}
}