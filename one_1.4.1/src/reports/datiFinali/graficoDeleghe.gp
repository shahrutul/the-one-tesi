#set terminal png transparent nocrop enhanced font arial 8 size 420,320 
#set output 'histograms.3.png'
#set terminal jpeg medium
#set output "histo.jpg"
set term post eps noenhanced monochrome dashed
set output 'percDeleghe.eps'
set style fill pattern 4 border
#set style histogram clustered gap 2 title  offset character 0, 0, 0
set style histogram gap 2
set yrange [0:1]

#set boxwidth 5 relative
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ytics 0.1

set ylabel "Percent of returned delegated tasks"
#set xlabel "Session number"

set key horiz
set key center outside top
set grid ytics

#plot 'dati.dat' using 3 t "Server", '' using 4 t "Client", '' using 5:xtic(1) t "Network"
#plot 'dati.dat' u 2 ti col, '' u 3 ti col, '' u 4 ti col
plot 'datiDeleghe.dat' using 2:xtic(1) ti col fs pattern 3, '' u 3 ti col fs pattern 5

#pause -1 "Hit any key to continue"
