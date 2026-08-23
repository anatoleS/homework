#!/usr/bin/env bash
# тест оптимального размера хипа CalcDemo (G1GC).
# Хипы 32..2048 Мб шагом 64, по 3 прогонов.
# Метрика: "spend msec" из лога классов по заданю (не оптимизированы, перенесены как есть)
# Результат: сводка с медианами.

set -u

DIR="$(cd "$(dirname "$0")/.." && pwd)"
JAR="${JAR:-$DIR/../build/libs/homework-1.0-SNAPSHOT-fat.jar}"
RESULTS="${RESULTS:-$DIR/results}"
RUNS=3
HEAP_START=32
HEAP_END=2048
HEAP_STEP=64
RESULT_FILE="$RESULTS/sweep.csv"
LOG_DIR="$RESULTS/logs"

[ -f "$JAR" ] || { echo "Нет fat-jar: $JAR — сначала собрать: gradle shadowJar" >&2; exit 1; }
mkdir -p "$LOG_DIR"
echo "heap_mb,run,spend_msec,status" > "$RESULT_FILE"

benchmark_heap() {
  local heap=$1
  local run=$2
  local logf="$LOG_DIR/heap${heap}-run${run}.log"
  echo ">>> Start heap=$heap run=$run"
  java -Xms"${heap}"m -Xmx"${heap}"m -XX:+UseG1GC -jar "$JAR" >"$logf" 2>&1
  rc=$?
  spend=$(sed -n 's/.*spend msec:[[:space:]]*\([0-9][0-9]*\).*/\1/p' "$logf" | tail -1)
  if [ $rc -ne 0 ] || [ -z "${spend:-}" ]; then
    status="FAIL(rc=$rc)"
    spend=${spend:-NA}
  else
    status="OK"
  fi
  echo "$heap,$run,$spend,$status" >> "$RESULT_FILE"
  echo "<<< Done heap=$heap run=$run spend_msec=$spend $status"
}

for heap in $(seq $HEAP_START $HEAP_STEP $HEAP_END); do
  for run in $(seq 1 "$RUNS"); do
    benchmark_heap "$heap" "$run"
  done
done

echo "---- Сводка (медиана по прогонам) ----"
printf "%-8s %-4s %12s\n" "heap_mb" "n" "spend_msec"
tail -n +2 "$RESULT_FILE" | awk -F, '$4=="OK"{print $1, $3}' | \
sort -n | \
awk '{h=$1; v[h]=v[h] " " $2} END {for(h in v){print h, v[h]}}' | \
while read h vals; do
  n=$(echo "$vals" | wc -w)
  sorted=$(echo "$vals" | tr ' ' '\n' | sort -n)
  if [ $((n % 2)) -eq 1 ]; then
    med=$(echo "$sorted" | sed -n "$(( (n+1)/2 ))p")
  else
    a=$(echo "$sorted" | sed -n "$((n/2))p")
    b=$(echo "$sorted" | sed -n "$((n/2+1))p")
    med=$(awk "BEGIN{printf \"%.0f\", ($a+$b)/2}")
  fi
  printf "%-8s %-4s %12s\n" "$h" "$n" "$med"
done | sort -n

echo "Done. CSV: $RESULT_FILE"

