 set terminal pngcairo enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'VehvsTS.png'
set title "Incomping traffic to the Munich selected area"
set style fill solid
set ytics textcolor "red"
set ytics nomirror
set xtics 50 nomirror
set tics out
set yrange [0:12]
set xrange [0:316]
set xlabel "Time (second)" textcolor lt -1
set ylabel "Number of vehicles)"
set xtics ("0" 0, "100" 100, "200" 200, "300" 300)
plot "VehvsTS.dat" with lines notitle
set output