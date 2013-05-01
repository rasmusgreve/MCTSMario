@echo off
cd ./bin
echo Starting CP values STATS

echo %DATE% %TIME%

echo Testing CP 0
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0 0.25 0 0 0 0 0 0 >> "Enhancement CP 0.txt"
echo Completed test (1/9)

echo %DATE% %TIME%

echo Testing CP 0.1875
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.1875 0.25 0 0 0 0 0 0 >> "Enhancement CP 0.1875.txt"
echo Completed test (2/9)

echo %DATE% %TIME%

echo Testing CP 0.25
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.25 0.25 0 0 0 0 0 0 >> "Enhancement CP 0.25.txt"
echo Completed test (3/9)

echo %DATE% %TIME%

echo Testing CP 0.33333
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.33333 0.25 0 0 0 0 0 0 >> "Enhancement CP 0.33333.txt"
echo Completed test (4/9)

echo %DATE% %TIME%

echo Testing CP 0.5
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 0 0 0 0 >> "Enhancement CP 0.5.txt"
echo Completed test (5/9)

echo %DATE% %TIME%

echo Testing CP 0.70711
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.70711 0.25 0 0 0 0 0 0 >> "Enhancement CP 0.70711.txt"
echo Completed test (6/9)

echo %DATE% %TIME%

echo Testing CP 2
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 2 0.25 0 0 0 0 0 0 >> "Enhancement CP 2.txt"
echo Completed test (7/9)

echo %DATE% %TIME%

echo Testing CP 5
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 5 0.25 0 0 0 0 0 0 >> "Enhancement CP 5.txt"
echo Completed test (8/9)

echo %DATE% %TIME%

echo Testing CP 10
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 10 0.25 0 0 0 0 0 0 >> "Enhancement CP 10.txt"
echo Completed test (9/9)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause