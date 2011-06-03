#set terminal png transparent nocrop enhanced font arial 8 size 420,320 
#set output 'histograms.3.png'
#set terminal jpeg medium
#set output "histo.jpg"

set style fill   solid 1.00 border -1
#set style histogram clustered gap 2 title  offset character 0, 0, 0
set style histogram gap 2
set yrange [0:1]

#set boxwidth 5 relative
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ytics 0.1

#set ylabel "Data (bytes)"
#set xlabel "Session number"

#plot 'dati.dat' using 3 t "Server", '' using 4 t "Client", '' using 5:xtic(1) t "Network"
#plot 'dati.dat' u 2 ti col, '' u 3 ti col, '' u 4 ti col
plot 'reports/datiDeleghe.dat' using 2:xtic(1) ti col, '' u 3 ti col

pause -1 "Hit any key to continue"