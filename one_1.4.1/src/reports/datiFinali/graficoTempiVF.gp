set term post eps noenhanced color dashed
#set term post eps noenhanced monochrome dashed
set output 'tempi.eps'
set boxwidth 0.9 absolute
#set style fill   solid 1.00 border -1
#set style fill pattern 5 border
#set samples 11, 11

set style histogram clustered gap 2 title  offset character 0, 0, 0
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ylabel "Time (h)"
#set xlabel "Session number"
set yrange [0:168]

set key horiz
set key center outside top
set grid ytics

plot 'datiTempiVF.dat' using 2:xtic(1) ti col fc rgb "blue" fs pattern 2, '' u 3 ti col fc rgb "red" fs pattern 3, '' u 4 ti col fc rgb "dark-green" fs pattern 7
#plot 'datiTempiVF.dat' using 2:xtic(1) lc rgb red, '' u 3 lc rgb green, '' u 4 lc rgb blue

#pause -1 "Hit any key to continue"
