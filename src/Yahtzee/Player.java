package Yahtzee;



/**
 *
 * @author forrest_meade
 */
public class Player 
{
    private final int MAXTURNS = 13;
    private final int MAXROLLS = 3;
    
    protected String name;
    protected int turnsRemaining,rollCount;
    protected String[] scoreName;
    protected Score[] scoreObj;
    /**
     * [0] aces		[6] 3 of a kind		[12] chance
     * [1] twos		[7] 4 of a kind		[13] subtotal left
     * [2] threes	[8] full house		[14] bonus
     * [3] fours	[9] small straight	[15] subtotal right
     * [4] fives	[10] large straight	[16] total score
     * [5] sixes	[11] Yahtzee
     */
    
    int subtotalLeft = 0;
    int subtotalRight = 0;
    int grandTotal = 0;
    int bonusPoints = 0;
    boolean[] usedCategory; // If choice has been used
    
    /**
     * 
     * 
     * @param _name
     * @param _scoreName 
     */
    public Player(String _name,String[] _scoreName)
    {
        name = _name; // Player's name
        scoreName = _scoreName;// Score names
        
        turnsRemaining = MAXTURNS;// game consists of 13 rolls
        rollCount = MAXROLLS;// each turn consists of 3 rolls
        
        scoreObj = new Score[scoreName.length];
        for(int i=0;i<scoreObj.length;i++)
        {
            scoreObj[i] = new Score(scoreName[i]);
        }
        
        scoreObj[13].value = 0;
        scoreObj[14].value = 0;
        scoreObj[15].value = 0;
        scoreObj[16].value = 0;
        
    }
    
    /**
     * 
     * 
     * @param score
     * @param turn
     * @return 
     */
    public int calculateSubtotal(int score, int turn)
    {
        int result = scoreObj[turn].value += score;
        return result;
    }
    
    /**
     * 
     * 
     * @return 
     */
    public int grandTotal()
    {
        for(int i=13;i<15;i++)
        {
            scoreObj[16].value +=scoreObj[i].value;
        }
        return scoreObj[16].value;
    }
    
}
