#!/bin/bash

SERVER=fikovnik.net
R_PATH=/www/projects/TACO/update-site/
L_PATH=$(dirname $0)/src/net.fikovnik.projects.taco.update-site/target/site/

function build() {
  mvn -f src/net.fikovnik.projects.taco.parent/pom.xml clean install
  if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
  fi
}

function upload() {
  # get username
  ftpu=$(/usr/bin/security find-internet-password -s $SERVER | grep 'acct' | cut -d '"' -f 4)
  # get password
  ftpp=$(/usr/bin/security 2>&1 > /dev/null find-internet-password -g -s $SERVER | cut -d '"' -f 2)

  # upload update site
  ftpsync.pl $L_PATH ftp://$ftpu:$ftpp@$SERVER/$R_PATH
  if [ $? -ne 0 ]; then
    echo "Upload failed!"
    exit 1
  fi
}

# build
if [ "$1" = "build" ]; then
  build
elif [ "$1" = "upload" ]; then
  upload
else
  build
  upload
fi

