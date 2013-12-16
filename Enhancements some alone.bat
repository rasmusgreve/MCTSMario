@echo off
cd ./bin
echo Starting Combination MiniStats2

REM class seed rsl cp USE_SOFTMAX USE_MACRO_ACTIONS USE_PARTIAL_EXPANSION USE_ROULETTE_WHEEL_SELECTION USE_HOLE_DETECTION USE_LIMITED_ACTIONS

echo %DATE% %TIME%
echo Testing Best-NoDK
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 1 1 1 0 0 0 >> "Enhancement Best-NoDK.txt"
echo Completed test (1/3)

echo %DATE% %TIME%

echo Testing MixHP
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 1 0 1 1 0 0 >> "Enhancement MixHP.txt"
echo Completed test (2/3)

echo %DATE% %TIME%

echo Testing BestCombo
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 1 0 1 1 1 1 >> "Enhancement BestCombo.txt"
echo Completed test (3/3)

echo %DATE% %TIME%

echo ALL DONE
echo ALL DONE
echo ALL DONE
pause