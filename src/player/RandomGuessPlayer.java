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
	Answer answer = new Answer();
	
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

    @Override
    public Answer getAnswer(Guess guess) 
    {
    	for(int i=0;i<world.shipLocations.size();i++)
    	{
    		for(int j=0;j<world.shipLocations.get(i).coordinates.size();j++)
    		{
    			if(guess.row == world.shipLocations.get(i).coordinates.get(j).row
    				&& guess.column == world.shipLocations.get(i).coordinates.get(j).column)
    			{
    				answer.isHit = true;
    				int shiplen = sunks.get(i);
    				shiplen--;
    				sunks.set(i, shiplen);
    				if(sunks.get(i)==0)
    					answer.shipSunk = world.shipLocations.get(i).ship;   				
    			}
    		}
    	}
        return answer;
    } // end of getAnswer()


    @Override
    public Guess makeGuess() 
    {
    	while(true)
    	{
    		guess.row = random.nextInt(world.numRow);
        	guess.column = random.nextInt(world.numColumn);
        	
        	if(hitedrows.size()==0)
        	{
        		hitedrows.add(guess.row);
        		hitedcolumns.add(guess.column);
        	}
        	
        	int checksame = 0;
        	for(int i=0;i<hitedrows.size();i++)
        	{
        		if(guess.row==hitedrows.get(i)&&guess.column==hitedcolumns.get(i))
        				checksame = 1;
        	}
        	
        	if(checksame==1)
        		continue;
        	else
        		break;
        }
    	
    	return guess;
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) 
    {
    	hitedrows.add(guess.row);
    	hitedcolumns.add(guess.column);
		if(answer.shipSunk!=null)
		{
			shipalive--;
		}
    } // end of update()


    @Override
    public boolean noRemainingShips() 
    {
    	if(shipalive>0)
    		return false;
    	else
    		return true;   		
    } // end of noRemainingShips()

} // end of class RandomGuessPlayer
