@echo off
cd ./bin
echo Starting Combination MiniStats2

REM class seed rsl cp USE_SOFTMAX USE_MACRO_ACTIONS USE_PARTIAL_EXPANSION USE_ROULETTE_WHEEL_SELECTION USE_HOLE_DETECTION USE_LIMITED_ACTIONS

echo %DATE% %TIME%
echo Testing Softmax
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 1 0 0 0 0 0 >> "Enhancement Softmax.txt"
echo Completed test (1/6)

echo %DATE% %TIME%

echo Testing Macro
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 0 1 0 0 0 0 >> "Enhancement Macro.txt"
echo Completed test (2/6)

echo %DATE% %TIME%

echo Testing Partial
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 0 0 1 0 0 0 >> "Enhancement Partial.txt"
echo Completed test (3/6)

echo %DATE% %TIME%

echo Testing Roulette
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 0 0 0 1 0 0 >> "Enhancement Roulette.txt"
echo Completed test (4/6)

echo %DATE% %TIME%

echo Testing Hole
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 0 0 0 0 1 0 >> "Enhancement Hole.txt"
echo Completed test (5/6)

echo %DATE% %TIME%

echo Testing Limited
java itu.ejuuragr.HardStats itu.ejuuragr.UCT.EnhancementTester 0 6 0.25 0.125 0 0 0 0 0 1 >> "Enhancement Limited.txt"
echo Completed test (6/6)

echo %DATE% %TIME%

echo ALL DONE
echo ALL DONE
echo ALL DONE
pause