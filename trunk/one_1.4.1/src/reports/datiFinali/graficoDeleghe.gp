#set terminal png transparent nocrop enhanced font arial 8 size 420,320 
#set output 'histograms.3.png'
#set terminal jpeg medium
#set output "histo.jpg"
set term post eps noenhanced color dashed
set output 'percDeleghe.eps'
set style fill pattern 4 border
#set style histogram clustered gap 2 title  offset character 0, 0, 0
set style histogram gap 2
set yrange [0:100]

#set boxwidth 5 relative
set datafile missing '-'
set style data histograms
set xtics border in scale 1,0.5 nomirror rotate by -45  offset character 0, 0, 0 
set ytics 10

set ylabel "Percentage of returned delegated tasks (%)"
#set xlabel "Session number"

set key horiz
set key center outside top
set grid ytics

plot 'datiDeleghe.dat' using 2:xtic(1) ti col fc rgb "red" fs pattern 3, '' u 3 ti col fc rgb "dark-green" fs pattern 7

#pause -1 "Hit any key to continue"
