package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import world.World;

/**
 * Random guess player (task A).
 * Please implement this class.
 */
public class RandomGuessPlayer implements Player{

	World world = null;
	List<Integer> hitedrows = new ArrayList<Integer>();
	List<Integer> hitedcolumns = new ArrayList<Integer>();
	List<Integer> sunks = new ArrayList<Integer>();
	int shipalive = 0;
	Guess guess = new Guess();
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
    		// random a number assign to guess.row and guess.column between 0 to 9
    		guess.row = random.nextInt(world.numRow);
        	guess.column = random.nextInt(world.numColumn);        	
        	
        	// if this the first time to make the guess, break loop and return the guess
        	if(hitedrows.size()==0)
        		break;
        	
        	// check the repetition of hit
        	int checksame = 0;   	
        	if(hitedrows.size()!=0)
        	{
        		for(int i=0;i<hitedrows.size();i++)
            	{
            		if(guess.row==hitedrows.get(i)&&guess.column==hitedcolumns.get(i))
            				checksame = 1;
            	}
        	}  	       	  		    	                	
        	
        	// if the hit location has been made before, generate a new one 
        	if(checksame==1)
        		continue;
        	// if the hit location is new, break loop and return guess
        	else
        		break;    		
        }
    	
    	return guess;
    } // end of makeGuess()

    /**
     * add the guess number into the hit location lists
     */
    @Override
    public void update(Guess guess, Answer answer) 
    {      	
    	hitedrows.add(guess.row);
		hitedcolumns.add(guess.column);
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
		// TODO Auto-generated method stub
		return 0;
	}

} // end of class RandomGuessPlayer
