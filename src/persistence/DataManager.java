package persistence;

import controller.*;
import java.io.*;
import java.util.*;

public class DataManager
{
	private static String saveDir = "saves";
	private static String dataDir = "data";
	private static String saveExt = ".ser";
	private static String dataExt = ".ser";
	private static String config = "config";

	private static String getSeparator()
	{
		return System.getProperty("file.separator");
	}

	private static String getPath()
	{
		return System.getProperty("user.dir");
	}

	public static void initDirectories()
	{

		// Create the saves directory if it does not exist already
		File dir = new File(getPath() + getSeparator() + saveDir);
		if(!dir.exists())
		{
			dir.mkdir();
		}

		// Create the data directory if it does not exist already
		dir = new File(getPath() + getSeparator() + dataDir);
		if(!dir.exists())
		{
			dir.mkdir();
		}
	}

	public static String[] getSaves()
	{
		ArrayList<String> saves = new ArrayList<String>();
		File dir = new File(getPath() + getSeparator() + saveDir);

		if(dir.exists())
		{
			File[] files = dir.listFiles();

			for(File f : files)
			{
				String name = f.getName();
				String ext = name.substring(name.lastIndexOf('.'));
				if(ext.equalsIgnoreCase(saveExt))
				{
					saves.add(name);
				}
			}
		}
		else
		{
			// If the saves directory doesn't exist then try to create it
			try
			{
				dir.createNewFile();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}

		if(saves.size() == 0)
		{
			return null;
		}
		else
		{
			return saves.toArray(new String[saves.size()]);
		}
	}

	public static boolean saveGame(String fileName, SnakeController gameState)
	{
		// Will be true unless an IOException occurs
		boolean saved = true;

		try
		{
			// Write game state to specified binary file (saves directory?)
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(getPath() + getSeparator() + saveDir + getSeparator() + fileName + saveExt)));
			out.writeObject(gameState);
			out.close();

		}
		catch(IOException ioe)
		{
			saved = false;
		}

		return saved;
	}

	public static SnakeController loadGame(String fileName)
	{
		SnakeController loaded = null;

		try
		{
			// Read game state from specified binary file
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(getPath() + getSeparator() + saveDir + getSeparator() + fileName)));
			loaded = (SnakeController) in.readObject();
			in.close();

			return loaded;
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			return loaded;
		}
		catch(ClassNotFoundException cnfe)
		{
			cnfe.printStackTrace();
			return loaded;
		}
	}

	public static GameSettings loadGameSettings()
	{
		GameSettings loaded = null;

		File configFile = new File(getPath() + getSeparator() + dataDir + getSeparator() + config + dataExt);
		if(configFile.exists())
		{
			try
			{
				// Load the game settings
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(configFile));
				loaded = (GameSettings) in.readObject();

				in.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				return loaded;
			}
			catch(ClassNotFoundException cnfe)
			{
				cnfe.printStackTrace();
				return loaded;
			}
		}

		return loaded;
	}

	public static void saveGameSettings(GameSettings toSave)
	{
		try
		{
			// Write the game settings to the config file
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(getPath() + getSeparator() + dataDir + getSeparator() + config + dataExt)));
			out.writeObject(toSave);

			out.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			return;
		}
	}
}