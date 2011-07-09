#set term post eps enhanced color dashed
#set output 'movimento.eps'
set view map
set pm3d map

set yrange [-1000:9000] reverse
set xrange [-1000:9000]

unset key #horiz
#set key center outside top


#splot "datiMovimento.dat" with circles lc rgb "blue" fs transparent solid 0.15 noborder
#plot "datiMovimento.dat"with circles lt 3 fs transparent solid 0.5 noborder
#splot "datiMovimento.dat" with points palette pt 0#
set cbrange [0:3]
#pt 1 = +
#pt 2 = x
#pt 3 = asterisco
#pt 4 = quadrati

splot "FG1008653149_MM1008653149_Del1_FDS1_multiHop_3_M2MShareMovementReport.txt" with points palette pt 7


pause -1 "Hit any key to continue"
