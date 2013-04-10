@echo off
cd ./bin
echo Starting RandomSamplesLimit STATS

echo %DATE% %TIME%

echo Testing RandomSamplesLimit, RSL:0 cp:0.1875 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 0 0.1875 0 >> "RandomSamplesLimit 0 0.1875 0.txt"
echo RandomSamplesLimit done (1/7)

echo %DATE% %TIME%

echo Testing RandomSamplesLimit, RSL:1 cp:0.1875 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 1 0.1875 0 >> "RandomSamplesLimit 1 0.1875 0.txt"
echo RandomSamplesLimit done (2/7)

echo %DATE% %TIME%

echo Testing RandomSamplesLimit, RSL:2 cp:0.1875 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 2 0.1875 0 >> "RandomSamplesLimit 2 0.1875 0.txt"
echo RandomSamplesLimit done (3/7)

echo %DATE% %TIME%

echo Testing RandomSamplesLimit, RSL:4 cp:0.1875 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.1875 0 >> "RandomSamplesLimit 4 0.1875 0.txt"
echo RandomSamplesLimit done (4/7)

echo %DATE% %TIME%

echo Testing RandomSamplesLimit, RSL:8 cp:0.1875 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 8 0.1875 0 >> "RandomSamplesLimit 8 0.1875 0.txt"
echo RandomSamplesLimit done (5/7)

echo %DATE% %TIME%

echo Testing RandomSamplesLimit, RSL:16 cp:0.1875 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 16 0.1875 0 >> "RandomSamplesLimit 16 0.1875 0.txt"
echo RandomSamplesLimit done (6/7)

echo %DATE% %TIME%

echo Testing RandomSamplesLimit, RSL:1000000 cp:0.1875 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 1000000 0.1875 0 >> "RandomSamplesLimit 1000000 0.1875 0.txt"
echo RandomSamplesLimit done (7/7)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause