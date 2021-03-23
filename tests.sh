#!/bin/bash

for f in ./tests/*.lox; do
  echo "=================================================="
  echo "Running test for $f"
  "$1 $f"
  ts=$(date +%s%N)
  $@
  ms=$((($(date +%s%N) - $ts)/1000000))
    echo "Completed in $ms ms"
  echo "=================================================="
done

