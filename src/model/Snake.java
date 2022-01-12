package model;

import java.util.*;
import java.io.*;

public class Snake implements Serializable
{
	// Generated serial version UID
	private static final long serialVersionUID = 8915910873184881382L;
	private static final int BASE_SPEED = 1;
	private static final int SPRINT_SPEED = 3;
	private SnakeSegment head;
	private ArrayList<SnakeSegment> body;
	private Direction heading;
	private int speed;
	
	public Snake(Coordinates pos)
	{
		head = new SnakeSegment(pos);
		body = new ArrayList<SnakeSegment>();
		heading = Direction.RIGHT;
		speed = BASE_SPEED;
	}
	
	public ArrayList<SnakeSegment> getSnakeSegments()
	{
		// Defensive copy
		@SuppressWarnings("unchecked")
		ArrayList<SnakeSegment> snakeSegments = (ArrayList<SnakeSegment>)body.clone();
		snakeSegments.add(0, head);
		return snakeSegments;
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	public void sprint(boolean sprint)
	{
		if(sprint)
		{
			speed = SPRINT_SPEED;
		}
		else
		{
			speed = BASE_SPEED;
		}
	}
	
	public Coordinates getHeadPosition()
	{
		return head.getPosition();
	}
	
	public int getLength()
	{
		return body.size() + 1;
	}
	
	// This method is only set up to support single growth and will result in an exception if called multiple times in a row
	public SnakeSegment grow()
	{
		SnakeSegment grown = null;
		
		// If there isn't a tail yet
		if(body.isEmpty())
		{
			// Add the first tail segment in the last space occupied by the head
			grown = new SnakeSegment(head.getPreviousPosition());
			body.add(grown);
		}
		else
		{
			// Otherwise add the new tail segment in the last space occupied by the rearmost segment of the tail
			grown = new SnakeSegment(body.get(body.size() - 1).getPreviousPosition());
			body.add(grown);
		}
		
		return grown;
	}
	
	
	public void changeHeading(Direction turnTo)
	{
		// Enforce the constraint that the snake is only allowed to turn 90 degrees at a time
		boolean validTurn = false;
		switch(heading)
		{
			case UP:
			case DOWN:
			{
				if(turnTo == Direction.LEFT || turnTo == Direction.RIGHT)
				{
					validTurn = true;
				}
				break;
			}
			case LEFT:
			case RIGHT:
			{
				if(turnTo == Direction.UP || turnTo == Direction.DOWN)
				{
					validTurn = true;
				}
				break;
			}
		}
		
		if(validTurn)
		{
			heading = turnTo;
		}
	}
	
	public void move()
	{
		// Move head a step in the direction defined by heading
		Coordinates pos = head.getPosition();
		switch(heading)
		{
			case UP:
			{
				head.setPosition(new Coordinates(pos.getX(), pos.getY() - 1));
				break;
			}
			case DOWN:
			{
				head.setPosition(new Coordinates(pos.getX(), pos.getY() + 1));
				break;
			}
			case LEFT:
			{
				head.setPosition(new Coordinates(pos.getX() - 1, pos.getY()));
				break;
			}
			case RIGHT:
			{
				head.setPosition(new Coordinates(pos.getX() + 1, pos.getY()));
			}
		}
		
		if(!body.isEmpty())
		{
			// Move the first section of the tail to the head's previous position
			body.get(0).setPosition(head.getPreviousPosition());
		
			// For each section of the tail after the first
			for(int i = 1; i < body.size(); i++)
			{
				// Move the tail section into the previous position of the prior element
				body.get(i).setPosition(body.get(i - 1).getPreviousPosition());
			}
		}
	}
}