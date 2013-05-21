package itu.ejuuragr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.sprites.Mario;

import itu.ejuuragr.UCT.UCTNode;

public class MCTSTools {
	//Specify which buttons are available for the algorithm to use
	//                                {LEFT, RIGHT, DOWN, JUMP, SPEED}
	public static boolean[] buttons = {true, true, false, true, true};
	//Specify the default state of a button if it isn't available for the algorithm.
	public static boolean[] defaultButtonState = {false, false, false, false, false};
	//Number of children each node is going to have 
	public static int NUM_CHILDREN;
	//A list of possible actions
	public static List<boolean[]> actions = new ArrayList<boolean[]>();
	static{
		buildActionsFromButtons();
	}
	
	//Turn debug messages and drawing of the search tree on or off
	public static final boolean DEBUG = true;
	
	/**
	 * Prints an observation (received from the game engine) to the console
	 */
	public static void printObservation(byte[][] observation)
	{
		System.out.println("-------------------------------------");
		for (int x = 0; x < observation.length; x++)
		{
			for (int y = 0; y < observation[x].length; y++)
			{
				if (x == 11 && y == 11)
					System.out.print(" M ");
				else if (observation[x][y] == 0)
					System.out.print("   ");
				else
					System.out.print(String.format("%2d ",observation[x][y]));
			}
			System.out.println();
		}
	}
	
	/**
	 * Print a message to the console if debug is on
	 */
	public static void print(String message)
	{
		if (DEBUG) System.out.println(message);
	}
	
	/**
	 * Specify the exact possible actions
	 */
	public static void setActions(boolean[][] actionsToAdd)
	{
		actions.clear();
		for (boolean[] a : actionsToAdd)
		{
			actions.add(a);
		}
		NUM_CHILDREN = actions.size();
	}
	
	/**
	 * Build a set of actions from the settings of pushable buttons and their default state
	 */
	public static void buildActionsFromButtons()
	{
		int numActions = 1;
		for (int i = 0; i < 5; i++)
			if (buttons[i])
				numActions <<= 1;
		
		actions.clear();
		
		for (int index = 0; index < numActions; index++)
		{
			boolean[] action = defaultButtonState.clone();
			
			int j = 0; //j: Moves through output button
			for (int i = 0; i < 5; i++) //i: Moves through input button
			{
				if (!buttons[i]) continue; //Don't change from default and don't increment value (j)
				action[i] = ((index & 1<<j) != 0); //Build the action based on bit pattern of the index
				j++;
			}
			actions.add(action);
		}
		NUM_CHILDREN = actions.size();
	}
	
	/**
	 * Get the index corresponding to a given action
	 * @param action The action to find the index for
	 * @return The corresponding index
	 */
	public static int actionToIndex(boolean[] action)
	{
		for (int i = 0; i < NUM_CHILDREN; i++)
		{
			if (Arrays.equals(actions.get(i),action))
				return i;
		}
		throw new IllegalArgumentException("The given action is invalid");
	}
	
	/**
	 * Get an actions corresponding to a given index
	 * @param index The index of the action to find
	 * @return The corresponding action
	 */
	public static boolean[] indexToAction(int index)
	{
		return actions.get(index);
	}
	
	/**
	 * Clone a simulation, advance a step on the clone and return it
	 */
	public static LevelScene advanceStepClone(LevelScene state, boolean[] action){
		return advanceStepClone(state, action, 1);
	}	
	
	/**
	 * Clone a simulation and advance a step on the clone a specific amount of repetitions
	 * @param state The simulation to clone
	 * @param action The action to simulate
	 * @param repetitions The number of repetitions to perform
	 * @return The resulting simulation state
	 */
	public static LevelScene advanceStepClone(LevelScene state, boolean[] action, int repetitions){
		try {
			
			LevelScene result = (LevelScene) state.clone();
			result.mario.setKeys(action);
			while (repetitions-- > 0)
				result.tick();
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	/**
	 * Return true if mario has shrunk from "old" to "cur"
	 */
	public static boolean marioShrunk(Mario old, Mario cur){
		int before = marioSize(old);
		int after = marioSize(cur);
		if(after < before) return true;
		return false;
	}
	
	/**
	 * Get the size of mario expressed as an int
	 * 2: Fire
	 * 1: Large, no fire
	 * 0: small
	 */
	public static int marioSize(Mario mario){
		if(mario.fire) return 2;
		if(mario.large) return 1;
		return 0;
	}
	
	/**
	 * Checks if Mario is in a gap.
	 * @author Robin Baumgarten
	 * @param levelScene The LevelScene that contains the Mario and Level to check.
	 * @return True if Mario is in a gap, else false.
	 */
	public static boolean isInGap(LevelScene levelScene){
    	return (levelScene.level.isGap[(int) (levelScene.mario.x/16)] &&
    			levelScene.mario.y > levelScene.level.gapHeight[(int) (levelScene.mario.x/16)]*16);
	}
	
	/**
	 * Generic tuple class for containing two objects
	 */
	public static class Tuple<T,V>{
		public T first;
		public V second;
		public Tuple(T first, V second){
			this.first = first;
			this.second = second;
		}
	}
	
	/**
	 * Get the direct distance of two 2D points
	 */
	public static double dist(float x1, float y1, float x2, float y2){
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	/********************
	 * XML Stuff
	 ********************/
	
	/**
	 * Output the tree under a node as XML
	 * @param filename Where to store the generated XML
	 */
	public static void outputTree(String filename, UCTNode node)
	{
		try {
			String xml = getXMLRepresentation(node);
			File f = new File(filename);
			FileWriter fw = new FileWriter(f);
			fw.write(xml);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert an action boolean array to a string suitable for XML
	 */
	public static String actionToXML(boolean[] act)
	{
		StringBuilder b = new StringBuilder("Move=\"");
		if (act == null || act.length < 5)
			b.append("Nothing");
		else
		{
			if (act[0]) b.append("Left ");
			if (act[1]) b.append("Right ");
			if (act[2]) b.append("Down ");
			if (act[3]) b.append("Jump ");
			if (act[4]) b.append("Speed ");
			if (!(act[0] || act[1] || act[2] || act[3] || act[4])) b.append("Nothing");
		}
		return b.append("\"").toString();
	}
	
	/**
	 * Get a XML representation of a node
	 * @param n The node to get the representation of
	 * @return A String containing the generated XML
	 */
	public static String getXMLRepresentation(UCTNode n)
	{
		StringBuilder b = new StringBuilder();
		getXMLRepresentation(n, b);
		return b.toString();
	}
	
	/**
	 * Get a XML representation of a node recursively
	 * @param n The node to get a representation of
	 * @param b The string builder where the XML representation is put
	 */
	private static void getXMLRepresentation(UCTNode n, StringBuilder b)
	{
		b.append("<Node " + actionToXML(n.action) + " " + String.format("Reward=\"%s\"",n.reward/n.visited) + ">");
		if (n.children != null)
			for (UCTNode c : n.children)
				if (c != null)
					getXMLRepresentation(c,b);		
		b.append("</Node>");
	}
	
	
}
