set term post eps noenhanced color dashed
#set term post eps noenhanced monochrome dashed
set output 'tempiVF_MultiHop.eps'
set style fill pattern 4 border
#set style histogram clustered gap 2 title  offset character 0, 0, 0
set style histogram gap 2
set yrange [0:168]

#set boxwidth 5 relative
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ytics 10

set ylabel "Time (h)"
set xlabel "Multi-hop delegation probability (MhDP)"

set key horiz
set key center outside top
set grid ytics

plot 'datiTempiMultiHopPerc.dat' using 2:xtic(1) ti col fs pattern 3, '' u 3 ti col fc rgb "dark-green" fs pattern 5, '' u 4 ti col fs pattern 6


#pause -1 "Hit any key to continue"
