package Yahtzee;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.JButton;



/**
 *
 * @author forrest_meade
 */
public class YahtzeeDie extends JButton 
{
    int face; // number of dots showing 1-6
    boolean keep;
    
    /**
     * 
     * 
     * @param _keep 
     */
    public YahtzeeDie(boolean _keep)
    {
        keep = _keep;
        
        setPreferredSize(new Dimension(100,100));
        setEnabled(true);
        addMouseListener(new DieAction());
        
        repaint();
    }
    
    /**
     * 
     */
    private class DieAction implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {
            keep = !keep;
            repaint();
        }
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }
    
    /**
     * 
     */
    public void roll()
    {
        if(!keep)
        {
            face = (int)(Math.random()*6+1);
        }
    }
    
    /**
     * 
     * 
     * @param g 
     */
    @Override
    public void paint(Graphics g){
        super.paint(g);

        if (keep){ 
        	setEnabled(false); 
        	setOpaque(true); 
        }
        else { 
        	setEnabled(true);
        	setOpaque(false);
        }
        
        int a = 20,b = 45, c = 70;

        if (face==1 || face==3 || face==5){
            g.fillOval(b,b,10,10);
        }
        if (face>1){
            g.fillOval(a,a,10,10);
            g.fillOval(c,c,10,10);
        }
        if (face==4 || face==5 || face==6){
            g.fillOval(c,a,10,10);
            g.fillOval(a,c,10,10);
        }
        if(face==6){
            g.fillOval(a,b,10,10);
            g.fillOval(c,b,10,10);
        }
    }
}
