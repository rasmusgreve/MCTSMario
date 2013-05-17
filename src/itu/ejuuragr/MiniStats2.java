package itu.ejuuragr;

import itu.ejuuragr.UCT.EnhancementTester;
import java.io.FileWriter;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.ai.TimingAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.scenarios.Stats;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.utils.StatisticalSummary;
import java.io.*;

public class MiniStats2 {

    final static int numberOfTrials = 100;
    
    private static BufferedWriter out;

    public static void main(String[] args) {
    	Stats.ARGUMENTS = args;
        //Agent controller = AgentsPool.load (args[0]);
    	
    	EnhancementTester controller;
    	try{
    	out = new BufferedWriter(new FileWriter("Big Stats Mixmax Results.txt"));
    	}
    	catch(Exception e){}
        for (int i = 10; i < 32; i++)
        {
        	controller = new EnhancementTester();
        	controller.setSoftmax(	true			 );
        	controller.setMacro(	(i & (1<<0)) != 0);
        	controller.setPartial(	(i & (1<<1)) != 0);
        	controller.setRoulette(	(i & (1<<2)) != 0);
        	controller.setHole(		(i & (1<<3)) != 0);
        	controller.setLimited(	(i & (1<<4)) != 0);
        	doStats (controller);
        }
        try{
		out.flush();
		out.close();
    	}
    	catch(Exception e){}
        System.exit(0);

    }

    public static void print(String msg)
    {
    	System.out.println(msg);
    	try{out.write(msg + "\n");out.flush();} catch(Exception e){}
    }
    
    public static void doStats (Agent agent) {
        TimingAgent controller = new TimingAgent (agent);
        EvaluationOptions options = new CmdLineOptions(new String[0]);

        options.setNumberOfTrials(1);
        options.setVisualization(false);
        options.setMaxFPS(true);
        print("");
        print("Testing controller " + agent.getName());
        
        StatisticalSummary ssScore = new StatisticalSummary ();
        StatisticalSummary ssTimeLeft = new StatisticalSummary ();
        for (int i = 0; i < numberOfTrials; i++)
        	testConfig (controller, options, 131626201 + i, 20, false, ssScore, ssTimeLeft);

        print(String.format("Results: Score mean: %.2f SD: (%.2f), Time left on wins, mean: %.2f SD: (%.2f)", ssScore.mean(), ssScore.sd(), ssTimeLeft.mean(), ssTimeLeft.sd()));
    }

    public static void testConfig (TimingAgent controller, EvaluationOptions options, int seed, int level, boolean paused, StatisticalSummary ssScore, StatisticalSummary ssTimeLeft) {
        options.setLevelDifficulty(level);
        options.setPauseWorld(paused);
        options.setLevelRandSeed(seed);
        
        controller.reset();
        options.setAgent(controller);
        
        Evaluator evaluator = new Evaluator (options);
        EvaluationInfo result = evaluator.evaluate().get(0);

        ssScore.add (result.computeDistancePassed());
        
        if(result.marioStatus == Mario.STATUS_WIN)
        	ssTimeLeft.add(result.timeLeft);
        	
        print(String.format("Score from seed %9d : %.1f - time left %3d %s", seed, result.computeDistancePassed(),result.timeLeft, (result.marioStatus == Mario.STATUS_WIN) ? "WIN (time recorded)" : ""));
        
    }


}
