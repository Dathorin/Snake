package model;

public class SnakeSegment extends Thing
{
	// Generated serial version UID
	private static final long serialVersionUID = -8772937988951908962L;
	private Coordinates previousPosition;
	
	public SnakeSegment(Coordinates pos)
	{
		super(pos);
		previousPosition = null;
	}
	
	public void setPosition(Coordinates newPos)
	{
		previousPosition = super.getPosition();
		super.setPosition(newPos);
	}
	
	public Coordinates getPreviousPosition()
	{
		return previousPosition;
	}
}