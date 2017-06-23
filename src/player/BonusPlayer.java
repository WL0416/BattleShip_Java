package player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import world.World;

/**
 * Player (handle hex) for bonus task.
 * Only implement this if attempting the bonus task, otherwise can leave alone.
 */
public class BonusPlayer  implements Player{

	World world = null;
	List<Integer> hitedrows = new ArrayList<Integer>();
	List<Integer> hitedcolumns = new ArrayList<Integer>();
	Queue<Integer> trow = new LinkedList<Integer>();
	Queue<Integer> tcolumn = new LinkedList<Integer>();;
	List<Integer> sunks = new ArrayList<Integer>();
	List<String> sunkships = new ArrayList<String>();
	int shipalive = 0;
	Guess guess = new Guess();
	Guess lastGuess = new Guess();
	int round = 0;
	int mode = 0; // 0 is hunting, 1 is targeting
	int guessMode = 0;
	int sunkCalculation = 0;
	Random random = new Random();

	/**
	 * initial player, pass the world to the player object, 
	 * assign the ship life depending on how many ships stored in the world,
	 * sunk list will record the ships length, once the length becomes 0, that means the ship is sunk
	 */
    @Override
    public void initialisePlayer(World world) 
    {
    	this.world = world;
    	this.shipalive = world.shipLocations.size();
    	for(int i=0;i<this.world.shipLocations.size();i++)
    	{
    		sunks.add(this.world.shipLocations.get(i).ship.len());
    	}
    } // end of initialisePlayer()

    /**
     * depending on the guess which the other player make to evaluate if the ship is hit and sunk.  
     */
    @Override
    public Answer getAnswer(Guess guess) 
    {
    	Answer answer = new Answer();
    	// loop the ships in the world
    	for(int i=0;i<world.shipLocations.size();i++)
    	{
    		// loop each ship's location
    		for(int j=0;j<world.shipLocations.get(i).coordinates.size();j++)
    		{
    			// if the hit location is the same with the any part's location of ship
    			if(guess.row == world.shipLocations.get(i).coordinates.get(j).row
    				&& guess.column == world.shipLocations.get(i).coordinates.get(j).column)
    			{
    				// the ship is hit
    				answer.isHit = true;   				
    				int shiplen = sunks.get(i);				
    				shiplen--;
    				// the current ship's length minus 1
    				sunks.set(i, shiplen);
    				// if ship's length equals 0, the ship is sunk
    				if(sunks.get(i)==0)
    				{
    					answer.shipSunk = world.shipLocations.get(i).ship;
    					// remove the ship from current world
    					world.shipLocations.remove(i);
    			    	sunks.remove(i);
    			    	// ship's life minus 1
    			    	shipalive--;
    			    	lastGuess = guess;
    				}
    				break;
    			}
    		}
    		// if ship is hit, break the loop
    		if(answer.isHit)
    			break;
    	}
        return answer;
    } // end of getAnswer()

    /**
     * generate the guess number from player
     * there are four guess modes in the game, depending on the ship's length
     * they have different random region
     * guessFive() has the smallest region
     * guessTwo() has the biggest region
     */
    @Override
    public Guess makeGuess() 
    {
    	// start loop,if the hit location has been made before, generate a new one 
    	while(true)
    	{
    		// if mode equals 0 and the erow queue is empty which means the it is not in the target mode
    		// turn to hunting mode
        	if(mode==0 && trow.isEmpty())
        	{
        		// random a number depending on the world's row and column
        		guess.column = random.nextInt(world.numColumn+(world.numColumn/2)-1);
        		
        		if(guess.column>=world.numColumn)
        		{
        			guess.row = random.nextInt(world.numRow-(guess.column-world.numRow+1)) + (guess.column-world.numRow);
        		}
        		else if(guess.column==0)
        		{
        			guess.row = 0;
        		}
        		else
        		{
        			guess.row = random.nextInt(world.numRow);
            		if(guess.row%2==1)
            			guess.row+=1;
        			guess.row = random.nextInt(guess.row+1);
        		}
        		
        		guess = guessTwo(guess);
        		
        		// depending on the guess mode to determine the guess processing function
        		/*
        		switch(guessMode)
        		{
        		case 0:
        			guess = guessFive(guess);
        			break;
        		case 1:
        			guess = guessFour(guess);
        			break;
        		case 2:
        			guess = guessThree(guess);
        			break;
        		case 3:
        			guess = guessTwo(guess);
        			break;
        		} */
        		
        		// check the repetition of hit
        		int checksame = 0;
            	if(hitedrows.size()!=0)
            	{
                	for(int i=0;i<hitedrows.size();i++)
                	{
                		if(guess.row==hitedrows.get(i)
                				&&guess.column==hitedcolumns.get(i))
                				checksame = 1;
                	}
            	}
        		
            	// if the guess is repetitive, generate a new one
            	if(checksame==1)
            	{
            		continue;
            	}
            	// if the guess is new, add the guess into the hit recording lists
            	else
            	{
            		hitedrows.add(guess.row);
                	hitedcolumns.add(guess.column);
            		break;
            	}
            	
        	}
        	// if the mode is not 0 or the trow queue is not empty
        	// turn to targeting mode
        	else
        	{
        		// if mode equals 1, generate the location around current hit location 
        		if(mode==1)
        		{
        			List<Integer> tempr = new ArrayList<Integer>();
            		List<Integer> tempc = new ArrayList<Integer>();
            		// if the location is bottom left corner, generate up and right
            		if(guess.row==0&&guess.column==0)
            		{
            			tempr.add(guess.row + 1);
            			tempc.add(guess.column + 1);
            			tempr.add(guess.row);
            			tempc.add(guess.column+1);
            		}
            		// if the location is top left corner, generate down and right
            		else if(guess.row==world.numRow-1&&guess.column==0)
            		{
            			if(guess.row%2==0)
            			{
            				tempr.add(guess.row);
                			tempc.add(guess.column+1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column);
            			}
            			else
            			{
            				tempr.add(guess.row);
                			tempc.add(guess.column+1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column-1);
            			}
            		}
            		// if the location is top right corner, generate down and left
            		else if(guess.row==world.numRow-1&&guess.column==world.numColumn-1)
            		{
            			if(guess.row%2==0)
            			{
            				tempr.add(guess.row);
                			tempc.add(guess.column-1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column-1);
                			
            			}
            			else
            			{
            				tempr.add(guess.row);
                			tempc.add(guess.column-1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column-1);
            			}	
            		}
            		// if the location is bottom right corner, generate up and left
            		else if(guess.row==0&&guess.column==world.numColumn-1)
            		{
            			if(guess.row%2==0)
            			{
            				tempr.add(guess.row+1);
                			tempc.add(guess.column+1);
            				tempr.add(guess.row+1);
                			tempc.add(guess.column);
                			tempr.add(guess.row);
                			tempc.add(guess.column-1);
            			}
            			else
            			{
            				tempr.add(guess.row+1);
                			tempc.add(guess.column);
            				tempr.add(guess.row);
                			tempc.add(guess.column-1);
            			}	
            		}
            		// if the location is bottom, generate up, left and right
            		else if(guess.row==0)
            		{
            			
            			tempr.add(guess.row);
            			tempc.add(guess.column-1);
            			tempr.add(guess.row);
            			tempc.add(guess.column+1);
            			tempr.add(guess.row+1);
            			tempc.add(guess.column);
            			tempr.add(guess.row+1);
            			tempc.add(guess.column+1);
            		}
            		// if the location is top, generate down, left and right
            		else if(guess.row==world.numRow-1)
            		{
            			tempr.add(guess.row);
            			tempc.add(guess.column-1);
            			tempr.add(guess.row);
            			tempc.add(guess.column+1);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column-1);
            		}
            		// if the location is left, generate up, down and right
            		else if((guess.column*2==guess.row)||(guess.column*2==guess.row-1))
            		{
            			if(guess.row%2==0)
            			{
            				tempr.add(guess.row+1);
                			tempc.add(guess.column+1);
                			tempr.add(guess.row);
                			tempc.add(guess.column+1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column);
            			}
            			else
            			{
            				tempr.add(guess.row+1);
                			tempc.add(guess.column);
                			tempr.add(guess.row+1);
                			tempc.add(guess.column+1);
                			tempr.add(guess.row);
                			tempc.add(guess.column+1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column-1);
            			}
            			
            		}
            		// if the location is right, generate up, down and left
            		else if((guess.column>world.numColumn-1)&&guess.row<=(guess.column-world.numRow+1)*2)
            		{
            			if(guess.row%2==0)
            			{
            				tempr.add(guess.row+1);
                			tempc.add(guess.column+1);
                			tempr.add(guess.row+1);
                			tempc.add(guess.column);
                			tempr.add(guess.row);
                			tempc.add(guess.column-1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column-1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column);
            			}
            			else
            			{
            				tempr.add(guess.row+1);
                			tempc.add(guess.column);
                			tempr.add(guess.row);
                			tempc.add(guess.column-1);
                			tempr.add(guess.row-1);
                			tempc.add(guess.column-1);
            			}
            		}
            		// if the location is other situations, generate up, down, left and right
            		else
            		{
            			tempr.add(guess.row+1);
            			tempc.add(guess.column);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column);
            			tempr.add(guess.row);
            			tempc.add(guess.column+1);
            			tempr.add(guess.row);
            			tempc.add(guess.column-1);
            			tempr.add(guess.row+1);
            			tempc.add(guess.column+1);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column-1);
            		}
                	
            		// check if the generated locations are overlap with existing locations
            		for(int i=0;i<tempr.size();i++)    		
                	{   
            			int checksame = 0;
            			for(int j=0;j<hitedrows.size();j++)
            			{           				
            				if(tempr.get(i)==hitedrows.get(j)
            						&&tempc.get(i)==hitedcolumns.get(j))
            				{
            					checksame = 1;
            					break;
            				}        					
            			}
            			
            			// if the location is new, add it in the queue and hit recording lists
            			if(checksame==0)
            			{
            				trow.add(tempr.get(i));
            				tcolumn.add(tempc.get(i));
            				hitedrows.add(tempr.get(i));
            				hitedcolumns.add(tempc.get(i));
            			}
                	}
        		}       		        			 			      	
        		
        		// if the queue is not empty, poll an element to assign to the guess 
        		if(!trow.isEmpty())
        		{
        			guess.row = trow.poll();
                	guess.column = tcolumn.poll();
        		}        
        	
        		break;
        	}
    	}   	
    	return guess;
    } // end of makeGuess()

    
    /**
     * turn the shot mode and guess mode into different ones
     */
    @Override
    public void update(Guess guess, Answer answer) 
    {	
    	// if hit on the ship, mode equals 1
    	if((answer.isHit&&answer.shipSunk==null)
    			||answer.isHit&&answer.shipSunk!=null&&!trow.isEmpty())
    	{
    		mode = 1;
    	}
    	// if hit is not on the ship and queue is not empty, mode equals 2
    	else if(!answer.isHit&&!trow.isEmpty())
    	{
    		mode = 2;
    	}
    	// if hit is not on the ship and queue is empty, mode equals 0
    	else
    	{
    		mode = 0;
    	}	
    	
    	// change the guess mode if ship is sunk
    	if(answer.shipSunk!=null)
    	{
    		// add sunk ship into the list
    		sunkships.add(answer.shipSunk.name());
    		
    		int checkThree = 0;
    		// loop the sunk ship
    		for(int i = 0; i<sunkships.size();i++)
    		{	
    			// if sunk ships include AircraftCarrier and mode equals 0, turn to guess mode 1
    			if(sunkships.get(i).equals("AircraftCarrier")&&guessMode==0)
    				guessMode = 1;
    			
    			// if sunk ships include Battleship and mode equals 1, turn to guess mode 2
    			if(sunkships.get(i).equals("Battleship")&&guessMode==1)
    				guessMode = 2;
    			
    			// if sunk ships include Cruiser and Submarine in mode 2, turn to guess mode 3
    			if((sunkships.get(i).equals("Cruiser")&&guessMode==2)
    					||(sunkships.get(i).equals("Submarine")&&guessMode==2))
    				checkThree++;
    			
    			if(checkThree==2)
    				guessMode = 3;
    		}
    	}	
    } // end of update()

    /**
     * if ship's life equals 0, that means no ship is alive, return true
     */
    @Override
    public boolean noRemainingShips() 
    {
    	if(shipalive>0)
    		return false;
    	else
    		return true;
    } // end of noRemainingShips()

    /**
     * get the shot mode of player, 0 is hunting mode, 1 is targeting mode  
     */
	@Override
	public int getMode() {	
		return mode;
	}

	/**
	 * process the guess location for the ship which length is 2
	 * this function can generate the points with the biggest region
	 * @param guess
	 * @return
	 */
	public Guess guessTwo(Guess guess)
	{
		if(guess.row % 2 == 0)
		{	
			if(guess.column % 2 == 1)
			{
				guess.column--;
			}
		}
		else
		{
			if(guess.column % 2 == 0)
			{
				guess.column++;
			}
		}
		
		return guess;
	}
	
	/**
	 * process the guess location for the ship which length is 3
	 * @param guess
	 * @return
	 */
	public Guess guessThree(Guess guess)
	{
		if(guess.row % 3 == 0)
		{
			if(guess.column % 3 == 1)
				guess.column--;
			else if(guess.column % 3 == 2)
				guess.column-=2;
		}
		else if(guess.row % 3 == 1)
		{
			if(guess.column<1)
			{
				guess.column++;
			}
			else
			{
				if(guess.column % 3 == 0)
					guess.column-=2;
				else if(guess.column % 3 == 2)
					guess.column--;
			}
			
		}
		else if(guess.row % 3 == 2)
		{
			if(guess.column<2)
			{
				if(guess.column % 3 ==0)
					guess.column+=2;
				else if(guess.column % 3 == 1)
					guess.column++;
			}
			else
			{
				if(guess.column % 3 ==0)
					guess.column--;
				else if(guess.column % 3 == 1)
					guess.column-=2;
			}	
		}
		return guess;
	}
	
	/**
	 * process the guess location for the ship which length is 4
	 * @param guess
	 * @return
	 */
	public Guess guessFour(Guess guess)
	{
		if(guess.row % 4 == 0)
		{	
			if(guess.column % 4 == 1)
				guess.column--;
			else if(guess.column % 4 == 2)
				guess.column-=2;
			else if(guess.column % 4 == 3)
				guess.column-=3;		
		}
		else if(guess.row % 4 == 1)
		{
			if(guess.column % 4 == 0)
				guess.column++;			
			else if(guess.column % 4 == 2)
				guess.column--;		
			else if(guess.column % 4 == 3)
				guess.column-=2;	
		}
		else if(guess.row % 4 == 2)
		{
			if(guess.column<2)
			{
				if(guess.column % 4 ==0)
					guess.column+=2;
				else if(guess.column % 4 == 1)
					guess.column++;
			}
			else
			{
				if(guess.column % 4 ==0)
					guess.column-=2;
				else if(guess.column % 4 == 1)
					guess.column-=3;
				else if(guess.column % 4 == 3)
					guess.column--;		
			}	
		}
		else if(guess.row % 4 == 3)
		{
			if(guess.column<3)
			{
				if(guess.column % 4 ==0)
					guess.column+=3;		
				else if(guess.column % 4 == 1)
					guess.column+=2;
				else if(guess.column % 4 == 2)
					guess.column++;	
			}
			else
			{
				if(guess.column % 4 ==0)
					guess.column--;
				else if(guess.column % 4 ==1)
					guess.column-=2;
				else if(guess.column % 4 ==2)
					guess.column-=3;
			}
		}
		return guess;
	}
	
	/**
	 * process the guess location for the ship which length is 5
	 * this function can generate the points with the smallest region
	 * @param guess
	 * @return
	 */
	public Guess guessFive(Guess guess)
	{
		if(guess.row % 5 == 0)
		{
			if(guess.column % 5 == 1)
				guess.column--;
			else if(guess.column % 5 == 2)
				guess.column-=2;
			else if(guess.column % 5 == 3)
				guess.column-=3;
			else if(guess.column % 5 == 4)
				guess.column-=4;
		}
		else if(guess.row % 5 == 1)
		{
			if(guess.column % 5 == 0)
				guess.column++;
			else if(guess.column % 5 == 2)
				guess.column--;
			else if(guess.column % 5 == 3)
				guess.column-=2;
			else if(guess.column % 5 == 4)
				guess.column-=3;
		}
		else if(guess.row % 5 == 2)
		{
			if(guess.column % 5 == 0)
				guess.column+=2;
			else if(guess.column % 5 == 1)
				guess.column++;
			else if(guess.column % 5 == 3)
				guess.column--;
			else if(guess.column % 5 == 4)
				guess.column-=2;
		}
		else if(guess.row % 5 == 3)
		{
			if(guess.column % 5 == 0)
				guess.column+=3;
			else if(guess.column % 5 == 1)
				guess.column+=2;
			else if(guess.column % 5 == 2)
				guess.column++;
			else if(guess.column % 5 == 4)
				guess.column--;
		}
		else if(guess.row % 5 == 4)
		{
			if(guess.column % 5 == 0)
				guess.column+=4;
			else if(guess.column % 5 == 1)
				guess.column+=3;
			else if(guess.column % 5 == 2)
				guess.column+=2;
			else if(guess.column % 5 == 3)
				guess.column++;
		}
		return guess;
	}
} // end of class BonusPlayer
