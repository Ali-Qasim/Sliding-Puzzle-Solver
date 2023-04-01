import java.util.ArrayList;

/*
      Instances of the class State represent states that can arise in the 8-puzzle problem.
      The int array 'puzzle' represents the puzzle configuration; that is, the location of each of the
      8 numbers. The empty location is denoted by integer 0.
      The int pos0 holds the position of the empty location.

      INIT and GOAL are constant arrays holding the initial and goal board configurations.
 */

public class State {
    final int[] puzzle;
    private int pos0;
    static final int[] INIT = {8, 7, 6, 5, 4, 3, 2, 1, 0};
    static final int[] GOAL = {1, 2, 3, 4, 5, 6, 7, 8, 0};

    /*
        State is a constructor that takes an int array holding a puzzle configuration as argument.
     */
    public State(int[] puzzle) {

        this.puzzle = puzzle;

        for (int i = 0; i < 9; i++) {

            if (puzzle[i] == 0) {

                this.pos0 = i;
                break;

            }
        }
    }

    /*
        clone returns a new State with the same puzzle configuration as the current State.
     */
    public State clone() {

        int[] puzzleCopy = new int[9];
        System.arraycopy(this.puzzle, 0, puzzleCopy, 0, 9);
        return new State(puzzleCopy);

    }

    /*
        toString returns the board configuration of the current GameState as a printable string.
    */
    public String toString() {
        String s = "";

        for (int c : this.puzzle) s = s + c;

        return s;
    }

    /*
        isGoal returns true if and only if the board configuration of the current GameState is the goal
        configuration.
    */

    public boolean isGoal() {

        for (int j = 0; j < 9; j++) {

            if (this.puzzle[j] != GOAL[j]) return false;

        }

        return true;
    }

    /*
         isIdentical returns true if and only if the State supplied as argument has the same puzzle
         configuration as the current State.
     */
    public boolean isIdentical(State s) {

        for (int j = 0; j < 9; j++) {

            if (this.puzzle[j] != s.puzzle[j]) return false;

        }

        return true;
    }

    /*
        possibleMoves returns a list of all States that can be reached in a single move from the current State.
     */
    public ArrayList<State> possibleMoves() {

        ArrayList<State> moves = new ArrayList<>();

        for (int position = 0; position < 9; position++) {

            if (position != this.pos0) {

                int distance = Math.abs(this.pos0 - position);
                if ((distance == 3 || distance == 1)) {

                    //diagChecks make sure that diagonal moves such as puzzle[5] to puzzle[6] are not possible
                    boolean diagCheck = (position+1)%3 == 0 && pos0==position+1;
                    boolean diagCheck2 = (pos0+1)%3 == 0 && position==pos0+1;

                    //continue if checks are false
                    if (!diagCheck && !diagCheck2) {

                        State newState = this.clone();

                        newState.puzzle[this.pos0] = this.puzzle[position];
                        newState.puzzle[position] = 0;
                        newState.pos0 = position;

                        moves.add(newState);
                    }
                }
            }
        }

        return moves;

    }
}
