#set term post eps noenhanced monochrome dashed
set term post eps noenhanced color dashed
set output 'tempiVF_Fp50.eps'

#set boxwidth 0.9 absolute
#set style fill   solid 1.00 border -1
#set style fill pattern 3 border

set style line 1  linetype 2 linewidth 3.000 pointtype 1 pointsize default
set style line 2  linetype 2 linewidth 2.000 pointtype 2 pointsize default
set style line 3  linetype 2 linewidth 3.000 pointtype 3 pointsize default

set bmargin  7
set yrange [0:168]

#set style histogram clustered gap 2 title  offset character 0, 0, 0
set datafile missing '-'
set style data linespoints

set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ylabel "Time (h)"
set xlabel "Nodes"

set key horiz
set key center outside top
set grid ytics

plot 'datiTempiVF_Fp50'  using 2:xtic(1) ti col lc rgb "blue" lt 1  lw 3, '' u 3 ti col lc rgb "red" lt 2  lw 3, '' u 4 ti col  ls 8 lc rgb "dark-green" lt 3  lw 3

#pause -1 "Hit any key to continue"
