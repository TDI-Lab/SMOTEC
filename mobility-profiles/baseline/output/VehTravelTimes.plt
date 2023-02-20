set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'VehTravelTimes.png'
#set key fixed right top vertical Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid
set title "Travel time of the vehicles in the Munich selected area"
#set datafile sep','
#set boxwidth 0.5
set style fill solid
set ytics textcolor "red"
set ytics nomirror
set xtics 50 nomirror
set tics out
set yrange [0:320]
set xrange [0:9]
set xlabel "Vehicle id (sorted based on the entrance time to the area)" textcolor lt -1
set ylabel "Vehicle entrance-exit (second)"
set xtics ("0" 0, "5" 5, "10" 10)
plot "VehTravels.dat" using 1:3 with lines notitle,"VehTravels.dat" using 1:4 with lines notitle
#plot "VehTravels.dat" using ($0 + 1):3 with lines notitle,"VehTravels.dat" using ($0 + 1):2 with lines notitle,
set output