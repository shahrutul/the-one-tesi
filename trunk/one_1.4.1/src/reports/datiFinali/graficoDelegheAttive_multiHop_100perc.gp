set term post eps noenhanced color dashed
set output 'delegheAttive_multiHop_100perc.eps'

set boxwidth 0.9 absolute
set style fill pattern 4 border
#set style histogram clustered gap 2 title  offset character 0, 0, 0
set datafile missing '-'
set style data lines #histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ylabel "Nr. of active delegated tasks"
set xlabel "Time (h)"
set xtics 8

set yrange [0:750]

set key horiz
set key center outside top
set grid ytics

set style rect fc lt -1 fs solid 0.10 noborder
set obj rect from 0, graph 0 to 24, graph 1 behind
set obj rect from 48, graph 0 to 72, graph 1 behind
set obj rect from 96, graph 0 to 120, graph 1 behind
set obj rect from 144, graph 0 to 168, graph 1 behind

#plot 'datiRidondanzaDati.dat' using 2:xtic(5) ti col, '' u 3 ti col, '' u 4 ti col
plot 'datiDelegheAttiveMultiHop.dat' using 14 ti "M2MShare_1_hop" lw 3, '' u 15 ti "M2MShare_2_hop" ls 8 lc rgb "dark-green"  lw 3, '' u 16 ti "M2MShare_3_hop" lw 3

#pause -1 "Hit any key to continue"
