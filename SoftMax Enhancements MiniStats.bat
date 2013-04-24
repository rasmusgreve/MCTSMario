@echo off
cd ./bin
echo Starting Softmax STATS

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0 1 0 0 0 0 0 >> "Enhancement Softmax 0.txt"
echo Completed test (1/7)

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0.125 1 0 0 0 0 0 >> "Enhancement Softmax 0.125.txt"
echo Completed test (2/7)

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0.25 1 0 0 0 0 0 >> "Enhancement Softmax 0.25.txt"
echo Completed test (3/7)

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0.375 1 0 0 0 0 0 >> "Enhancement Softmax 0.375.txt"
echo Completed test (4/7)

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0.5 1 0 0 0 0 0 >> "Enhancement Softmax 0.5.txt"
echo Completed test (5/7)

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 0.75 1 0 0 0 0 0 >> "Enhancement Softmax 0.75.txt"
echo Completed test (6/7)

echo %DATE% %TIME%

echo Testing Softmax
java itu.ejuuragr.MiniStats itu.ejuuragr.UCT.EnhancementTester 0 4 0.1875 1 1 0 0 0 0 0 >> "Enhancement Softmax 1.txt"
echo Completed test (7/7)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause