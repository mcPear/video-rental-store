#!/bin/bash
mvn clean package

if which xdg-open > /dev/null
then
  xdg-open http://localhost:8080
elif which gnome-open > /dev/null
then
  gnome-open http://localhost:8080
fi
