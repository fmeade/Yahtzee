package Yahtzee;

/**
 * Creates a Player object
 *
 * @author forrest_meade
 */
public class Player {

    private final int MAXTURNS = 13;
    private final int MAXROLLS = 3;
    protected String name;
    protected int turnsRemaining, rollCount;
    protected String[] scoreName;
    protected Score[] scoreObj;
    /**
     * [0] aces	[6] 3 of a kind	[12] chance [1] twos	[7] 4 of a kind	[13]
     * subtotal left [2] threes	[8] full house	[14] bonus [3] fours	[9] small
     * straight	[15] subtotal right [4] fives	[10] large straight	[16] total
     * score [5] sixes	[11] Yahtzee
     */
    int subtotalLeft = 0;
    int subtotalRight = 0;
    int grandTotal = 0;
    int bonusPoints = 0;
    boolean[] usedCategory; // If choice has been used

    /**
     * Instantiates the instance variables
     *
     * @param _name players name
     * @param _scoreName array of score names
     */
    public Player(String _name, String[] _scoreName) {
        name = _name; // Player's name
        scoreName = _scoreName;// Score names

        turnsRemaining = MAXTURNS;// game consists of 13 rolls
        rollCount = MAXROLLS;// each turn consists of 3 rolls

        scoreObj = new Score[scoreName.length];
        for (int i = 0; i < scoreObj.length; i++) {
            scoreObj[i] = new Score(scoreName[i]);
        }

        scoreObj[13].value = 0;
        scoreObj[14].value = 0;
        scoreObj[15].value = 0;
        scoreObj[16].value = 0;

    }

    /**
     * Calculates the Subtotal
     *
     * @param score the score
     * @param turn the turn
     * @return the total of the subtotal
     */
    public int calculateSubtotal(int score, int turn) {
        int result = scoreObj[turn].value += score;
        return result;
    }

    /**
     * Calculates the grand total
     *
     * @return the total of the two subtotals
     */
    public int grandTotal() {
        scoreObj[16].value = 0;
        for (int i = 13; i <= 15; i++) {
            scoreObj[16].value += scoreObj[i].value;
        }
        return scoreObj[16].value;
    }
}
