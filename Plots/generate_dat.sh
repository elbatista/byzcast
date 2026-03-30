#!/bin/bash

# valores fixos
dests=(2 3 4)
clis=(15 30 75 150 300)
tams=(64 512 1024)

# limpa antigos (opcional)
rm -f D*_T*.dat

for dest in "${dests[@]}"; do
  for tam in "${tams[@]}"; do

    outfile="D${dest}_T${tam}.dat"

    for cli in "${clis[@]}"; do

      file="Dests:${dest},Lat:50,Time:60,Cli:${cli}.txt"
      echo $file
      # ignora se arquivo não existir
      [ ! -f "$file" ] && continue

      # pula header e processa
      while read codigo t lat tp || [ -n "$codigo" ]; do

      if [ "$t" = "$tam" ]; then
        lat=$(echo "$lat" | tr ',' '.')
        tp=$(echo "$tp" | tr ',' '.')

        echo "$codigo $cli $tp $lat" >> "$outfile"
      fi

    done < <(tail -n +2 "$file")

    done

  done
done

echo "Arquivos .dat gerados!"