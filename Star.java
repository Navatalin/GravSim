
public class Star {

	private double X;
	private double Y;
	private String sClass;
	private double M;
	private double PM;
	
	
	public Star(double X, double Y, String sClass, double M)
	{
		this.X = X;
		this.Y = Y;
		this.sClass = sClass;
		this.M = M;
	}
	public String getStarClass()
	{
		return sClass;
	}
	public double getX()
	{
		return X;
	}
	public double getY()
	{
		return Y;
	}
	public String getSClass()
	{
		return sClass;
	}
	public double getMass()
	{
		return M;
	}
}
