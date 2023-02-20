set terminal pngcairo enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'UnsortedVehPerAP.png'
set key fixed right top vertical Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid
set title "Total number of vehicles connected to every edge node"
#set datafile sep' '
#set key autotitle columnhead
set boxwidth 0.5
set style fill solid
set ytics textcolor "red"
set ytics nomirror
set yrange [0:300]
set xrange [-1:10]
set xtics ("0" 0, "1" 1)
set xlabel "Edge node" textcolor lt -1
set ylabel "Number of vehicles"
plot "UnsortedVehPerAP.dat" using 1:2 with boxes title "Number of vehicles"