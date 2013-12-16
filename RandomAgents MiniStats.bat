@echo off
cd ./bin
echo Starting Random Agent STATS

echo %DATE% %TIME%

echo Testing EnhancementTester
java itu.ejuuragr.HardStats itu.ejuuragr.RandomMCTSAgent 0 6 0.25 0.125 1 0 1 1 1 1 >> "Random EnhancementTester.txt"
echo Completed test (1/2)

echo %DATE% %TIME%

echo Testing AStarAgent
java itu.ejuuragr.HardStats itu.ejuuragr.RandomAStarAgent 0 >> "Random AStarAgent.txt"
echo Completed test (2/2)

echo %DATE% %TIME%

echo ALL DONE
echo ALL DONE
echo ALL DONE
pause