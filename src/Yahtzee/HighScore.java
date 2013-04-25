package Yahtzee;

/**
 * Creates a HighScore object with the name and score
 *
 * @author forrest_meade
 */
public class HighScore {
    /* name of choice */
    private String name;
    /* score amount */
    private int score;

    /**
     * instantiates the name and score
     *
     * @param name name of choice
     * @param score score amount
     */
    public HighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return score
     */
    public int getScore() {
        return score;
    }
}
