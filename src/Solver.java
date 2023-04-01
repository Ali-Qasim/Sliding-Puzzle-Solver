import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*
   The Solver class contains the methods used to search for and print solutions
   plus the data structures needed for the search.
 */
public class Solver {

    ArrayList<Node> unexpanded = new ArrayList<>(); // Holds unexpanded node list
    ArrayList<Node> expanded = new ArrayList<>();   // Holds expanded node list
    Node rootNode;                                  // Node representing initial state


    /*
       Solver is a constructor that sets up an instance of the class with a node corresponding
       to the initial state as the root node.
     */
    public Solver(int[] initialBoard) {
        State initialState = new State(initialBoard);
        rootNode = new Node(initialState);
    }

    public void solve(PrintWriter output, int method) {

        unexpanded.add(rootNode);          // Initialise the unexpanded node list with the root node.

        if (method == 0) {

            System.out.println("Starting");

            while (unexpanded.size() > 0) {    // While there are nodes waiting to be expanded:

                Node n = unexpanded.get(0);    // Get the first item off the unexpanded node list
                expanded.add(n);               // Add it to the expanded node list
                unexpanded.remove(n);// Remove it from the unexpanded node list

                if (n.state.isGoal()) {        // If the node represents goal state then

                    reportSolution(n, output); // write solution to a file and terminate search
                    System.out.println("Complete");
                    return;

                } else {                       // Otherwise, (i.e. if the node does not represent goal state)

                    ArrayList<State> moveList = n.state.possibleMoves();      // Get list of permitted moves

                    for (State s : moveList) {                               // For each such move:

                        if ((Node.findNodeWithState(unexpanded, s) == null) && (Node.findNodeWithState(expanded, s) == null)) { // if not already on expanded list

                            int newCost = n.getCost() + 1;                        // add it to the unexpanded node list.
                            Node newNode = new Node(s, n, newCost);              // The parent is the current node.
                            unexpanded.add(newNode);

                        }
                    }
                }
            }
        } else if (method == 1) {

            System.out.println("Starting");

            while (unexpanded.size() > 0) {    // While there are nodes waiting to be expanded:

                int lowestCost = 10000;

                int nInd = 0;           //nInd stores index of the lowest cost node

                for (Node node : unexpanded) {          //loop to find the lowest cost node in list

                    if (node.state.isIdentical(rootNode.state)){

                        nInd = unexpanded.indexOf(rootNode);
                        break;

                    } else {

                        calcCost(node);

                        if (node.totalCost < lowestCost) {

                            lowestCost = node.totalCost;
                            nInd = unexpanded.indexOf(node);

                        }
                    }
                }

                Node n = unexpanded.get(nInd);    // Get the item with lowest g(n)+ h(n) off the unexpanded node list
                unexpanded.remove(unexpanded.get(nInd));          // Remove n from the unexpanded node list
                expanded.add(n);                // add n to expanded list

                if (n.state.isGoal()) {        // If the node represents goal state then

                    reportSolution(n, output); // write solution to a file
                    System.out.println("Complete");
                    return;

                } else {                       // Otherwise, (i.e. if the node does not represent goal state) then

                    ArrayList<State> moveList = n.state.possibleMoves();      // Get list of permitted moves

                    for (State s : moveList) {                               // For each such move:

                        if ((Node.findNodeWithState(expanded, s) == null)) { // if it is not already on expanded node list,

                            int newCost = n.getCost() + 1;                        // add it to the unexpanded node list.


                            Node f = Node.findNodeWithState(unexpanded, s);

                            Node newNode = new Node(s, n, newCost);              // The parent is the current node.
                            unexpanded.add(newNode);

                            calcCost(newNode);

                            try {

                                assert f != null;
                                calcCost(f);

                                if (f.getCost() > newNode.getCost()) { //if unexpanded contains costlier route, remove.

                                    unexpanded.remove(f);
                                }

                            } catch (Exception e){

                                System.out.println("oopsie");

                            }
                        }
                    }
                }
            }
        } else {

            System.out.println("\nInvalid entry.");

        }

        output.println("No solution found");
    }

    /*
       printSolution is a recursive method that prints all the states in a solution.
       It uses the parent links to trace from the goal to the initial state then prints
       each state as the recursion unwinds.
       Node n should be a node representing the goal state.
       The PrintWriter argument is used to specify where the output should be directed.
     */
    public void printSolution(Node n, PrintWriter output) {

        if (n.parent != null) printSolution(n.parent, output);

        output.println(n.state);
    }

    /*
       reportSolution prints the solution together with statistics on the number of moves
       and the number of expanded and unexpanded nodes.
       The Node argument n should be a node representing the goal state.
       The PrintWriter argument is used to specify where the output should be directed.
     */
    public void reportSolution(Node n, PrintWriter output) {

        output.println("Solution found!");
        printSolution(n, output);
        output.println(n.getCost() + " Moves");
        output.println("Nodes expanded: " + this.expanded.size());
        output.println("Nodes unexpanded: " + this.unexpanded.size());
        output.println();

    }

    /*
        calcCost calculates the g(n) + h(n) for A* search. h(n) is calculated using
        the sum of Manhattan distances between nodes
     */

    public int calcCost(Node node){

        int gon = node.getCost();
        int hon = 8;
        int cost = 0;

        for (int i = 0; i < 9; i++) {

            int goal = State.GOAL[i];
            int rowG = (int)Math.floor(i/3)+1;
            int colG = 3 - (rowG*3 - i);

            for (int j = 0; j < 9; j++) {

                int targ = node.state.puzzle[j];
                int rowT = (int)Math.floor(i/3)+1;
                int colT = 3 - (rowT*3 - i);
                
                if (goal == targ){

                    hon += Math.abs(rowG-rowT)+ Math.abs(colG-colT);

                }
            }
        }

        cost = gon + hon;
        node.totalCost = cost;

        return cost;
    }


    public static void main(String[] args) throws Exception {

        Solver problem = new Solver(State.INIT);  // Set up the problem to be solved.

        System.out.println("\nPlease enter '0' for Uniform Cost Search, or '1' for A* search: ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();

        int method = Integer.parseInt(input);
        File outFile = switch (method) {// Create a different file as the destination for output, based on which method was chosen

            case 0 -> new File("outputUniCost.txt");
            case 1 -> new File("outputAstar.txt");
            default -> new File("output.txt");

        };

        PrintWriter output = new PrintWriter(outFile);         // Create a PrintWriter for that file
        problem.solve(output, method);                                 // Search for and print the solution
        output.close();                                        // Close the PrintWriter (to ensure output is produced).
    }
}
