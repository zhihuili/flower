#!/usr/bin/env bash

this="${BASH_SOURCE:-$0}"

flower_home="`dirname "$this"`"/..
flower_home="`cd "$flower_home"; pwd`"

export FLOWER_HOME=${flower_home}
