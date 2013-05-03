package itu.ejuuragr;

import competition.cig.robinbaumgarten.AStarAgent;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.ai.TimingAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.scenarios.Stats;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.utils.StatisticalSummary;

public class MiniStats2 {

    final static int numberOfTrials = 50;

    public static void main(String[] args) {
    	Stats.ARGUMENTS = args;
        Agent controller = AgentsPool.load (args[0]);
        final int startingSeed = Integer.parseInt (args[1]);
        doStats (controller, startingSeed);
        System.exit(0);

    }

    public static void doStats (Agent agent, int startingSeed) {
        TimingAgent controller = new TimingAgent (agent);
        EvaluationOptions options = new CmdLineOptions(new String[0]);

        options.setNumberOfTrials(1);
        options.setVisualization(false);
        options.setMaxFPS(true);
        System.out.println("Testing controller " + agent.getName() + " with starting seed " + startingSeed);
        
        StatisticalSummary ssScore = new StatisticalSummary ();
        StatisticalSummary ssTimeLeft = new StatisticalSummary ();
        for (int i = 0; i < numberOfTrials; i++)
        	testConfig (controller, options, 131626201 + i, 20, false, ssScore, ssTimeLeft);
        
        System.out.printf("Results: Score mean: %.2f SD: (%.2f), Time left on wins, mean: %.2f SD: (%.2f)\n", ssScore.mean(), ssScore.sd(), ssTimeLeft.mean(), ssTimeLeft.sd());
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
        	
        	System.out.printf("Score from seed %9d : %.1f - time left %3d %s\n", seed, result.computeDistancePassed(),result.timeLeft, (result.marioStatus == Mario.STATUS_WIN) ? "WIN (time recorded)" : "");
        
    }


}
