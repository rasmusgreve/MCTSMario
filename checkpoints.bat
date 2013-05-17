@echo off
cd ./bin

echo Testing Checkpoints
java itu.ejuuragr.HardStats itu.ejuuragr.checkpoints.CheckpointUCT 0 >> "Enhancement Checkpoints.txt"
echo Completed test

echo %DATE% %TIME%

echo ALL DONE
echo ALL DONE
echo ALL DONE
pause