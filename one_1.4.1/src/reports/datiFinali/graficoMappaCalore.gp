# set terminal png transparent nocrop enhanced font arial 8 size 420,320 
# set output 'heatmaps.1.png'
unset key
set view map
set style data linespoints
set xtics border in scale 0,0 mirror norotate  offset character 0, 0, 0
set ytics border in scale 0,0 mirror norotate  offset character 0, 0, 0
set ztics border in scale 0,0 nomirror norotate  offset character 0, 0, 0
set nocbtics
set title "Heat Map generated from a file containing Z values only" 
set urange [ * : * ] noreverse nowriteback  # (currently [-5.00000:5.00000] )
set vrange [ * : * ] noreverse nowriteback  # (currently [-5.00000:5.00000] )
#set xrange [ -0 : 1000 ] noreverse nowriteback
#set yrange [ 0 : 800 ] noreverse nowriteback
set zrange [ * : * ] noreverse nowriteback  # (currently [0.00000:5.00000] )
set cblabel "Score" 
#set cbrange [ 0.00000 : 5.00000 ] noreverse nowriteback
set palette rgbformulae -7, 2, -7
#splot "multiHopPerc/FG901595110_MM901595110_Del1_FDS1_multiHop_3_M2MShareMapCoverageReport.txt" matrix with image

splot "multiHopPerc/M2MShare_1_hop.dat" matrix with image
pause -1 "Hit any key to continue"

splot "multiHopPerc/M2MShare_2_hop.dat" matrix with image
pause -1 "Hit any key to continue"

splot "multiHopPerc/M2MShare_3_hop.dat" matrix with image
pause -1 "Hit any key to continue"
