package model;

import java.io.*;

public class Grid implements Serializable
{
	// Generated serial version UID
	private static final long serialVersionUID = -1164614277305508816L;
	private Thing[][] grid;
	
	public Grid(int x, int y)
	{
		grid = new Thing[y][x];
	}
	
	public Thing[][] getArray()
	{
		// Defensive copy
		return grid.clone();
	}
	
	public Thing placeThing(Thing toPlace) throws OutOfBoundsException
	{
		Thing prev = null;
		
		// Check for out-of-bounds coordinates
		Coordinates coords = toPlace.getPosition();
		int x = coords.getX();
		int y = coords.getY();
		if(x < 0 || x >= grid[0].length || y < 0 || y >= grid.length)
		{
			throw new OutOfBoundsException();
		}
		
		prev = grid[y][x];
		grid[y][x] = toPlace;
		
		return prev;
	}
	
	// Clear the grid
	public void reset()
	{
		grid = new Thing[grid.length][grid[0].length];
	}
	
	public void clearPosition(Coordinates coords)
	{
		grid[coords.getY()][coords.getX()] = null;
	}
	
	public Thing checkPosition(Coordinates coords)
	{
		return grid[coords.getY()][coords.getX()];
	}
}