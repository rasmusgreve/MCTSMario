@echo off
cd ./bin
echo Enhancements STATS

echo %DATE% %TIME%

echo Testing Limited Actions
java itu.ejuuragr.MiniStats itu.ejuuragr.highdk.LimitedActions2 0 4 0.1875 0 >> "LimitedActions2.txt"
echo Limited Actions done (1/5)

echo %DATE% %TIME%

echo Testing Hole Detection
java itu.ejuuragr.MiniStats itu.ejuuragr.highdk.HoleDetection 0 4 0.1875 0 >> "HoleDetection.txt"
echo Hole Detection done (2/5)

echo %DATE% %TIME%

echo Testing Macro Actions
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.MacroActions 0 4 0.1875 0 >> "MacroActions.txt"
echo Hole Detection done (3/5)

echo %DATE% %TIME%

echo Testing H. Partial Expansion
java itu.ejuuragr.MiniStats itu.ejuuragr.heuristicpartial.HeuristicPartialMCTS 0 4 0.1875 0 >> "HPartialExpansion.txt"
echo H. Partial Expansion done (4/5)

echo %DATE% %TIME%

echo Testing Checkpoints
java itu.ejuuragr.MiniStats itu.ejuuragr.checkpoints.CheckpointUCT 0 4 0.1875 0 >> "Checkpoints.txt"
echo Checkpoints done (5/5)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause