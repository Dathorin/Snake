package model;

import java.io.*;

public abstract class Thing implements Serializable
{
	// Generated serial version UID
	private static final long serialVersionUID = -6562816407804880226L;
	private Coordinates position;
	
	public Thing(Coordinates position)
	{
		this.position = position;
	}
	
	public Coordinates getPosition()
	{
		return position;
	}
	
	public void setPosition(Coordinates pos)
	{
		position = pos;
	}
}