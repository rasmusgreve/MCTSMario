@echo off
cd ./bin
echo Starting Mixmax STATS

echo %DATE% %TIME%

echo Testing Mixmax
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.375 1 0 0 0 0 0 >> "Enhancement Mixmax 0.375.txt"
echo Completed test (4/7)

echo %DATE% %TIME%

echo Testing Mixmax
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.5 1 0 0 0 0 0 >> "Enhancement Mixmax 0.5.txt"
echo Completed test (5/7)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause