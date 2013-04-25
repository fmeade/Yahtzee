package Yahtzee;

import java.io.*;
import javax.sound.sampled.*;

/**
 * Creates a Music object
 *
 * @author forrest_meade
 */
public class Music {

    /*
     * Creates a clip for the inGame song
     * "Jazz Elevator music"
     */
    public void music1() {

        try {

            File soundFile = new File("Jazz_Elevator.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();


            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);

            // Starts the clip
            clip.start();

            // Loops the clip continuously
            clip.loop(-1);
            clip.stop();
            clip.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Creates a clip of the winning music "Ode to Joy"
     */
    public void music2() {

        try {
            File soundFile = new File("Ode_to_Joy.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();

            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);

            // Starts the clip
            clip.start();
            clip.stop();
            clip.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
