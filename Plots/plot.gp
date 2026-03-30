set terminal pngcairo size 900,650
set xlabel "Throughput (TP)"
set ylabel "Latência"
set grid
set key left top

set title sprintf("Dests=%s Tam=%s", ARG1, ARG2)
set output sprintf("grafico_D%s_T%s.png", ARG1, ARG2)

file = sprintf("D%s_T%s.dat", ARG1, ARG2)

prot_color = "#0b3c5d"
orig_color = "#c0392b"

set style line 1 pt 7  ps 1.5
set style line 2 pt 5  ps 1.5
set style line 3 pt 9  ps 1.5
set style line 4 pt 11 ps 1.5
set style line 5 pt 13 ps 1.5

plot \
"< grep '^Prot' ".file." | sort -k3 -n" using 3:4 with lines lw 2 lc rgb prot_color title "Prot", \
"< grep '^Origin' ".file." | sort -k3 -n" using 3:4 with lines lw 2 lc rgb orig_color title "Origin", \
"< grep '^Prot 15 ' ".file using 3:4 with points ls 1 lc rgb prot_color notitle, \
"< grep '^Prot 30 ' ".file using 3:4 with points ls 2 lc rgb prot_color notitle, \
"< grep '^Prot 75 ' ".file using 3:4 with points ls 3 lc rgb prot_color notitle, \
"< grep '^Prot 150 ' ".file using 3:4 with points ls 4 lc rgb prot_color notitle, \
"< grep '^Prot 300 ' ".file using 3:4 with points ls 5 lc rgb prot_color notitle, \
"< grep '^Origin 15 ' ".file using 3:4 with points ls 1 lc rgb orig_color notitle, \
"< grep '^Origin 30 ' ".file using 3:4 with points ls 2 lc rgb orig_color notitle, \
"< grep '^Origin 75 ' ".file using 3:4 with points ls 3 lc rgb orig_color notitle, \
"< grep '^Origin 150 ' ".file using 3:4 with points ls 4 lc rgb orig_color notitle, \
"< grep '^Origin 300 ' ".file using 3:4 with points ls 5 lc rgb orig_color notitle