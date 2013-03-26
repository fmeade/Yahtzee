package Yahtzee;



/**
 *
 * @author forrest_meade
 */
public class Score 
{
    protected Player whichPlayer;
    protected String label;
    protected int value;
    protected boolean used;

    public Score(String _label) 
    {
        label = _label;
        used = false;
    }
}
