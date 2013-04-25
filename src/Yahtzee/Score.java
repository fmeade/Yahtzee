package Yahtzee;

/**
 * Creates a score object
 *
 * @author forrest_meade
 */
public class Score {

    protected Player whichPlayer;
    protected String label;
    protected int value;
    protected boolean used;

    /**
     * puts the score object to unused and puts the label on there
     *
     * @param _label the label for the score choice
     */
    public Score(String _label) {
        label = _label;
        used = false;
    }
}
