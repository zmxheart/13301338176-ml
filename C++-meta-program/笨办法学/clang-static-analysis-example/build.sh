#!/bin/sh
rm -rf 'build' 'report'
mkdir -p 'build' 'report'
cd 'build'

cmake -DCMAKE_C_COMPILER=`which ccc-analyzer` -DCMAKE_BUILD_TYPE=Debug ..
scan-build-3.5 --use-analyzer=/usr/bin/clang-3.5 --view -o ${PWD}/report make
