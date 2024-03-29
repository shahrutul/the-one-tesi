#set terminal png transparent nocrop enhanced font arial 8 size 420,320 
#set output 'histograms.3.png'
#set terminal jpeg medium
#set output "histo.jpg"
set term post eps noenhanced color dashed
set output 'data.eps'
set boxwidth 0.9 absolute
#set style fill pattern 5 border
set style histogram clustered gap 2 title  offset character 0, 0, 0
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ylabel "Data (MB)"
#set xlabel "Session number"
set key horiz
set key center outside top
set grid ytics

#plot 'dati.dat' using 3 t "Server", '' using 4 t "Client", '' using 5:xtic(1) t "Network"
#plot 'dati.dat' u 2 ti col, '' u 3 ti col, '' u 4 ti col
plot 'datiTotalData.dat' using 2:xtic(1) ti col fc rgb "red" fs pattern 3, '' u 3 ti col fc rgb "dark-green" fs pattern 7

#pause -1 "Hit any key to continue"
