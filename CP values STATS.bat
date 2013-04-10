@echo off
cd ./bin
echo Starting CP values STATS

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:0 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0 0 >> "CPvalues 4 0 0.txt"
echo CP values done (1/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:0.1875 (1.5/8) Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.1875 0 >> "CPvalues 4 0.1875 0.txt"
echo CP values done (2/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:0.25 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.25 0 >> "CPvalues 4 0.25 0.txt"
echo CP values done (3/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:0.33333 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.33333 0 >> "CPvalues 4 0.33333 0.txt"
echo CP values done (4/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:0.5 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.5 0 >> "CPvalues 4 0.5 0.txt"
echo CP values done (5/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:0.70711 (1/sqrt(2)) Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 0.70711 0 >> "CPvalues 4 0.70711 0.txt"
echo CP values done (6/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:2 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 2 0 >> "CPvalues 4 2 0.txt"
echo CP values done (7/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:5 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 5 0 >> "CPvalues 4 5 0.txt"
echo CP values done (8/9)

echo %DATE% %TIME%

echo Testing CP values, RSL:4 cp:10 Q:0
java ch.idsia.scenarios.Stats itu.ejuuragr.softmax.SoftMaxMCTS 0 4 10 0 >> "CPvalues 4 10 0.txt"
echo CP values done (9/9)

echo %DATE% %TIME%
echo ALL DONE
echo ALL DONE
echo ALL DONE
pause