package model;

import java.io.*;

public class Coordinates implements Serializable
{
	// Generated serial version UID
	private static final long serialVersionUID = -3743440865258865074L;
	private int x;
	private int y;
	
	public Coordinates(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}