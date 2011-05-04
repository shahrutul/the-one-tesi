#NUMSEEDS=`wc --lines seeds.txt | egrep -o '[0-9]*'`
INDEX_FILE="`sed -n '1p' nextValues.txt`"
INDEX_MOVEMENT="`sed -n '2p' nextValues.txt`"
echo $INDEX_FILE
echo $INDEX_MOVEMENT

echo "FilesGenerator.rngSeed = ${i}" > seedSettings.txt;
echo "MovementModel.rngSeed = ${i}" >> seedSettings.txt;

INDEX_MOVEMENT=$INDEX_MOVEMENT+1
echo $INDEX_FILE > nextValues.txt;
echo $INDEX_MOVEMENT >> nextValues.txt;
