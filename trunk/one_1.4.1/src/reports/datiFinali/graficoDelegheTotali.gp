set term post eps noenhanced monochrome dashed
set output 'delegheFatte.eps'

set boxwidth 0.9 absolute
#set style fill pattern 4 border
set style histogram clustered gap 2 title  offset character 0, 0, 0
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ylabel "Nr. of delegated tasks"
#set xlabel "Session number"

set key horiz
set key center outside top
set grid ytics

plot 'datiDelegheTotali.dat' using 2:xtic(1) ti col fs pattern 3, '' u 3 ti col fs pattern 5

#pause -1 "Hit any key to continue"
