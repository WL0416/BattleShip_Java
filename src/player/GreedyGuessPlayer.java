package player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import world.World;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 */
public class GreedyGuessPlayer  implements Player
{
	World world = null;
	List<Integer> hitedrows = new ArrayList<Integer>();
	List<Integer> hitedcolumns = new ArrayList<Integer>();
	Queue<Integer> trow = new LinkedList<Integer>();
	Queue<Integer> tcolumn = new LinkedList<Integer>();;
	List<Integer> sunks = new ArrayList<Integer>();
	int shipalive = 0;
	Guess guess = new Guess();
	Guess lastGuess = new Guess();
	int round = 0;
	int mode = 0; // 0 is hunting, 1 is targeting
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
        		guess.row = random.nextInt(world.numRow);
        		guess.column = random.nextInt(world.numColumn);
        		
        		// use mod to meet the task 2's requirement
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
            		continue;
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
            			tempc.add(guess.column);
            			tempr.add(guess.row);
            			tempc.add(guess.column+1);
            		}
            		// if the location is top left corner, generate down and right
            		else if(guess.row==world.numRow-1&&guess.column==0)
            		{
            			tempr.add(guess.row);
            			tempc.add(guess.column+1);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column);
            		}
            		// if the location is top right corner, generate down and left
            		else if(guess.row==world.numRow-1&&guess.column==world.numColumn-1)
            		{
            			tempr.add(guess.row);
            			tempc.add(guess.column-1);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column);
            		}
            		// if the location is bottom right corner, generate up and left
            		else if(guess.row==0&&guess.column==world.numColumn-1)
            		{
            			tempr.add(guess.row);
            			tempc.add(guess.column-1);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column);
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
            		}
            		// if the location is left, generate up, down and right
            		else if(guess.column==0)
            		{
            			tempr.add(guess.row+1);
            			tempc.add(guess.column);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column);
            			tempr.add(guess.row);
            			tempc.add(guess.column+1);
            		}
            		// if the location is right, generate up, down and left
            		else if(guess.column==world.numColumn-1)
            		{
            			tempr.add(guess.row+1);
            			tempc.add(guess.column);
            			tempr.add(guess.row-1);
            			tempc.add(guess.column);
            			tempr.add(guess.row);
            			tempc.add(guess.column-1);
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
     * turn the shot mode into different ones
     */
    @Override
    public void update(Guess guess, Answer answer) 
    {	
    	// if hit on the ship, mode equals 1
    	if((answer.isHit&&answer.shipSunk==null)
    			||(answer.isHit&&answer.shipSunk!=null&&!trow.isEmpty()))
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

} // end of class GreedyGuessPlayer
