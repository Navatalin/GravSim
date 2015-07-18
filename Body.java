
public class Body {
	private static final double G = 6.673e-11;
	private static final double solarmass = 1.98892e30;
	private static final double solarradii = 6.955e8;
	private static final double C = 2.998e8;
	public double rx, ry;
	public double vx, vy;
	public double fx, fy;
	public double mass;
	public String starClass;
	public double radius;
	
	public Body(double rx, double ry, double vx, double vy, double mass, String starClass)
	{
		this.rx = rx;
		this.ry = ry;
		this.vx = vx;
		this.vy = vy;
		this.mass = mass;
		this.starClass = starClass;
		calcRadius();
	}
	public void calcRadius()
	{
		double sMass = mass/solarmass;
		double sRadius = Math.pow(sMass,0.8);
		radius = sRadius * solarradii;
	}
	public String getStarClass()
	{
		return starClass;
	}
	public void update(double dt)
	{
			
		vx += dt * fx / mass;
		vy += dt * fy /mass;
		
		if(vx > C)
		{
			vx = C;
			System.out.println("Breaking Physics");
		}
			
		if(vy > C)
		{
			vy = C;
			System.out.println("Breaking Physics");
		}
			
		rx += dt * vx;
		ry += dt * vy;
	}
	
	public double distanceTo(Body b)
	{
		double dx = rx - b.rx;
		double dy = ry - b.ry;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public void resetForce()
	{
		fx = 0.0;
		fy = 0.0;
	}
	
	public void addForce(Body b)
	{
		Body a = this;
		double EPS = 3E4;
		double dx = b.rx - a.rx;
		double dy = b.ry - a.ry;
		double dist = Math.sqrt(dx*dx + dy*dy);
		double F = (G * a.mass * b.mass) / (dist * dist + EPS * EPS);
		a.fx += F * dx /dist;
		a.fy += F * dy /dist;
	}
	public boolean checkCollision(Body b)
	{
		boolean collision = false;
		calcRadius();
		
		if(distanceTo(b) < radius || distanceTo(b) < b.radius)
		{
			collision = true;
			//System.out.println("Collision: " + this.starClass + " with " + b.starClass);
		}
		
		
		return collision;
		
	}
	public void updateClass()
	{
		calcRadius();
	}
	
	
}
