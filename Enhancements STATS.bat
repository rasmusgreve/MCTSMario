@echo off
cd ./bin
echo Enhancements STATS

echo %DATE% %TIME%

echo Testing Limited Actions
java ch.idsia.scenarios.Stats itu.ejuuragr.highdk.LimitedActions2 0 4 0.1875 0 >> "LimitedActions2.txt"
echo Limited Actions done (1/4)

echo %DATE% %TIME%

echo Testing Hole Detection
java ch.idsia.scenarios.Stats itu.ejuuragr.highdk.HoleDetection 0 4 0.1875 0 >> "HoleDetection.txt"
echo Hole Detection done (2/4)

echo %DATE% %TIME%

echo Testing H. Partial Expansion
java ch.idsia.scenarios.Stats itu.ejuuragr.heuristicpartial.HeuristicPartialMCTS 0 4 0.1875 0 >> "HPartialExpansion.txt"
echo H. Partial Expansion done (3/4)

echo %DATE% %TIME%

echo Testing Checkpoints
java ch.idsia.scenarios.Stats itu.ejuuragr.checkpoints.CheckpointUCT 0 4 0.1875 0 >> "Checkpoints.txt"
echo Checkpoints done (4/4)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause