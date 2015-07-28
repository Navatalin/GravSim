import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class GravMain {

	public static ArrayList<Star> stars;
	public static Body bodies[];
	public static int collisionCount = 0;
	public static double BHoleMass = 0;
	public static String inputPath = "";
	public static String outputPath = "";
	public static double TimeStep = 0;
	public static int imgCount = 0;
	public static double maxx, maxy = 0;
	public static String imgOutputPath = "";
	public static int threadCount = 0;
	
	public static void main(String[] args) 
	{
		String[] configs = readConfig();
		
		try{
			BHoleMass = Double.parseDouble(configs[2]);
			inputPath = configs[3];
			TimeStep = Double.parseDouble(configs[4]);
			outputPath = configs[5];
			imgOutputPath = configs[6];
			threadCount = Integer.parseInt(configs[7]);
			
			stars = new ArrayList<Star>();
			System.out.println("Reading Files");
			readFile();
			bodies = new Body[stars.size() + 1];
			System.out.println("Initializing System");
			start();
			int maxSteps = Integer.parseInt(configs[0]);
			int writeOutCount = 0;
			int writeOutValue = Integer.parseInt(configs[1]);
			
			System.out.println("Begining loop");
			for(int i = 0; i < maxSteps; i++)
			{
				//addforces(bodies.length);
				
				//System.out.println("Starting Add forces");
				try
				{
					MTAddForces(threadCount);
				}
				catch(InterruptedException IE)
				{
					System.out.println(IE.getMessage());
				}
				catch(ExecutionException EE)
				{
					System.out.println(EE.getMessage());
				}
				
				writeOutCount++;
				if(writeOutCount >= writeOutValue)
				{
					writeOut();
					writeOutCount = 0;
					//System.out.println(i + " Collisions: " + collisionCount);
				}
				System.out.println("Output Image: " + i);
				imgOut();
				imgCount++;
				
		
			}
		}
		catch(Exception e)
		{
			System.out.println("Config error " + e.getMessage());
			
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
			
			if(Math.abs(px) > maxx)
				maxx = Math.abs(px);
				
			if(Math.abs(py) > maxy)
				maxy = Math.abs(py);
			
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
	public static void imgOut() throws Exception
	{
		String imgCountStr = String.format("%010d", imgCount);
		String imgOutputDir = imgOutputPath + "/img" + imgCountStr + ".jpeg";
	
		ScatterPlot s = new ScatterPlot(bodies);
		s.writeImage(imgOutputDir,maxx*1.2,maxy*1.2);
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
	    String[] configs = new String[8];
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
				    else
				    if(line.contains("ImgDir"))
				    {
				    	String[] t = line.split("=");
				    	configs[6] = t[1].substring(1).trim();
				    }
				    else
				    if(line.contains("Threads"))
				    {
				    	String[] t= line.split("=");
				    	configs[7] = t[1].substring(1).trim();
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
	public static void MTAddForces(int t) throws InterruptedException, ExecutionException
	{
		float split = bodies.length/t;
		
		//System.out.println("Starting pool " + split);
		//System.out.println("Starting Array Size: " + bodies.length);
		
		ExecutorService pool = Executors.newFixedThreadPool(t);
		ArrayList<Integer> starts = new ArrayList<Integer>();
		ArrayList<Integer> ends = new ArrayList<Integer>();
		
		Set<Future<Body[]>> set = new HashSet<Future<Body[]>>();
		
		//int start = 0;
		int end = Math.round(split);
		//System.out.println("End point: " + end);
		
		//System.out.println("Creating tasks");
		for(int start = 0; start < bodies.length; start++)
		{
			//System.out.println("Calling compute");
			Callable<Body[]> callable = new Compute(bodies,start,end);
			
			//System.out.println("Adding to pool");
			Future<Body[]> future = pool.submit(callable);
			
			//System.out.println(start);
			starts.add(start);
			//System.out.println(end);
			ends.add(end);
			
			set.add(future);
			start = end;
			end += end;
			
			if(end > bodies.length)
				end = bodies.length;
		}
		//System.out.println("Number of Tasks: " + set.size());
		
		//System.out.println("Create new bodies array");
		Body[] nBodies = new Body[bodies.length];
		
		int segCount = 0;
		
		//System.out.println("Merge Returns");
		
		//System.out.println(starts.size() + " " + ends.size());
		
		for(Future<Body[]> future : set)
		{
			//System.out.println("Get future data");
			Body[] sub = future.get();
			
			int cStart = starts.get(segCount);
			int cEnd = ends.get(segCount);
			
			//System.out.println("Merge "  + segCount + " Start: " + cStart + " End: " +cEnd);
			if(cStart > 0)
				cStart--;
				
			for(int i = cStart; i < cEnd; i++)
			{
				nBodies[i] = sub[i];
			}
			segCount++;
		}
		
		//System.out.println("New Array count: " + nBodies.length + " Old Array count: " + bodies.length);
		
		System.arraycopy(nBodies,0,bodies,0,nBodies.length);
		
		//System.out.println("Do update");
		for(int i = 0; i < bodies.length; i++)
		{
			//System.out.println(bodies[i]);
			if(bodies[i].mass > 0)
			{
				//System.out.println("doing update: " + i);
				//bodies[i].update(1e11);
				bodies[i].update(TimeStep);
			}
		}
		//System.out.println("Finished Update");
		pool.shutdown();
		
	}
	
}

