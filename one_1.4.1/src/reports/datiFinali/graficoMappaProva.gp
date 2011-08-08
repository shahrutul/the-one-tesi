set term post eps noenhanced color dashed

set view map
set style data linespoints
#set nocbtics
unset key
unset xtics
unset ytics
set ztics 10

set urange [ * : * ] noreverse nowriteback  # (currently [-5.00000:5.00000] )
set vrange [ * : * ] noreverse nowriteback  # (currently [-5.00000:5.00000] )


set bmargin at screen 0.03
set tmargin at screen 0.90

set yrange [20:730]
set xrange [0:750]
#set zrange [0:20]

set palette rgbformulae -7, 2, -7

set output 'mappe/controlSet.eps'
splot "multiHopPerc/controlSet.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha


set output 'mappe/M2MShare_1_hop_10perc.eps'
splot "multiHopPerc/M2MShare_1_hop_10perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_2_hop_10perc.eps'
splot "multiHopPerc/M2MShare_2_hop_10perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_3_hop_10perc.eps'
splot "multiHopPerc/M2MShare_3_hop_10perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_1_hop_25perc.eps'
splot "multiHopPerc/M2MShare_1_hop_25perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_2_hop_25perc.eps'
splot "multiHopPerc/M2MShare_2_hop_25perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_3_hop_25perc.eps'
splot "multiHopPerc/M2MShare_3_hop_25perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_1_hop_50perc.eps'
splot "multiHopPerc/M2MShare_1_hop_50perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_2_hop_50perc.eps'
splot "multiHopPerc/M2MShare_2_hop_50perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_3_hop_50perc.eps'
splot "multiHopPerc/M2MShare_3_hop_50perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_1_hop_75perc.eps'
splot "multiHopPerc/M2MShare_1_hop_75perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_2_hop_75perc.eps'
splot "multiHopPerc/M2MShare_2_hop_75perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_3_hop_75perc.eps'
splot "multiHopPerc/M2MShare_3_hop_75perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_1_hop_100perc.eps'
splot "multiHopPerc/M2MShare_1_hop_100perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_2_hop_100perc.eps'
splot "multiHopPerc/M2MShare_2_hop_100perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha

set output 'mappe/M2MShare_3_hop_100perc.eps'
splot "multiHopPerc/M2MShare_3_hop_100perc.dat" matrix with image, 'multiHopPerc/mappa.gif' binary filetype=gif origin=(10,35,0) dx=1 dy=1.06 with rgbalpha




#splot 'multiHopPerc/mappa.gif' binary filetype=gif with rgbalpha




#pause -1 "Hit return to continue"
