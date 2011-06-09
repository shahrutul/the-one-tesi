#set terminal png transparent nocrop enhanced font arial 8 size 420,320 
#set output 'histograms.3.png'
#set terminal latex
#set output "histo.jpg"
#set term post eps noenhanced monochrome dashed
#set output 'reports/tempi.eps'
set boxwidth 0.9 absolute
#set style fill   solid 1.00 border -1
#set style fill pattern 4 border
#set samples 11, 11

set style histogram clustered gap 2 title  offset character 0, 0, 0
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ylabel "Time (s)"
#set xlabel "Session number"

plot 'reports/datiTempiVF.dat' using 2:xtic(1) ti col fs pattern 2, '' u 3 ti col fs pattern 3, '' u 4 ti col fs pattern 5
#plot 'reports/datiTempiVF.dat' using 2:xtic(1) lc rgb red, '' u 3 lc rgb green, '' u 4 lc rgb blue

pause -1 "Hit any key to continue"
