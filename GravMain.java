import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;


public class GravMain {

	public static ArrayList<Star> stars;
	public static Body bodies[];
	public static int collisionCount = 0;
	public static double BHoleMass = 0;
	public static String inputPath = "";
	public static String outputPath = "";
	public static double TimeStep = 0;
	
	public static void main(String[] args) 
	{
		String[] configs = readConfig();
		
		try{
			BHoleMass = Double.parseDouble(configs[2]);
			inputPath = configs[3];
			TimeStep = Double.parseDouble(configs[4]);
			outputPath = configs[5];
		
			stars = new ArrayList<Star>();
			readFile();
			bodies = new Body[stars.size() + 1];
			start();
			int maxSteps = Integer.parseInt(configs[0]);
			int writeOutCount = 0;
			int writeOutValue = Integer.parseInt(configs[1]);

			for(int i = 0; i < maxSteps; i++)
			{
				addforces(bodies.length);
				writeOutCount++;
				if(writeOutCount >= writeOutValue)
				{
					writeOut();
					writeOutCount = 0;
					//System.out.println(i + " Collisions: " + collisionCount);
				}
				
		
			}
		}
		catch(Exception e)
		{
			System.out.println("Config error");
		}
	}
	public static void start()
	{
		double solarmass = 1.98892e30;
		double blackHoleMass = BHoleMass;
		Body BH = new Body(0,0,0,0,(blackHoleMass),"Black Hole");
		bodies[0] = BH;
		//bodies[1] = new Body(0,0,0,0,(1e1*solarmass), "Black Hole");
		for(int i = 0; i < stars.size(); i++)
		{
			double px = stars.get(i).getX();
			double py = stars.get(i).getY();
			double mass = stars.get(i).getMass();
			
			double magv = circlev(px,py,blackHoleMass);
			
			double absangle = Math.atan(Math.abs(px/py));
			double thetav = Math.PI/2-absangle;
			double vx   = -1*Math.signum(py)*Math.cos(thetav)*magv;
		    double vy   = Math.signum(px)*Math.sin(thetav)*magv;
		    
		    /*
		    if (Math.random() <=.5) {
	              vx=-vx;
	              vy=-vy;
	            }
	        */
		    
		    //double mass = stars.get(i).getMass() * solarmass;
		   
			
		    bodies[i+1] = new Body(px, py, vx, vy, mass, stars.get(i).getStarClass());
		}
	}
	public static void addforces(int n)
	{
		for(int i = 0; i < n; i++)
		{
			if(bodies[i].mass > 0)
			{
				bodies[i].resetForce();
				for(int j = 0; j < n; j++)
				{
					if ( i != j && bodies[j].mass > 0)
					{
						bodies[i].addForce(bodies[j]);
					
					  
						if(bodies[i].checkCollision(bodies[j]))
						{
							//System.out.println("Collision");
							collisionCount++;
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
		for(int i = 0; i < n; i++)
		{
			if(bodies[i].mass > 0)
			{
				//bodies[i].update(1e11);
				bodies[i].update(TimeStep);
			}
		}
	}
	public static double circlev(double rx, double ry, double bMass)
	{
		
		double c = 2.998e8;
		double G = 6.673e-11;
		double r = Math.sqrt((rx*rx)+(ry*ry));
		double rs = (2 * (G * bMass))/Math.pow(c,2);
		double numerator = G*bMass;

		return Math.sqrt(numerator/(r-rs));
		
		
		//return 2e3;
		
		
	}
	public static void readFile()
	{
		String path = inputPath;
		BufferedReader br = null;
		String line = "";
		String delim = ",";
		
		try{
			br = new BufferedReader(new FileReader(path));
			while((line = br.readLine())!= null)
			{
				//System.out.println(line);
				String[] input = line.split(delim);
				if(!input[0].contains("X"))
				{	
					Star s = new Star(Double.parseDouble(input[0]), Double.parseDouble(input[1]), input[3], Double.parseDouble(input[2]));
					stars.add(s);
				}
			}
			
		}catch(Exception e)
		{}
	}
	public static void writeOut()
    {
    	 try
        {
            PrintWriter writer = new PrintWriter(outputPath,"UTF-8");
        
        	writer.println("X,Y,Size,Class");
            for(Body b : bodies)
            {
            	if(b.mass > 0)
               		writer.println(b.rx + ", " + b.ry +" ," + b.mass +  ", " + b.getStarClass());
               		
                
            }
            writer.println("END");
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println("error");
        }
    }
     public static String[] readConfig()
	{
	    String[] configs = new String[6];
		String path = "config.cfg";
		BufferedReader br = null;
		String line = "";
		String comment = "#";
		
		try
		{
			br = new BufferedReader(new FileReader(path));
			while((line = br.readLine())!= null)
			{
			    if(!line.contains(comment))
			    {
				    if(line.contains("MaxSteps"))
				    {  
				        String[] t = line.split("=");
				        configs[0] = t[1].substring(1).trim();
				    }
				    else
				    if(line.contains("WriteOutValue"))
				    {   
				        String[] t = line.split("=");
				        configs[1] = t[1].substring(1).trim();
				    }
				    else
				    if(line.contains("BlackHoleMass"))
				    {   
				        String[] t = line.split("=");
				        configs[2] = t[1].substring(1).trim();
				    }
				    else
				    if(line.contains("InputPath"))
				    {   
				        String[] t = line.split("=");
				        configs[3] = t[1].substring(1).trim();
				    }
				    else
				    if(line.contains("TimeStep"))
				    {   
				        String[] t = line.split("=");
				        configs[4] = t[1].substring(1).trim();
				    }
				    else
				    if(line.contains("OutputDir"))
				    {   
				        String[] t = line.split("=");
				        configs[5] = t[1].substring(1).trim();
				    }
				    
			    }
			}
			for(int i = 0; i < configs.length; i++)
		    {
		        System.out.println(configs[i]);
		    }
			
		}
		catch(Exception e)
		{
		    System.out.println("Error reading Config");
		    System.out.println(e.getMessage());
		}
		
		return configs;
		
	}

}

