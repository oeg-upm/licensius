package oeg.odrlapi.validator;

import java.io.*;
import java.util.*;
 
/**
 * @author Aakash Hasija
 * @seeAlso http://www.geeksforgeeks.org/topological-sorting/
 * A Java program to print topological sorting of a DAG
 */
class PolicyGraph
{
    public int vertices;   // No. of vertices
    private LinkedList<Integer>[] adjacencies; // Adjacency List
    //Alternative r 
    //adjacency list
    //key denotes the node
    //value denotes the arraylist of all the adjacent nodes
    Map<Integer, ArrayList<Integer>> adj;    
    
    
        
    
    
    
    //Constructor
    PolicyGraph(int v)
    {
        vertices = v;
        adjacencies = new LinkedList[v];
        adj = new HashMap<Integer, ArrayList<Integer>> ();
        for (int i=0; i<v; ++i)
        {
            adjacencies[i] = new LinkedList();
            adj.put(i, new ArrayList<Integer>());
        }        
    }
 
    // Function to add an edge into the graph
    void addEdge(int v,int w) {
        adjacencies[v].add(w); 
        adj.get(v).add(w);
    }
 
    // A recursive function used by topologicalSort
    void topologicalSortUtil(int v, boolean visited[], Stack stack)
    {
        // Mark the current node as visited.
        visited[v] = true;
        Integer i;
 
        // Recur for all the vertices adjacent to this
        // vertex
        Iterator<Integer> it = adjacencies[v].iterator();
        while (it.hasNext())
        {
            i = it.next();
            if (!visited[i])
                topologicalSortUtil(i, visited, stack);
        }
 
        // Push current vertex to stack which stores result
        stack.push(new Integer(v));
    }
 
    // The function to do Topological Sort. It uses
    // recursive topologicalSortUtil()
    int [] topologicalSort()
    {
        Stack stack = new Stack();
        int resultado[] = new int[vertices];
 
        // Mark all the vertices as not visited
        boolean visited[] = new boolean[vertices];
        for (int i = 0; i < vertices; i++)
            visited[i] = false;
 
        // Call the recursive helper function to store
        // Topological Sort starting from all vertices
        // one by one
        for (int i = 0; i < vertices; i++)
            if (visited[i] == false)
                topologicalSortUtil(i, visited, stack);
 
        // Print contents of stack
        int cuenta=0;
        while (stack.empty()==false)
        {
            Integer elemento = (Integer)stack.pop();
            resultado[cuenta] = elemento;
      //      System.out.print(elemento + " ");
            cuenta++;
        }
    //    System.out.println();
        return resultado;
    }
    

    
    // Driver method
    public static void main(String args[])
    {
        // Create a graph given in the above diagram
        PolicyGraph g = new PolicyGraph(6);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(4, 3);
        g.addEdge(2,0);
        System.out.println(g);
        System.out.println(g.isCyclic());

        System.out.println("Topological sort");
        g.topologicalSort();
        
        System.out.println("\n"+g);
    }
	public boolean isCyclic()
	{
		//@visited array to keep track of visited nodes 
		boolean[] visited = new boolean[vertices];
		Arrays.fill(visited, false); 
		//@recStack recursion stack, to check which all nodes are present 
		//on the stack
		Set<Integer> recStack = new HashSet<Integer>();
//		System.out.println(vertices);
		//loop through all the vertices in the graph
		for(int i=0;i<vertices;i++)
		{
			if(helper(i, visited, recStack))
				return true;
		}
		return false;
	}
	//@v : current vertex
	public boolean helper(int v, boolean[] visited, Set<Integer> recStack)
	{
	//	System.out.println("v: "+v+" set: "+recStack);
		//if we have not visited the vertex v yet 
		//then visit the vertex v and all its adjacent vertices
		if(!visited[v])
		{
			//mark the vertex v to be visited
			visited[v] = true;
			//add v in the recursion stack
			recStack.add(v);
			//for all adjacent vertices 
			for(int i : adj.get(v))
			{
				//if the adjacent node is not visited yet
				if(!visited[i])
				{
					if(helper(i, visited, recStack))
						return true;
				}
				//if the node is already present on the recursion stack
				//then there is cycle add return true
				else if(recStack.contains(i))
					return true;
			}
		}
		//remove the node from the recursion stack
		recStack.remove(v);
		return false;
	}    
    public String toString()
    {
        String s = "";
        for(int i=0;i<adjacencies.length;i++)
            s+=adjacencies[i]+" ";
        return s;
    }
}
// This code is contributed by Aakash Hasija