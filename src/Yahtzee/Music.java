package Yahtzee;

import sun.audio.*;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author forrest_meade
 */
public class Music 
{
    boolean play1 = true;
    boolean play2 = true;
    
    public void music1() {

        try {
           
            File soundFile = new File("Jazz_Elevator.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            
 
            if (play1 == true) {
                // Open audio clip and load samples from the audio input stream.
                clip.open(audioIn);

                // Starts the clip
                clip.start();

                // Loops the clip continuously
                clip.loop(-1);
            } else {
                clip.stop();
                clip.close();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void music2() {

        try {
            File soundFile = new File("Ode_to_Joy.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();

            if (play2 == true) {
                // Open audio clip and load samples from the audio input stream.
                clip.open(audioIn);

                // Starts the clip
                clip.start();
            }
            else {
                clip.stop();
                clip.close();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void stopped1()
    {
        play1 = false;
    }
    public void stopped2()
    {
        play2 = false;
    }
}
