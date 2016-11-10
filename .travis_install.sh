#!/usr/bin/env bash

git clone https://github.com/danglotb/CoCoSpoon.git
cd CoCoSpoon
git checkout java7
mvn clean install
cd ..

git clone http://github.com/SpoonLabs/nopol.git
cd nopol/nopol
mvn package -DskipTests

cd ../..
mkdir lib
mv nopol/nopol/target/nopol-0.2-SNAPSHOT.jar lib/nopol-0.2-SNAPSHOT.jar