set terminal pngcairo enhanced font "arial,10" fontscale 1.0 size 400, 200 
set output 'VehPerAPPerTS-line.png'
set title "Number of connected vehicles to each edge node"
set xrange[0:316] 
set yrange[0:10]
set tics out
set tics nomirror
set palette rgb 33,13,10
set xlabel "Time (second)" textcolor lt -1
set ylabel "Number of vehicles
#set palette model CMY rgbformulae 7,5,15
#set cbrange [0:68]
plot "UnsortedVehPerAPPerTS.dat" using 1:3 with lines notitle,"UnsortedVehPerAPPerTS.dat" using 1:2 with lines notitle
set output