package itu.ejuuragr;

import java.util.*;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.sprites.Mario;

import itu.ejuuragr.UCT.UCTNode;

public class MCTSTools {

	//                                {LEFT, RIGHT, DOWN, JUMP, SPEED}
	public static boolean[] buttons = {true, true, true, true, true};
	public static boolean[] defaultButtonState = {false, false, false, false, false};
	public static int CHILDREN;
	public static List<boolean[]> actions = new ArrayList<boolean[]>();
	static{
		buildActionsFromButtons();
	}
	
	
	public static final boolean DEBUG = true;
	
	public static void print(String message)
	{
		if (DEBUG) System.out.println(message);
	}
	
	/**
	 * Set the possible actions
	 */
	public static void setActions(boolean[][] actionsToAdd)
	{
		actions.clear();
		for (boolean[] a : actionsToAdd)
		{
			actions.add(a);
		}
		CHILDREN = actions.size();
	}
	
	/**
	 * Build a set of actions from a setting of pushable buttons and their default state
	 * @param buttons Array of buttons, true if it is pushable
	 * @param defaultButtonState Array of buttons giving the default state (used if not pushable)
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
			
			int j = 0; //j: The value of the button
			for (int i = 0; i < 5; i++) //i: The position of the button
			{
				if (!buttons[i]) continue; //Don't change from default and don't increment value (j)
				action[i] = ((index & 1<<j) != 0); //Build the action based on bit pattern of the index
				j++;
			}
			actions.add(action);
		}
		CHILDREN = actions.size();
	}
	
	/**
	 * Get the index corresponding to a given action
	 * @param action The action to find the index for
	 * @return The corresponding index
	 */
	public static int actionToIndex(boolean[] action)
	{
		for (int i = 0; i < CHILDREN; i++)
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
	 * Clone a LevelScene, advance a step on the clone and return it
	 * @param state
	 * @param action
	 * @return
	 */
	public static LevelScene advanceStepClone(LevelScene state, boolean[] action){
		return advanceStepClone(state, action, 1);
	}	
	
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
	
	public static double marioShrunk(Mario old, Mario cur){
		int before = marioSize(old);
		int after = marioSize(cur);
		if(after < before) return 2.0;
		if(after > before) return 1/2.0;
		return 1.0;
	}
	
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
	
	public static class Tuple<T,V>{
		public T first;
		public V second;

		public Tuple(T first, V second){
			this.first = first;
			this.second = second;
		}
	}
	
	public static double dist(float x1, float y1, float x2, float y2){
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	/********************
	 * XML Stuff
	 ********************/
	
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
	
	public static String getXMLRepresentation(UCTNode n)
	{
		StringBuilder b = new StringBuilder();
		getXMLRepresentation(n, b);
		return b.toString();
	}
	
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
