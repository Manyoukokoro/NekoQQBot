#!/bin/bash
mvn clean install
scp target/NekoBot-1.0.1a02.jar root@101.43.37.210:/root/NekoBot-now.jar