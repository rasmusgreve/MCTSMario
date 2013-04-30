@echo off
cd ./bin
echo Starting RandomSamplesLimit STATS

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 0 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 0.txt"
echo Completed test (1/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 1 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 1.txt"
echo Completed test (2/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 2 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 2.txt"
echo Completed test (3/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 4 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 4.txt"
echo Completed test (4/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 6 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 6.txt"
echo Completed test (5/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 8 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 8.txt"
echo Completed test (6/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 10 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 10.txt"
echo Completed test (7/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 12 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 12.txt"
echo Completed test (8/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 16 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 16.txt"
echo Completed test (9/10)

echo %DATE% %TIME%

echo Testing Random samples limit
java itu.ejuuragr.MiniStats2 itu.ejuuragr.UCT.EnhancementTester 0 1000000 0.5 0.25 0 0 0 0 0 0 >> "Enhancement RSL 1000000.txt"
echo Completed test (10/10)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause