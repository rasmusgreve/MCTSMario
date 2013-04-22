@echo off
cd ./bin
echo Starting Combination ministats

REM class seed rsl cp USE_SOFTMAX USE_MACRO_ACTIONS USE_PARTIAL_EXPANSION USE_ROULETTE_WHEEL_SELECTION USE_HOLE_DETECTION USE_LIMITED_ACTIONS

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 0 0 0 0 0 >> "Enhancement Softmax.txt"
echo Completed test (1/19)

echo %DATE% %TIME%

echo Testing Macro
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 1 0 0 0 0 >> "Enhancement Macro.txt"
echo Completed test (2/19)

echo %DATE% %TIME%

echo Testing Partial
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 1 0 0 0 >> "Enhancement Partial.txt"
echo Completed test (3/19)

echo %DATE% %TIME%

echo Testing Roulette
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 0 1 0 0 >> "Enhancement Roulette.txt"
echo Completed test (4/19)

echo %DATE% %TIME%

echo Testing Hole
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 0 0 1 0 >> "Enhancement Hole.txt"
echo Completed test (5/19)

echo %DATE% %TIME%

echo Testing Limited
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 0 0 0 1 >> "Enhancement Limited.txt"
echo Completed test (6/19)

echo %DATE% %TIME%

echo Testing Partial + Roulette
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 1 1 0 0 >> "Enhancement Partial + Roulette.txt"
echo Completed test (7/19)

echo %DATE% %TIME%

echo Testing Limited + Macro
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 1 0 0 0 1 >> "Enhancement Limited + Macro.txt"
echo Completed test (8/19)

echo %DATE% %TIME%

echo Testing Softmax + Macro
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 1 0 0 0 0 >> "Enhancement Softmax + Macro.txt"
echo Completed test (9/19)

echo %DATE% %TIME%

echo Testing Softmax + Hole
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 0 0 0 1 0 >> "Enhancement Softmax + Hole.txt"
echo Completed test (10/19)

echo %DATE% %TIME%

echo Testing Limited + Hole
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 0 0 1 1 >> "Enhancement Limited + Hole.txt"
echo Completed test (11/19)

echo %DATE% %TIME%

echo Testing Partial + Roulette + Hole
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 1 1 1 0 >> "Enhancement Partial + Roulette + Hole.txt"
echo Completed test (12/19)

echo %DATE% %TIME%

echo Testing Softmax + Hole + Limited
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 0 0 0 1 1 >> "Enhancement Softmax + Hole + Limited.txt"
echo Completed test (13/19)

echo %DATE% %TIME%

echo Testing Macro + Hole + Limited
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 1 0 0 1 1 >> "Enhancement Macro + Hole + Limited.txt"
echo Completed test (14/19)

echo %DATE% %TIME%

echo Testing Roulette + Hole + Limited
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 0 1 1 1 >> "Enhancement Roulette + Hole + Limited.txt"
echo Completed test (15/19)

echo %DATE% %TIME%

echo Testing Partial + Roulette + Limited + Hole
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 0 1 1 1 1 >> "Enhancement Partial + Roulette + Limited + Hole.txt"
echo Completed test (16/19)

echo %DATE% %TIME%

echo Testing Softmax + Macro + Hole + Limited
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 1 0 0 1 1 >> "Enhancement Softmax + Macro + Hole + Limited.txt"
echo Completed test (17/19)

echo %DATE% %TIME%

echo Testing Softmax + Macro + Partial + Roulette
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 1 1 1 0 0 >> "Enhancement Softmax + Macro + Partial + Roulette.txt"
echo Completed test (18/19)

echo %DATE% %TIME%

echo Testing All enhancements
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 1 1 1 1 1 >> "Enhancement Softmax + Macro + Partial + Roulette + Hole + Limited (all).txt"
echo Completed test (19/19)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause