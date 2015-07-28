import java.util.concurrent.*;

public class Compute implements Callable
{
    private Body[] bodies;
    private int start;
    private int end;
    
    public Compute(Body[] bodies,int start, int end)
    {
        this.bodies = bodies;
        this.start = start;
        this.end =end;
    }
    public Body[] call()
    {
        addforces(start,end);
        return bodies;
    }
    private void addforces(int start, int end)
	{
	    int n = end;
	    
		for(int i = start; i < n; i++)
		{
			if(bodies[i].mass > 0)
			{
				bodies[i].resetForce();
				for(int j = 0; j < bodies.length; j++)
				{
					if ( i != j && bodies[j].mass > 0)
					{
						bodies[i].addForce(bodies[j]);
					
					  
						if(bodies[i].checkCollision(bodies[j]))
						{
							//System.out.println("Collision");
							//collisionCount++;
							if(bodies[i].mass > bodies[j].mass)
							{
								bodies[i].mass += bodies[j].mass;
								bodies[i].updateClass();
								bodies[j].mass = 0.0;
							}
							else
							{
								bodies[j].mass += bodies[i].mass;
								bodies[j].updateClass();
								bodies[i].mass = 0.0;
							}
						}
						
					}
				}
			}
		}
	}
    
}