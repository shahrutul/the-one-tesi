#set term post eps noenhanced monochrome dashed
set term post eps noenhanced color dashed

set output 'dataDFS_10MB.eps'
set boxwidth 0.9 absolute
#set style fill   solid 1.00 border -1
#set style fill pattern 4 border
#set samples 11, 11

set style histogram clustered gap 2 title  offset character 0, 0, 0
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ylabel "Total Data (MB)"
#set xlabel "Session number"

set yrange [0:7100]

set key horiz
set key center outside top
set grid ytics

plot 'datiDataDiversaFDS_10MB.dat'  using 3:xtic(1) ti col fs pattern 3, '' u 2 ti col fc rgb "dark-green" fs pattern 2, '' u 4 ti col fs pattern 7 lw 4


#pause -1 "Hit any key to continue"
