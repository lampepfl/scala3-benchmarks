#!/bin/sh
# Benchmarks all minor Scala 3 versions starting with 3.3 for a given benchmark, passed as $1

if [ $# -ne 1 ]; then
  echo "Usage: $0 <benchmark>" >&2
  exit 1
fi

if [ ! -d 'bench' ]; then
  echo 'Run this inside the benchmarks repo' >&2
  exit 2
fi

benchmark="$1"
results_file='results.csv'

# Not doing: '3.0.2' '3.1.3' '3.2.2'
# The benchmarks fail to compile in all kinds of ways with those

printf '%-10s%-10s%-20s%s\n' 'Version,' 'Score,' 'Error (99.9%),' 'Unit'
for ver in '3.3.8' '3.4.3' '3.5.2' '3.6.4' '3.7.4' '3.8.4' '3.9.0'; do
  sbt -Dcompiler.version="$ver" "clean; bench / Jmh / run -gc true -foe true -rf csv -rff $results_file $benchmark" 1>bench.log 2>bench.log
  # "Benchmark","Mode","Threads","Samples","Score","Score Error (99.9%)","Unit"
  score="$(cut -d ',' -f 5 "bench/$results_file" | tail -n 1)"
  score_error="$(cut -d ',' -f 6 "bench/$results_file" | tail -n 1)"
  unit="$(cut -d ',' -f 7 "bench/$results_file" | tail -n 1)"
  rm "bench/$results_file"
  printf '%-10s%-10s%-20s%s\n' "$ver," "$score," "$score_error," "$unit"
done
