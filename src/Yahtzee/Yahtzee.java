package Yahtzee;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.border.*;
import sun.audio.*;

/**
 * Creates a Yahtzee board Does all calculation for the game physics
 *
 * @author forrest_meade
 */
public class Yahtzee {
    /* Instance variables for game board */

    private JFrame mainFrame;
    private JPanel dicePanel, rollPanel, gamePanel;
    private JPanel column1, column2;
    private JButton rollButton;
    private JButton numberOfTurns;
    private JButton rolledTimes;
    private JButton scoreButton[];
    private JTextField scoreField[];
    private JLabel blank;
    /* Instance variables for the game physics */
    private YahtzeeDie[] dice;
    private Player[] players;
    private final String[] scoreName = {"Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "3 of a Kind", "4 of a Kind", "Full House",
        "Small Straight", "Large Straight", "Yahtzee!", "Chance",
        "Subtotal", "Bonus", "Subtotal", "Total Score"};
    private int[] occurrence = {0, 0, 0, 0, 0, 0};
    private int playAgain;
    private int whoseTurn;
    private int topScore;
    private final int numDie = 5;
    private String winner;
    /* Instance Variables for high score */
    private Queue<HighScore> highScoreList;
    private HighScore[] highscore;

    /* Instance Variable for music */
    private Music music = new Music();

    /**
     * Calls the build method
     */
    public Yahtzee() throws Exception {
        build();
    }

    /**
     * Builds the game board
     *
     * @throws Exception from Iterator
     */
    public void build() throws Exception {
        mainFrame = new JFrame();

        gameSetup();

        /* Begin building the playing board */
        scoreButton = new JButton[scoreName.length];
        scoreField = new JTextField[scoreName.length];

        mainFrame.setTitle("Yahtzee");
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(new Color(0, 140, 0));
        mainFrame.setSize(new Dimension(500, 500));
        mainFrame.setResizable(false);

        /* this panel displays the dice */
        dicePanel = new JPanel();
        dicePanel.setLayout(new GridLayout(1, 5));
        dicePanel.setBackground(mainFrame.getBackground());
        for (int i = 0; i < numDie; i++) {
            dice[i] = new YahtzeeDie(false);
            dicePanel.add(dice[i]);
        }
        mainFrame.add(dicePanel, BorderLayout.NORTH);

        /* this panel houses the score board */
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(1, 2));
        gamePanel.setBackground(mainFrame.getBackground());
        gamePanel.setBorder(new TitledBorder(players[whoseTurn].name));
        mainFrame.add(gamePanel, BorderLayout.CENTER);

        /* build the scoreboard with this method */
        buildPanel();

        /* this panel lets the user track their game */
        rollPanel = new JPanel();
        rollPanel.setLayout(new GridLayout(1, 3));
        rollPanel.setBackground(mainFrame.getBackground());
        rollPanel.setPreferredSize(new Dimension(500, 50));

        rollButton = new JButton("Roll!");
        rollButton.addActionListener(new Yahtzee.ButtonAction());
        rollPanel.add(rollButton);

        rolledTimes = new JButton("Rolls Remaining: " + players[whoseTurn].rollCount);
        rolledTimes.setEnabled(false);
        rollPanel.add(rolledTimes);

        numberOfTurns = new JButton("Turns Remaining: " + players[whoseTurn].turnsRemaining);
        numberOfTurns.setEnabled(false);
        rollPanel.add(numberOfTurns);

        mainFrame.add(rollPanel, BorderLayout.SOUTH);

        mainFrame.setVisible(true);

        buildMusic(true);
    }

    /**
     * Sets up the panels
     */
    public void buildPanel() {
        column1 = new JPanel();
        column1.setBackground(Color.white);
        column1.setLayout(new GridLayout(9, 3));
        gamePanel.add(column1);

        column2 = new JPanel();
        column2.setBackground(Color.white);
        column2.setLayout(new GridLayout(9, 3));
        gamePanel.add(column2);

        blank = new JLabel(" ");

        for (int i = 0; i < scoreButton.length; i++) {
            if (i < 6) {
                scoreButton[i] = makeButton(scoreName[i], column1, true);
                scoreField[i] = makeField(column1);

            } else if (i < 13) {
                scoreButton[i] = makeButton(scoreName[i], column2, true);
                scoreField[i] = makeField(column2);
            } else if (i < 15) {
                scoreButton[i] = makeButton(scoreName[i], column1, false);
                scoreField[i] = makeField(column1);

            } else {
                scoreButton[i] = makeButton(scoreName[i], column2, false);
                scoreField[i] = makeField(column2);
            }
        }
    }

    /**
     * Creates a button
     *
     * @param name name of the button
     * @param back panel to add button to
     * @param edit if able to edit button
     * @return returns the button created
     */
    public JButton makeButton(String name, JPanel back, boolean edit) {
        JButton jb = new JButton(name);
        jb.setEnabled(edit);
        jb.addActionListener(new Yahtzee.ButtonAction());
        back.add(jb);
        jb.setEnabled(false);
        return jb;
    }

    /**
     * Creates a text field
     *
     * @param back panel to place field
     * @return returns the field created
     */
    public JTextField makeField(JPanel back) {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(Color.white);
        back.add(tf);
        return tf;
    }

    /**
     * calls the appropriate music method
     *
     * @param x true if you want music1, false if you want music2
     */
    public void buildMusic(Boolean x) {
        if (x == true) {
            music.music1();
        } else {
            music.music2();
        }
    }

    /**
     * Activates and reads the button input for scoreboard and dice roll
     */
    private class ButtonAction implements ActionListener {

        /**
         * Action for score and roll buttons
         *
         * @param e which action is being used
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            //Roll dice button - validate and do!
            // if you click the roll button and while your roll count and turns remaining aren't 0...
            if (e.getSource() == rollButton && players[whoseTurn].rollCount != 0 && players[players.length - 1].turnsRemaining != 0) {
                if (rollButton.getText().contains("!")) {

                    // makes score buttons unenabled
                    for (int k = 0; k < scoreButton.length; k++) {
                        scoreButton[k].setEnabled(false);
                    }

                    // roll every die
                    for (int i = 0; i < numDie; i++) {
                        if (players[whoseTurn].rollCount == 3) {
                            dice[i].keep = false;
                        }

                        // rolls the 5 dice
                        dice[i].roll();

                    }
                    // reduce roll count afterwards
                    players[whoseTurn].rollCount--;

                    if (players[whoseTurn].rollCount < 3) {
                        for (int k = 0; k < scoreButton.length; k++) {
                            scoreButton[k].setEnabled(true);

                            // makes the buttons unusable if used
                            if (players[whoseTurn].scoreObj[k].used) {
                                scoreButton[k].setEnabled(false);
                            }
                        }

                    }

                    // and reprint the new total
                    rolledTimes.setText("Rolls Remaining: " + players[whoseTurn].rollCount);
                    if (players[whoseTurn].rollCount == 0) {
                        //calculate all possible totals user can pick from
                        for (int i = 0; i < 13; i++) {
                            if (players[whoseTurn].scoreObj[i].used != true) {
                                pickType(i, false);
                            }
                        }
                    }
                    mainFrame.repaint();
                } else {
                    switchPlayers();

                    // makes all button unusable
                    for (int k = 0; k < scoreButton.length; k++) {
                        scoreButton[k].setEnabled(false);
                    }
                }
            }

            // sets score for that turn
            for (int i = 0; i < 13; i++) {
                if (e.getSource() == scoreButton[i] && !players[whoseTurn].scoreObj[i].used) {
                    pickType(i, true);
                    displayScores();
                    gamePanel.validate();
                    try {
                        resetDice();
                    } catch (Exception ex) {
                        Logger.getLogger(Yahtzee.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Method to pick the type pattern the user is going for
     *
     * @param arrayPos position in the array for the score
     * @param click if user clicked that button
     */
    public void pickType(int arrayPos, boolean click) {
        if (arrayPos < 6) {
            scoreNum(arrayPos + 1, click);
        } // #s 1-6
        else if (arrayPos == 6) {
            ofAKind(3, 6, click);
        } // 3 of a kind
        else if (arrayPos == 7) {
            ofAKind(4, 7, click);
        } // 4 of a kind
        else if (arrayPos == 8) {
            fullHouse(8, click);
        } // full house
        else if (arrayPos == 9) {
            straight(9, click);
        } // small straight
        else if (arrayPos == 10) {
            straight(10, click);
        } // large straight
        else if (arrayPos == 11) {
            yahtzee(11, click);
        } // yahtzee (5 of a kind)
        else if (arrayPos == 12) {
            int score = countAllDie();
            displayAnswer(12, score, click, false);
        } // chance
        else {
            if (arrayPos <= 6) {
                displayAnswer(arrayPos, 0, click, true);
            } else if (arrayPos < 13) {
                displayAnswer(arrayPos, 0, click, false);
            }
        }
    }

    /**
     * adds the face of the die to score
     *
     * @param face face of the die displayed
     * @param click if user clicked that button
     */
    public void scoreNum(int face, boolean click) {
        int score = 0;
        for (int i = 0; i < numDie; i++) {
            if (dice[i].face == face) {
                score += face;
            }
        }

        displayAnswer(face - 1, score, click, true);
    }

    /**
     * Method for the score of a 3 or 4 of a kind
     *
     * @param face face of the die
     * @param arrayPos position of the score array
     * @param click if user clicked that button
     */
    public void ofAKind(int face, int arrayPos, boolean click) {
        int score = 0;

        diceOccurence(); //Counts dice values
        boolean met = false;

        for (int a = 0; a < 6; a++) {
            if (occurrence[a] >= face) {
                met = true;
            }
        }
        if (met == true) {
            score = countAllDie();
        }

        displayAnswer(arrayPos, score, click, false);
    }

    /**
     * Method for the score of a full house
     *
     * @param arrayPos position of the score array
     * @param click if user clicked that button
     */
    public void fullHouse(int arrayPos, boolean click) {
        int score = 0;
        diceOccurence();
        boolean part1 = false, part2 = false;

        for (int a = 0; a < 6; a++) {
            if (occurrence[a] == 3) {
                part1 = true;
            }
            if (occurrence[a] == 2) {
                part2 = true;
            }
        }

        if (part1 == true && part2 == true) {
            score = 25;
        }
        displayAnswer(arrayPos, score, click, false);
    }

    /**
     * Sets the score based on if the dice are a straight
     *
     * @param arrayPos position of this field in the array
     * @param click if user clicked that button
     */
    public void straight(int arrayPos, boolean click) {
        int score = 0;
        boolean mode = false;
        diceOccurence();

        if (arrayPos == 9) {
            for (int i = 0; i < 3; i++) {
                if (occurrence[i] != 0 && occurrence[i + 1] != 0
                        && occurrence[i + 2] != 0 && occurrence[i + 3] != 0) {
                    mode = true;
                }
            }
            if (mode) {
                score = 30;
            }
        } else {
            for (int i = 0; i < 2; i++) {
                if (occurrence[i] != 0 && occurrence[i + 1] != 0 && occurrence[i + 2] != 0
                        && occurrence[i + 3] != 0 && occurrence[i + 4] != 0) {
                    mode = true;
                }
            }
            if (mode) {
                score = 40;
            }
        }

        displayAnswer(arrayPos, score, click, false);
    }

    /**
     * Method for the score if the user got Yahtzee
     *
     * @param arrayPos position of this field in the array
     * @param click if user clicked that button
     */
    public void yahtzee(int arrayPos, boolean click) {
        int score = 0;
        boolean mode = false;
        diceOccurence();

        for (int a = 0; a < 6; a++) {
            if (occurrence[a] == 5) {
                mode = true;
            }
        }
        if (mode == true) {
            score = 50;
        }

        displayAnswer(arrayPos, score, click, false);
    }

    /**
     * Method for the score for chance
     *
     * @return total of all the die
     */
    public int countAllDie() {
        int score = 0;
        for (int i = 0; i < numDie; i++) {
            score += dice[i].face;
        }
        return score;
    }

    /**
     * Method for when a choice is used
     *
     * @param arrayPos place of choice in the array
     * @param click if user clicked it
     */
    public void used(int arrayPos, boolean click) {
        displayAnswer(arrayPos, 0, click, false);
    }

    /**
     * Method that displays the score in the correct text field
     *
     * @param pos position in the score array
     * @param score the score for the turn
     * @param click if the button was clicked
     * @param leftSide if the score is on the left side of the scoreboard
     */
    public void displayAnswer(int pos, int score, boolean click, boolean leftSide) {
        if (click) {
            players[whoseTurn].scoreObj[pos].value = score;
            scoreField[pos].setBackground(Color.WHITE);
            scoreField[pos].setText(" " + score);
            players[whoseTurn].scoreObj[pos].used = true;
            onTheLeft(score, leftSide);
            resetBoard();
        } else {
            if (score > 0) {
                scoreField[pos].setBackground(Color.LIGHT_GRAY);
                scoreField[pos].setText(" " + score);
            }
        }

        for (int i = 0; i < occurrence.length; i++) {
            occurrence[i] = 0;
        }
    }

    /**
     * Method that counts the occurrence of each die
     */
    public void diceOccurence() {
        //Find out how many of each die there are on screen
        for (int b = 1; b < 7; b++) { //Consider each possible number
            for (int a = 0; a < numDie; a++) { //Go through each die
                if (dice[a].face == b) {
                    occurrence[b - 1]++; //Increment occurrence counter
                }
            }
        }
    }

    /**
     * Method that resets the row of dice
     */
    public void resetDice() throws Exception {
        for (int i = 0; i < numDie; i++) {
            dice[i].keep = false;
            dice[i].face = 0;
            players[whoseTurn].rollCount = 3;
            rolledTimes.setText("Rolls Remaining: " + players[whoseTurn].rollCount);
            dice[i].repaint();
        }

        // one less turn
        players[whoseTurn].turnsRemaining--;

        numberOfTurns.setText("Turns Remaining: " + players[whoseTurn].turnsRemaining);

        // reset occurrence counter
        for (int i = 0; i < 6; i++) {
            occurrence[i] = 0;
        }

        int endgame = 0;

        if (players[players.length - 1].turnsRemaining == 0) {
            endgame = players.length;
        }

        /* End Game */
        if (endgame == players.length) {
            buildMusic(false);
            int win = whoWon();
            whoseTurn = win;

            /* High Score */
            readHighScore("highScore.dat");
            highScoreList.add(new HighScore(players[whoseTurn].name, players[win].scoreObj[16].value));
            String highScoreFile = highScore(highScoreList);
            writeHighScore("highScore.dat", highScoreFile);

            /* Shows Winner */
            if (players[win].scoreObj[16].value > topScore) {
                JOptionPane.showMessageDialog(null, players[whoseTurn].name + " is the winner!"
                        + "\n" + " Score of " + players[win].scoreObj[16].value + "\n" + "New High Score!");
            } else {
                JOptionPane.showMessageDialog(null, players[whoseTurn].name + " is the winner!"
                        + "\n" + " Score of " + players[win].scoreObj[16].value);
            }
            gamePanel.setBorder(new TitledBorder(players[whoseTurn].name));
            displayScores();

            music.music2();
            /* Replay */
            String[] choices = {"Yes", "No"};
            scoreField[scoreField.length - 1].setBackground(mainFrame.getBackground());
            playAgain = JOptionPane.showOptionDialog(null,
                    "Would you like to play again?", "Reset Game?",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, choices, choices[1]);
            if (playAgain == 0) {
                gameReset();
            } else {
                System.exit(0);
            }
        }

        /* switches players */
        int next = whoseTurn + 1;
        if (next >= players.length) {
            next = 0;
        }
        rollButton.setText("Next player: " + players[next].name);
        numberOfTurns.setText("Turns Remaining: " + players[next].turnsRemaining);
    }

    /**
     * Method that switches the possession of the dice
     */
    public void switchPlayers() {
        rollButton.setText("Roll!");
        whoseTurn++;
        if (whoseTurn >= players.length) {
            whoseTurn = 0;
        }
        gamePanel.setBorder(new TitledBorder(players[whoseTurn].name));
        displayScores();
    }

    /**
     * Method that determines if the score belongs on the left side of the board
     *
     * @param _score the score
     * @param _leftSide if on the left side
     */
    public void onTheLeft(int _score, boolean _leftSide) {
        if (_leftSide) {
            int leftsubTotal = players[whoseTurn].calculateSubtotal(_score, 13);
            scoreField[13].setText(" " + leftsubTotal);

            if (leftsubTotal > 63) {
                players[whoseTurn].scoreObj[14].value = 35;
            }

            scoreField[14].setText(" " + players[whoseTurn].scoreObj[14].value);
        } else {
            int rsub = players[whoseTurn].calculateSubtotal(_score, 15);
            scoreField[15].setText(" " + rsub);
        }

        scoreField[16].setText(" " + players[whoseTurn].grandTotal());
    }

    /**
     * Method that prints the score to the appropriate text field
     */
    public void displayScores() {
        for (int i = 0; i < scoreField.length; i++) {
            int val = players[whoseTurn].scoreObj[i].value;
            if (val == 0 && i < 13) {
                scoreField[i].setText(" ");
                if (players[whoseTurn].scoreObj[i].used) {
                    scoreField[i].setText(" " + players[whoseTurn].scoreObj[i].value);
                    scoreField[i].update(mainFrame.getGraphics());
                }
            } else {
                scoreField[i].setText(" " + players[whoseTurn].scoreObj[i].value);
                scoreField[i].update(mainFrame.getGraphics());
            }
        }
    }

    /**
     * Method that initializes the game
     */
    public void gameSetup() throws Exception {
        try {
            int numPlayers = Integer.parseInt(JOptionPane.showInputDialog(null,
                    "How many players?"));
            players = new Player[numPlayers];


            for (int i = 0; i < players.length; i++) {
                String name = JOptionPane.showInputDialog(null,
                        "Name of Player" + (i + 1) + ":");
                players[i] = new Player(name, scoreName);

                readHighScore("highScore.dat");
                String highScoreFile = highScore(highScoreList);
                writeHighScore("highScore.dat", highScoreFile);
                File highScoreData = new File("highScore.dat");
                Scanner scan = new Scanner(highScoreData);
                String highScoreName = scan.next();
                int highScore = scan.nextInt();
                JOptionPane.showMessageDialog(null, "Current High Score"
                        + "\n" + "Score of " + highScore);

                if (name == null) {
                    System.exit(0);
                }
            }

            dice = new YahtzeeDie[numDie];
            whoseTurn = 0;

            mainFrame.repaint();
        } catch (NumberFormatException e) {
            System.exit(0);
        }
    }

    /**
     * Method that resets the game board
     */
    public void resetBoard() {
        for (int y = 0; y < 13; y++) {
            if (players[whoseTurn].scoreObj[y].used != true) {
                players[whoseTurn].scoreObj[y].value = 0;
                scoreField[y].setBackground(Color.WHITE);
                scoreField[y].setText(" ");
            } else {
                scoreField[y].setText(" " + players[whoseTurn].scoreObj[y].value);
            }
        }
    }

    /**
     * Method that displays who won
     *
     * @return the player with the highest score
     */
    public int whoWon() {
        int maxVal, maxPlayer;
        maxVal = players[0].scoreObj[16].value;
        maxPlayer = 0;
        for (int i = 0; i < players.length; i++) {
            System.out.println(players[i].name + " has score of " + players[i].scoreObj[16].value);
            if (players[i].scoreObj[16].value > maxVal) {
                maxPlayer = i;
            }
        }

        return maxPlayer;
    }

    /**
     * Method that resets the game
     */
    public void gameReset() throws Exception {
        players = null;
        mainFrame = null;
        build();
    }

    /**
     * Reads the highScore file
     *
     * @param __file name of file being read
     * @return true if file was read successful
     * @throws Exception from Iterator
     */
    public Boolean readHighScore(String __file) throws Exception {
        Boolean _file = false;
        highScoreList = new LinkedList<HighScore>();
        try {
            File file = new File(__file);
            Scanner scan = new Scanner(file);

            while (scan.hasNext()) {

                String name = scan.next();
                int score = scan.nextInt();

                HighScore result = new HighScore(name, score);

                highScoreList.add(result);

                _file = true;
            }
        } catch (IOException e) {

            _file = false;
            return _file;
        }
        return _file;
    }

    /**
     * Writes a new highScore file with updated info
     *
     * @param __file file name
     * @param fileInfo file info
     */
    public void writeHighScore(String __file, String fileInfo) {
        try {
            String theFile;

            FileWriter _file = new FileWriter(__file);
            BufferedWriter file = new BufferedWriter(_file);

            theFile = fileInfo;

            file.write(theFile);

            file.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads through the highScore list
     *
     * @param highScore list of scores
     * @return a in order sorted String implementation of the file info
     */
    public String highScore(Queue<HighScore> highScore) {
        String result = "";
        int size = highScore.size();
        HighScore score;
        String scoreString;

        highscore = new HighScore[highScore.size()];

        Iterator<HighScore> iter1 = highScore.iterator();

        /* takes the info from the list into the array */
        for (int i = 0; i < highscore.length; i++) {
            highscore[i] = iter1.next();
        }

        // calls sort method
        Sort(highscore);

        /* Makes the string of info */
        for (int j = 0; j < highscore.length; j++) {
            score = null;
            score = highscore[j];

            scoreString = score.getName() + " " + score.getScore() + "\n";

            result += scoreString;
        }

        topScore = highscore[0].getScore();

        return result;
    }

    /**
     * Sorts the data in the hoghScore array
     *
     * @param x array of scores
     */
    public void Sort(HighScore[] x) {
        for (int i = 0; i < x.length - 1; i++) {
            for (int j = i + 1; j < x.length; j++) {
                int current = x[i].getScore();
                int next = x[j].getScore();

                if (current < next) {
                    //... Exchange elements
                    HighScore temp = x[j];
                    x[j] = x[i];
                    x[i] = temp;
                }
            }
        }
    }
}