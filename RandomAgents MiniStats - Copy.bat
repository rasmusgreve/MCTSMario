@echo off
cd ./bin
echo Starting Random Agent STATS

echo %DATE% %TIME%

echo Testing AStarAgent
java itu.ejuuragr.HardStats itu.ejuuragr.RandomAStarAgent 0 >> "Random AStarAgent.txt"

echo Completed test (2/2)

echo %DATE% %TIME%

echo ALL DONE
echo ALL DONE
echo ALL DONE
pause