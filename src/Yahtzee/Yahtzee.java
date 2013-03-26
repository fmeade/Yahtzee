package Yahtzee;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

/**
 *
 * @author forrest_meade
 */
public class Yahtzee extends JFrame {

    private JPanel dicePanel, rollPanel, gamePanel;
    private JPanel column1, column2;
    private JButton rollButton;
    private JButton numberOfTurns;
    private JButton rolledTimes;
    private JButton scoreButton[];
    private JTextField scoreField[];
    private JLabel blank;
    private YahtzeeDie[] dice;
    private Player[] players;
    private final String[] scoreName = {"Aces", "Twos", "Threes", "Fours", "Fives", "Sixes",
        "3 of a Kind", "4 of a Kind", "Full House",
        "Small Straight", "Large Straight", "Yahtzee!", "Chance",
        "Subtotal", "Bonus", "Subtotal", "Total Score"};
    private int[] occurrence = {0, 0, 0, 0, 0, 0};
    private int playAgain;
    private int whoseTurn;
    private final int numDie = 5;

    
    /**
     * 
     */
    public Yahtzee() {
        gameSetup();

        scoreButton = new JButton[scoreName.length];
        scoreField = new JTextField[scoreName.length];


        /* Begin building the playing board */
        setTitle("Yahtzee");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 140, 0));
        setSize(new Dimension(500, 500));
        setResizable(false);

        /* this panel displays the dice */
        dicePanel = new JPanel();
        dicePanel.setLayout(new GridLayout(1, 5));
        dicePanel.setBackground(getBackground());
        for (int i = 0; i < numDie; i++) {
            dice[i] = new YahtzeeDie(true);
            dicePanel.add(dice[i]);
        }
        add(dicePanel, BorderLayout.NORTH);

        /* this panel houses the score board */
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(1, 2));
        gamePanel.setBackground(getBackground());
        gamePanel.setBorder(new TitledBorder(players[whoseTurn].name));
        add(gamePanel, BorderLayout.CENTER);

        /* build the scoreboard with this method */
        buildPanel();

        /* this panel lets the user track their game */
        rollPanel = new JPanel();
        rollPanel.setLayout(new GridLayout(1, 3));
        rollPanel.setBackground(getBackground());
        rollPanel.setPreferredSize(new Dimension(500, 50));

        rollButton = new JButton("Roll!");
        rollButton.addActionListener(new ButtonAction());
        rollPanel.add(rollButton);

        rolledTimes = new JButton("Rolls Remaining: " + players[whoseTurn].rollCount);
        rolledTimes.setEnabled(false);
        rollPanel.add(rolledTimes);

        numberOfTurns = new JButton("Turns Remaining: " + players[whoseTurn].turnsRemaining);
        numberOfTurns.setEnabled(false);
        rollPanel.add(numberOfTurns);

        add(rollPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Sets up the scoreboard 
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
     * 
     * 
     * @param name
     * @param back
     * @param edit
     * @return 
     */
    public JButton makeButton(String name, JPanel back, boolean edit) {
        JButton jb = new JButton(name);
        jb.setEnabled(edit);
        jb.addActionListener(new ButtonAction());
        back.add(jb);
        jb.setEnabled(false);
        return jb;
    }

    /**
     * 
     * 
     * @param back
     * @return 
     */
    public JTextField makeField(JPanel back) {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(Color.white);
        back.add(tf);
        return tf;
    }

    /**
     * Activates and reads the button input for scoreboard and dice roll
     */
    private class ButtonAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //Roll dice button - validate and do!
            // if you click the roll button and your roll count and turns remaining aren't 0...
            if (e.getSource() == rollButton && players[whoseTurn].rollCount != 0 && players[whoseTurn].turnsRemaining != 0) {
                if (rollButton.getText().contains("!")) {
                    // roll every die
                    for (int i = 0; i < numDie; i++) {
                        if (players[whoseTurn].rollCount == 3) {
                            dice[i].keep = false;
                        }
                        dice[i].roll();
                        for (int k = 0; k < scoreButton.length; k++) {
                            scoreButton[k].setEnabled(true);
                        }
                    }
                    // reduce roll count afterwards
                    players[whoseTurn].rollCount--;

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
                    repaint();
                } else {
                    switchPlayers();
                }
            }

            for (int i = 0; i < 13; i++) {
                if (e.getSource() == scoreButton[i] && !players[whoseTurn].scoreObj[i].used) {
                    pickType(i, true);
                    displayScores();
                    gamePanel.validate();
                    resetDice();
                }
            }
        }
    }
 
    /**
     * 
     * 
     * @param arrayPos
     * @param click 
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
    }

    /**
     * 
     * 
     * @param face
     * @param click 
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
     * 
     * 
     * @param face
     * @param arrayPos
     * @param click 
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
     * 
     * 
     * @param arrayPos
     * @param click 
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
     * 
     * 
     * @param arrayPos
     * @param click 
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
     * 
     * 
     * @param arrayPos
     * @param click 
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
     * 
     * 
     * @return 
     */
    public int countAllDie() {
        int score = 0;
        for (int i = 0; i < numDie; i++) {
            score += dice[i].face;
        }
        return score;
    }

    /**
     * 
     * 
     * @param pos
     * @param score
     * @param click
     * @param leftSide 
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
     * 
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
     * 
     */
    public void resetDice() {
        for (int i = 0; i < numDie; i++) {
            dice[i].keep = true;
            dice[i].face = 0;
            players[whoseTurn].rollCount = 3;
            rolledTimes.setText("Rolls Remaining: " + players[whoseTurn].rollCount);
            dice[i].repaint();
        }

        players[whoseTurn].turnsRemaining--;
        numberOfTurns.setText("Turns Remaining: " + players[whoseTurn].turnsRemaining);

        // reset occurrence counter
        for (int i = 0; i < 6; i++) {
            occurrence[i] = 0;
        }

        int endgame = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].turnsRemaining == 0) {
                endgame++;
            }
        }

        if (endgame == players.length) {
            int winner = whoWon();
            whoseTurn = winner;
            JOptionPane.showMessageDialog(null, players[whoseTurn].name + " is the winner!");
            gamePanel.setBorder(new TitledBorder(players[whoseTurn].name));
            displayScores();

            String[] choices = {"Yes", "No"};
            scoreField[scoreField.length - 1].setBackground(getBackground());
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

        int next = whoseTurn + 1;
        if (next >= players.length) {
            next = 0;
        }
        rollButton.setText("Next player: " + players[next].name);
    }

    /**
     * 
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
     * 
     * 
     * @param _score
     * @param _leftSide 
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
     * 
     */
    public void displayScores() {
        for (int i = 0; i < scoreField.length; i++) {
            int val = players[whoseTurn].scoreObj[i].value;
            if (val == 0 && i < 13) {
                scoreField[i].setText(" ");
            } else {
                scoreField[i].setText(" " + players[whoseTurn].scoreObj[i].value);
                scoreField[i].update(getGraphics());
            }
        }
    }

    /**
     * 
     */
    public void gameSetup() {
        int numPlayers = Integer.parseInt(JOptionPane.showInputDialog(null,
                "How many players?"));
        players = new Player[numPlayers];

        for (int i = 0; i < players.length; i++) {
            String name = JOptionPane.showInputDialog(null,
                    "Name of Player" + (i + 1) + ":");
            players[i] = new Player(name, scoreName);
        }

        dice = new YahtzeeDie[numDie];
        whoseTurn = 0;

        repaint();
    }

    /**
     * 
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
     * 
     * 
     * @return 
     */
    public int whoWon() {
        int maxVal, maxPlayer;
        maxVal = players[0].scoreObj[16].value;
        maxPlayer = 0;
        for (int i = 1; i < players.length; i++) {
            System.out.println(players[i].name + " has score of " + players[i].scoreObj[16].value);
            if (players[i].scoreObj[16].value > maxVal) {
                maxPlayer = i;
            }
        }

        return maxPlayer;
    }

    /**
     * 
     */
    public void gameReset() {
        players = null;
        gameSetup();
    }

    /**
     * 
     */
    static void sleep() {
        long current = System.currentTimeMillis();
        int secondsToDelay = 5;
        long future = current * 1000;
        future *= secondsToDelay;
        while (System.currentTimeMillis() < future) {
        }
    }
}