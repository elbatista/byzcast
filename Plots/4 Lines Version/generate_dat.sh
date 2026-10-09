#!/bin/bash

dests=(2 3 4)
clis=(30 150 300 450 750 1050)
tams=(64 512 1024)

rm -f D*_T*.dat

for dest in "${dests[@]}"; do
    for tam in "${tams[@]}"; do

        outfile="D${dest}_T${tam}.dat"
        echo "Gerando $outfile"

        for cli in "${clis[@]}"; do

            file="Dests:${dest},Lat:50,Time:60,Cli:${cli}.txt"

            echo "  Processando $file"

            if [ ! -f "$file" ]; then
                echo "    ARQUIVO NÃO EXISTE!"
                continue
            fi

            while read codigo t lat tp; do

                if [ "$t" = "$tam" ]; then
                    lat=$(echo "$lat" | tr ',' '.')
                    tp=$(echo "$tp" | tr ',' '.')

                    echo "$codigo $cli $tp $lat" >> "$outfile"
                fi

            done < "$file"

        done

        echo "  Linhas geradas: $(wc -l < "$outfile" 2>/dev/null || echo 0)"

    done
done

echo "Arquivos .dat gerados!"