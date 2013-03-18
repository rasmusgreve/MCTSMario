package itu.ejuuragr;

import java.util.*;

import competition.cig.robinbaumgarten.astar.LevelScene;
import competition.cig.robinbaumgarten.astar.sprites.Mario;

import itu.ejuuragr.UCT.UCTNode;

public class MCTSTools {

	//                                {LEFT, RIGHT, DOWN, JUMP, SPEED}
	public static boolean[] buttons = {true, true, false, true, true};
	public static boolean[] defBtns = {false, false, false, false, false};
	public static int CHILDREN = possibleActionsCount();
	
	public static List<boolean[]> actions = new LinkedList<boolean[]>();
	static{
		actions.add(new boolean[]{false, true, false, true, true});
		actions.add(new boolean[]{false, true, false, true, false});
		actions.add(new boolean[]{false, true, false, false, true});
		actions.add(new boolean[]{false, false, false, true, true});
		actions.add(new boolean[]{false, false, false, false, true});
		actions.add(new boolean[]{false, true, false, false, false});
		actions.add(new boolean[]{true, false, false, true, false});
		actions.add(new boolean[]{true, false, false, false, false});
	}
	
	public static final boolean DEBUG = !false;
	
	public static void print(String message)
	{
		if (DEBUG) System.out.println(message);
	}
	
	public static int possibleActionsCount()
	{
		int acts = 1;
		for (int i = 0; i < 5; i++)
			if (buttons[i])
				acts <<= 1;
		return acts;
	}
	
	public static int actionToIndex(boolean[] action)
	{
		int index = 0;
		int j = 0;
		for (int i = 0; i < 5; i++)
		{
			if (!buttons[i]) continue;
			if (action[i])
				index += 1<<j;
			j++;
		}
		
		return index;
	}
	
	public static boolean[] indexToAction(int index)
	{
		boolean[] action = defBtns.clone();
		
		int j = 0;
		for (int i = 0; i < 5; i++)
		{
			if (!buttons[i]) continue;
			action[i] = ((index & 1<<j) != 0);
			j++;
		}
		
		return action;
	}
	
	public static LevelScene advanceStepClone(LevelScene state, boolean[] action){
		try {
			
			LevelScene result = (LevelScene) state.clone();
			result.mario.setKeys(action);
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
