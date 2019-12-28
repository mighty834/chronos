#!/bin/zsh
resultDir="./classes/"

function compile {
    javac -classpath . -d $resultDir */**/*.java
}

function run {
    java -classpath $resultDir $*
}

compile
if [ $# -gt 0 ]
then
    run $*
fi

