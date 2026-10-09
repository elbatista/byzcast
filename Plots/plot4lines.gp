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

# Cores
LCA_color     = "#27ae60"
Par_color     = "#0b3c5d"
Seq_color     = "#8e44ad"
Origin_color  = "#c0392b"

set style line 1 pt 7  ps 1.5
set style line 2 pt 5  ps 1.5
set style line 3 pt 9  ps 1.5
set style line 4 pt 11 ps 1.5
set style line 5 pt 13 ps 1.5
set style line 6 pt 3  ps 1.5

# Linhas principais
set style line 10 lw 2 lc rgb LCA_color
set style line 11 lw 2 lc rgb Seq_color
set style line 12 lw 2 lc rgb Par_color
set style line 13 lw 2 lc rgb Origin_color


plot \
"< grep '^LCA ' ".file using 3:4 with lines lw 2 lc rgb LCA_color title "LCA", \
"< grep '^VarCliSeq' ".file using 3:4 with lines lw 2 lc rgb Seq_color title "VarCliSeq", \
"< grep '^VarCliPar' ".file using 3:4 with lines lw 2 lc rgb Par_color title "VarCliPar", \
"< grep '^Origin' ".file using 3:4 with lines lw 2 lc rgb Origin_color title "Original", \
"< grep '^LCA 15 ' ".file using 3:4 with points ls 1 lc rgb LCA_color notitle, \
"< grep '^LCA 30 ' ".file using 3:4 with points ls 2 lc rgb LCA_color notitle, \
"< grep '^LCA 150 ' ".file using 3:4 with points ls 3 lc rgb LCA_color notitle, \
"< grep '^LCA 300 ' ".file using 3:4 with points ls 4 lc rgb LCA_color notitle, \
"< grep '^LCA 450 ' ".file using 3:4 with points ls 5 lc rgb LCA_color notitle, \
"< grep '^VarCliSeq 15 ' ".file using 3:4 with points ls 1 lc rgb Seq_color notitle, \
"< grep '^VarCliSeq 30 ' ".file using 3:4 with points ls 2 lc rgb Seq_color notitle, \
"< grep '^VarCliSeq 150 ' ".file using 3:4 with points ls 3 lc rgb Seq_color notitle, \
"< grep '^VarCliSeq 300 ' ".file using 3:4 with points ls 4 lc rgb Seq_color notitle, \
"< grep '^VarCliSeq 450 ' ".file using 3:4 with points ls 5 lc rgb Seq_color notitle, \
"< grep '^VarCliSeq 750 ' ".file using 3:4 with points ls 6 lc rgb Seq_color notitle, \
"< grep '^VarCliSeq 1050 ' ".file using 3:4 with points ls 1 lc rgb Seq_color notitle, \
"< grep '^Origin 15 ' ".file using 3:4 with points ls 1 lc rgb Origin_color notitle, \
"< grep '^Origin 30 ' ".file using 3:4 with points ls 2 lc rgb Origin_color notitle, \
"< grep '^Origin 150 ' ".file using 3:4 with points ls 3 lc rgb Origin_color notitle, \
"< grep '^Origin 300 ' ".file using 3:4 with points ls 4 lc rgb Origin_color notitle, \
"< grep '^Origin 450 ' ".file using 3:4 with points ls 5 lc rgb Origin_color notitle, \
"< grep '^VarCliPar 15 ' ".file using 3:4 with points ls 1 lc rgb Par_color notitle, \
"< grep '^VarCliPar 30 ' ".file using 3:4 with points ls 2 lc rgb Par_color notitle, \
"< grep '^VarCliPar 150 ' ".file using 3:4 with points ls 3 lc rgb Par_color notitle, \
"< grep '^VarCliPar 300 ' ".file using 3:4 with points ls 4 lc rgb Par_color notitle, \
"< grep '^VarCliPar 450 ' ".file using 3:4 with points ls 5 lc rgb Par_color notitle