package itu.ejuuragr;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.ai.TimingAgent;
import ch.idsia.scenarios.Stats;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.utils.StatisticalSummary;

public class MiniStats2 {

    final static int numberOfTrials = 30;

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
        
        StatisticalSummary ss = new StatisticalSummary ();
        for (int i = 0; i < numberOfTrials; i++)
        	testConfig (controller, options, 131626201 + i, 20, false, ss);
        
        System.out.printf("Results: Mean: %.4f SD: (%.4f), (min %.4f max %.4f)\n", ss.mean(), ss.sd(), ss.min(), ss.max());
    }

    public static void testConfig (TimingAgent controller, EvaluationOptions options, int seed, int level, boolean paused, StatisticalSummary ss) {
        options.setLevelDifficulty(level);
        options.setPauseWorld(paused);
        options.setLevelRandSeed(seed);
        
        controller.reset();
        options.setAgent(controller);
        
        Evaluator evaluator = new Evaluator (options);
        EvaluationInfo result = evaluator.evaluate().get(0);
        ss.add (result.computeDistancePassed());
        
        System.out.printf("Score from seed %9d : %.1f\n", seed, result.computeDistancePassed());
        
    }


}
