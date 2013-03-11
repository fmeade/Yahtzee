package Yahtzee;



/**
 *
 * @author forrest_meade
 */
public class YahtzeeDie 
{
    int face; // number of dots showing 1-6
    boolean keep;
    
    public YahtzeeDie(boolean _keep)
    {
        keep = _keep;
    }
    
    public void roll()
    {
        if(!keep)
        {
            face = (int)(Math.random()*6+1);
        }
    }
}
