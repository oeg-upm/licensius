package oeg.odrlapi.validator;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;
 
/**
 *
 * @author vroddon
 */
public class GraphTraversal {
    
    private Stack<Integer> stack;
    private int adjacencyMatrix[][];
 
    public GraphTraversal() 
    {
        stack = new Stack<Integer>();
    }
 
    public void dfs(int adjacency_matrix[][], int source)
    {
        int number_of_nodes = adjacency_matrix[source].length - 1;
 
        adjacencyMatrix = new int[number_of_nodes + 1][number_of_nodes + 1];
        for (int sourcevertex = 1; sourcevertex <= number_of_nodes; sourcevertex++)
        {
            for (int destinationvertex = 1; destinationvertex <= number_of_nodes; destinationvertex++)
            {
                adjacencyMatrix[sourcevertex][destinationvertex] =
                   adjacency_matrix[sourcevertex][destinationvertex];
            }
        }
 
        int visited[] = new int[number_of_nodes + 1];		
        int element = source;		
        int destination = source;			
        visited[source] = 1;		
        stack.push(source);
 
        while (!stack.isEmpty())
        {
            element = stack.peek();
            destination = element;	
	    while (destination <= number_of_nodes)
	    {
                if (adjacencyMatrix[element][destination] == 1 && visited[destination] == 1)
                {
                    if (stack.contains(destination))
                    {	
                        System.out.println("The Graph contains cycle");
                        return;	
                    }
                }
 
              	if (adjacencyMatrix[element][destination] == 1 && visited[destination] == 0)
	        {
                    stack.push(destination);
                    visited[destination] = 1;
                    adjacencyMatrix[element][destination] = 0;
                    element = destination;
                    destination = 1;
	            continue;
                }
                destination++;
	    }
            stack.pop();	
        }	
    }
 
    public static void main(String...arg)
    {
        int number_of_nodes, source;
        Scanner scanner = null;
 	try
        {
	    System.out.println("Enter the number of nodes in the graph");
            scanner = new Scanner(System.in);
            number_of_nodes = scanner.nextInt();
 
	    int adjacency_matrix[][] = new int[number_of_nodes + 1][number_of_nodes + 1];
	    System.out.println("Enter the adjacency matrix");
	    for (int i = 1; i <= number_of_nodes; i++)
	        for (int j = 1; j <= number_of_nodes; j++)
                    adjacency_matrix[i][j] = scanner.nextInt();
 
            System.out.println("Enter the source for the graph");
            source = scanner.nextInt(); 
 
            GraphTraversal checkCycle = new GraphTraversal();
            checkCycle.dfs(adjacency_matrix, source);
 
        }catch(InputMismatchException inputMismatch)
        {
            System.out.println("Wrong Input format");
        } 	
        scanner.close();	
    }	
}