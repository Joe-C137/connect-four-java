
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

public class ConnectFour extends JFrame implements ActionListener {

	/**
	 * The number of rows for the board. It is set as a constant, but the programmer may change this value if a larger 
	 * or smaller board is desired at runtime. It is advised to keep {@code ROWS} set to 4 or greater, although it is 
	 * feasible to create a game of Connect Four so long as at least one of the dimensions is equal to or greater than four.
	 */	 
	private static final int ROWS = 6;

        /**
         * The number of columns for the board. It is set as a constant, but the programmer may change this value if a larger 
         * or smaller board is desired at runtime. It is advised to keep {@code COLUMNS} set to 4 or greater, although it is 
	 * feasible to create a game of Connect Four so long as at least one of the dimensions is equal to or greater than four.
         */
	private static final int COLUMNS = 7;

	/**
	 * The number of buttons that will be created for the board. This is derived from ROWS*COLUMNS and is a constant.
	 */
	private static final int btns = ROWS*COLUMNS;

        /**
         * The current turn being played. When {@code turn} is even, Red Player goes; else Yellow Player goes.
         */
	private static int turn;

        /**
         * The winner represented as a String, if the game has been won. Also holds "tie" if the game ends with no winner, and  
	 * the empty String if there is no win or tie condition.
         */
	private static String winner;

        /**
         * The default color of a JButton, stored here as {@code default} to make resetting the board easier.
         */
	private static Color defaultColor = new Color(238, 238, 238);

        /**
         * A 1-dimensional integer array that contains the next available JButton in that column that has not yet been changed 
	 * to red or yellow. Each element is initialized to {@code ROWS-1} in the {@code ConnectFour()} constructor.  
         */
	private static int[] bottomRow = new int[COLUMNS];

        /**
         * A 2-dimensional array containing the color of each JButton on the board, represented as a String. Initialized to null and 
	 * updated during runtime.
         */
	private static String[][] boardColors = new String[ROWS][COLUMNS];

        /**
         * The array of JButtons that the players interact with.
         */
	private static JButton[][] buttonArray = new JButton[ROWS][COLUMNS];


        /**
         * The constructor method for ConnectFour. Each element of {@code bottomRow} is initialized to ROWS-1, the title is set, the 
	 * size of the frame is set to 1200 x 1200, a GridLayout of {@code ROWS} rows and {@code COLUMNS} columns is created with no 
	 * horizontal or vertical gaps between elements, the frame is centered on the screen. {@code setDefaultCloseOperation(EXIT_ON_CLOSE)} 
	 * ensures that resrouces are release when the frame is closed by the player. The frame is made visible.
	 *<p>
	 * A for loop creates JButtons and places each inside {@code buttonArray}. Each JButton has a thin black border and an 
	 * ActionListener. Each JButton has a name composed of a number of empty Strings corresponding to the current column, which is 
	 * used later to identify which column has been clicked by the player. The JButtons are made visible and added to the layout.
         */ 
	public ConnectFour() {
		Arrays.fill(bottomRow, ROWS-1);			// Fill the static int array called bottomRow with ROWS-1

		setTitle("Connect Four!");			// Title of frame
		setSize(1200,1200);				// Set frame to 1200 x 1200 pixels
		setLayout(new GridLayout(ROWS,COLUMNS));	// Create ROWS rows and COLUMNS columns of JButtons with no gaps between them
		setLocationRelativeTo(null);			// Center the GUI window relative to screen

		for (int row = 0; row < ROWS; row++) {			// Create JButtons, store them in buttonArray
			for (int col = 0; col < COLUMNS; col++) {
				JButton theButton = new JButton();
				LineBorder thinBorder = new LineBorder(Color.BLACK, 1);
				theButton.setBorder(thinBorder);
				theButton.setBorderPainted(true); 

				String title = " ".repeat(col);
				theButton.setText(title);

				theButton.addActionListener(this);

				theButton.setOpaque(true);		// Make frame visible on macOS
				theButton.setContentAreaFilled(true);	//
			
				buttonArray[row][col] = theButton;
				
				add(theButton);
			}
		}

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

	}


        /**
         * ActionEvents are handled: {@code turn} is incremented, the appropriate square is colored when the player clicks on a column, 
	 * a message tells the player to pick another column if the current one is full (and {@code turn} is decremented to maintain 
	 * the current player's turn), the board is scanned after each move for a valid winning condition via {@code checkWinner()}, 
	 * the game is ended and the board is reset if there is a winner or a tie via {@code endGameUponCondition()}.
         */
	@Override
	public void actionPerformed(ActionEvent e) {
		turn++;
		String selectCol = e.getActionCommand();

/* Debug selectCol
		System.out.println(selectCol);
*/
		int colorCol = selectCol.length();
		int colorRow = bottomRow[colorCol];	

		if (colorRow > -1) {
			if (turn%2 == 0) {
				buttonArray[colorRow][colorCol].setBackground(Color.RED);
				boardColors[colorRow][colorCol] = "Red";
				bottomRow[colorCol]--;
			} else {
				buttonArray[colorRow][colorCol].setBackground(Color.YELLOW);
				boardColors[colorRow][colorCol] = "Yellow";
				bottomRow[colorCol]--;
			}
		} else {
			JOptionPane.showMessageDialog(null, "This column is full! Please pick another column.");
			turn--;
			return;
		}

/* Debug boardColors
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				System.out.print(boardColors[i][j]+ "\t");
			}
			System.out.println();
		}
		System.out.println();
*/

		winner = checkWinner();
		endGameUponCondition(winner);
	}


        /**
         * Checks the board for a winner: four consecutive squares of the same color in any direction. The winner is returned as a 
	 * String ("Red" or "Yellow"), a tie is returned as "tie", otherwise the empty String is returned.
         */
	public String checkWinner() {

                int sum = 0;
                for (int i = 0; i < COLUMNS; i++) {
                        sum += bottomRow[i];
                }
		if (sum == -1*COLUMNS) return "tie";

		for (int i = 0; i < 2; i++) {
			String checkWinner = "";
			if (i == 0) checkWinner = "Red";
			else checkWinner = "Yellow";
			int winFlag = 0;

			// Horizontal Check:
			for (int r = 0; r < ROWS && winFlag < 4; r++) {
				for (int c = 3; c < COLUMNS && winFlag < 4; c++) {
					winFlag = 0;
					for (int k = c-3; k <= c && winFlag < 4; k++) {
						if (boardColors[r][k] instanceof String && boardColors[r][k].equals(checkWinner)) winFlag++;
					}
				}
			}

                        // Vertical Check:
                        for (int r = 0; r < ROWS-3 && winFlag < 4; r++) {
                                for (int c = 0; c < COLUMNS && winFlag < 4; c++) {
                                        winFlag = 0;
                                        for (int k = r; k <= r+3 && winFlag < 4; k++) {
                                                if (boardColors[k][c] instanceof String && boardColors[k][c].equals(checkWinner)) winFlag++;
                                        }
                                }
                        }

			// Up-Left Diagonal Check:
                        for (int r = 3; r < ROWS && winFlag < 4; r++) {
                                for (int c = 3; c < COLUMNS && winFlag < 4; c++) {
                                        winFlag = 0;
                                        for (int j = r-3, k = c-3; j <= r && k <= c && winFlag < 4; j++, k++) {
                                                if (boardColors[j][k] instanceof String && boardColors[j][k].equals(checkWinner)) winFlag++;
                                        }
                                }
                        }

                        // Up-Right Diagonal Check:
                        for (int r = 3; r < ROWS && winFlag < 4; r++) {
                                for (int c = 0; c < COLUMNS-3 && winFlag < 4; c++) {
                                        winFlag = 0;
                                        for (int j = r-3, k = c+3; j <= r && k >= c && winFlag < 4; j++, k--) {
                                                if (boardColors[j][k] instanceof String && boardColors[j][k].equals(checkWinner)) winFlag++;
                                        }
                                }
                        }

			if (winFlag == 4) return checkWinner;
		}		
	
		return "";
	}
	

        /**
         * Receives {@code winner} and displays a message before resetting the board. {@code System.exit(0)} can be uncommented 
	 * so that the program simply terminates instead.
         */
	public void endGameUponCondition(String winner) {
   
		if (winner.equals("tie")) {
                        JOptionPane.showMessageDialog(null, "Tie game!");
                        //System.exit(0);
			resetBoard();
		}

                else if (winner.equals("Red")) {
                        JOptionPane.showMessageDialog(null, "Red Player Wins!");
                        //System.exit(0);
			resetBoard();
                }

                else if (winner.equals("Yellow")) {
                        JOptionPane.showMessageDialog(null, "Yellow Player Wins!");
                        //System.exit(0);
			resetBoard();
                }
               
	}


        /**
         * The board is reset: each element of {@code boardColors} is set to null, each JButton in {@code buttonArray} has its 
	 * color reset to {@code defaultColor}.
         */
	public void resetBoard() {
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLUMNS; c++) {
				boardColors[r][c] = null;
				buttonArray[r][c].setBackground(defaultColor);
			}
		}
		Arrays.fill(bottomRow, ROWS-1);
		turn = 0;
	}

	public static void main(String[] args) {
                new ConnectFour();
        }

}

