set terminal pngcairo size 900,650

set xlabel "Throughput (TP)" font "Arial,18"
set ylabel "Latência" font "Arial,18"

set grid

set key left top font "Arial,16"

set xtics font "Arial,16"
set ytics font "Arial,16"


set title sprintf("Dests=%s Tam=%s", ARG1, ARG2) font "Arial,20"
set output sprintf("grafico_D%s_T%s.png", ARG1, ARG2)

file = sprintf("D%s_T%s.dat", ARG1, ARG2)

Var_color    = "#27ae60"
Var1_color   = "#0b3c5d"
Origin_color = "#c0392b"

set style line 1 pt 7  ps 1.5
set style line 2 pt 5  ps 1.5
set style line 3 pt 9  ps 1.5
set style line 4 pt 11 ps 1.5
set style line 5 pt 13 ps 1.5
set style line 6 pt 3  ps 1.5
set style line 7 pt 1  ps 1.5

plot \
"< grep '^Var ' ".file using 3:4 with lines lw 2 lc rgb Var_color title "ByzcastVar", \
"< grep '^Var1' ".file using 3:4 with lines lw 2 lc rgb Var1_color title "ByzcastVarClient", \
"< grep '^Origin' ".file using 3:4 with lines lw 2 lc rgb Origin_color title "Original", \
"< grep '^Var 30 ' ".file using 3:4 with points ls 1 lc rgb Var_color notitle, \
"< grep '^Var 150 ' ".file using 3:4 with points ls 2 lc rgb Var_color notitle, \
"< grep '^Var 300 ' ".file using 3:4 with points ls 3 lc rgb Var_color notitle, \
"< grep '^Var 450 ' ".file using 3:4 with points ls 4 lc rgb Var_color notitle, \
"< grep '^Var 750 ' ".file using 3:4 with points ls 5 lc rgb Var_color notitle, \
"< grep '^Var 1050 ' ".file using 3:4 with points ls 6 lc rgb Var_color notitle, \
"< grep '^Var1 30 ' ".file using 3:4 with points ls 1 lc rgb Var1_color notitle, \
"< grep '^Var1 150 ' ".file using 3:4 with points ls 2 lc rgb Var1_color notitle, \
"< grep '^Var1 300 ' ".file using 3:4 with points ls 3 lc rgb Var1_color notitle, \
"< grep '^Var1 450 ' ".file using 3:4 with points ls 4 lc rgb Var1_color notitle, \
"< grep '^Var1 750 ' ".file using 3:4 with points ls 5 lc rgb Var1_color notitle, \
"< grep '^Var1 1050 ' ".file using 3:4 with points ls 6 lc rgb Var1_color notitle, \
"< grep '^Origin 30 ' ".file using 3:4 with points ls 1 lc rgb Origin_color notitle, \
"< grep '^Origin 150 ' ".file using 3:4 with points ls 2 lc rgb Origin_color notitle, \
"< grep '^Origin 300 ' ".file using 3:4 with points ls 3 lc rgb Origin_color notitle, \
"< grep '^Origin 450 ' ".file using 3:4 with points ls 4 lc rgb Origin_color notitle, \
"< grep '^Origin 750 ' ".file using 3:4 with points ls 5 lc rgb Origin_color notitle, \
"< grep '^Origin 1050 ' ".file using 3:4 with points ls 6 lc rgb Origin_color notitle