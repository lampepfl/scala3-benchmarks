# Benchmarking a series of commits

`publish-range.sh` and `benchmark-published-range.sh` make it easy to benchmark a series of commits.

Run **from this directory**.

First, publish all commits within a range:
```sh
# args: scala3 location, oldest commit inclusive, newest commit inclusive
./publish-range.sh ../../scala3 abcd1234 defg5678 2>published-log | tee published-versions
```
Then, benchmark them:
```sh
# args: file from the first step, benchmark filter (must match exactly 1 thing)
./benchmark-published-range.sh published-versions helloWorld 2>benchmark-log | tee benchmark-perf
```
