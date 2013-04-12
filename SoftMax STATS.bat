@echo off
cd ./bin
echo Starting Softmax STATS

echo %DATE% %TIME%

echo Testing Softmax, RSL:4 cp:0.1875 Q:0
java itu.ejuuragr.MiniStats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.1875 0 >> "SoftMax 4 0.1875 0.txt"
echo Softmax done (1/5)

echo %DATE% %TIME%

echo Testing Softmax, RSL:4 cp:0.1875 Q:0.125
java itu.ejuuragr.MiniStats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.1875 0.125 >> "SoftMax 4 0.1875 0.125.txt"
echo Softmax done (2/5)

echo %DATE% %TIME%

echo Testing Softmax, RSL:4 cp:0.1875 Q:0.25
java itu.ejuuragr.MiniStats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.1875 0.25 >> "SoftMax 4 0.1875 0.25.txt"
echo Softmax done (3/5)

echo %DATE% %TIME%

echo Testing Softmax, RSL:4 cp:0.1875 Q:0.5
java itu.ejuuragr.MiniStats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.1875 0.5 >> "SoftMax 4 0.1875 0.5.txt"
echo Softmax done (4/5)

echo %DATE% %TIME%

echo Testing Softmax, RSL:4 cp:0.1875 Q:1
java itu.ejuuragr.MiniStats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.1875 1 >> "SoftMax 4 0.1875 1.txt"
echo Softmax done (5/5)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause