@echo off
cd ./bin
echo Starting Combination MiniStats2

REM class seed rsl cp USE_SOFTMAX USE_MACRO_ACTIONS USE_PARTIAL_EXPANSION USE_ROULETTE_WHEEL_SELECTION USE_HOLE_DETECTION USE_LIMITED_ACTIONS

echo %DATE% %TIME%
REM 4 = 8, MiniStats22 0.1875 = 0.5
echo Testing Softmax
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 0 0 0 0 0 >> "Enhancement Softmax.txt"
echo Completed test (1/20)

echo %DATE% %TIME%

echo Testing Macro
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 1 0 0 0 0 >> "Enhancement Macro.txt"
echo Completed test (2/20)

echo %DATE% %TIME%

echo Testing Partial
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 1 0 0 0 >> "Enhancement Partial.txt"
echo Completed test (3/20)

echo %DATE% %TIME%

echo Testing Roulette
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 0 1 0 0 >> "Enhancement Roulette.txt"
echo Completed test (4/20)

echo %DATE% %TIME%

echo Testing Hole
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 0 0 1 0 >> "Enhancement Hole.txt"
echo Completed test (5/20)

echo %DATE% %TIME%

echo Testing Limited
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 0 0 0 1 >> "Enhancement Limited.txt"
echo Completed test (6/20)

echo %DATE% %TIME%

echo Testing Partial + Roulette
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 1 1 0 0 >> "Enhancement Partial + Roulette.txt"
echo Completed test (7/20)

echo %DATE% %TIME%

echo Testing Limited + Macro
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 1 0 0 0 1 >> "Enhancement Limited + Macro.txt"
echo Completed test (8/20)

echo %DATE% %TIME%

echo Testing Softmax + Macro
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 1 0 0 0 0 >> "Enhancement Softmax + Macro.txt"
echo Completed test (9/20)

echo %DATE% %TIME%

echo Testing Softmax + Hole
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 0 0 0 1 0 >> "Enhancement Softmax + Hole.txt"
echo Completed test (10/20)

echo %DATE% %TIME%

echo Testing Limited + Hole
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 0 0 1 1 >> "Enhancement Limited + Hole.txt"
echo Completed test (11/20)

echo %DATE% %TIME%

echo Testing Partial + Roulette + Hole
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 1 1 1 0 >> "Enhancement Partial + Roulette + Hole.txt"
echo Completed test (12/20)

echo %DATE% %TIME%

echo Testing Softmax + Hole + Limited
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 0 0 0 1 1 >> "Enhancement Softmax + Hole + Limited.txt"
echo Completed test (13/20)

echo %DATE% %TIME%

echo Testing Macro + Hole + Limited
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 1 0 0 1 1 >> "Enhancement Macro + Hole + Limited.txt"
echo Completed test (14/20)

echo %DATE% %TIME%

echo Testing Roulette + Hole + Limited
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 0 1 1 1 >> "Enhancement Roulette + Hole + Limited.txt"
echo Completed test (15/20)

echo %DATE% %TIME%

echo Testing Partial + Roulette + Limited + Hole
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 1 1 1 1 >> "Enhancement Partial + Roulette + Limited + Hole.txt"
echo Completed test (16/20)

echo %DATE% %TIME%

echo Testing Softmax + Macro + Hole + Limited
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 1 0 0 1 1 >> "Enhancement Softmax + Macro + Hole + Limited.txt"
echo Completed test (17/20)

echo %DATE% %TIME%

echo Testing Softmax + Macro + Partial + Roulette
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 1 1 1 0 0 >> "Enhancement Softmax + Macro + Partial + Roulette.txt"
echo Completed test (18/20)

echo %DATE% %TIME%

echo Testing Softmax + Macro + Roulette + Hole + Limited
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 1 0 1 1 1 >> "Enhancement Softmax + Macro + Roulette + Hole + Limited.txt"
echo Completed test (19/20)

echo %DATE% %TIME%

echo Testing All enhancements
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 1 1 1 1 1 1 >> "Enhancement Softmax + Macro + Partial + Roulette + Hole + Limited (all).txt"
echo Completed test (20/20)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause