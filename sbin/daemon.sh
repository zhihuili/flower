#!/usr/bin/env bash
#
# Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


sbin="`dirname "$0"`"
sbin="`cd "$sbin"; pwd`"
. ${sbin}/conf.sh

export FLOWER_NICENESS=0
export RUNNER="java -classpath ${FLOWER_HOME}/flower.assembly/flower.jar"

rotate_log ()
{
    log=$1;
    num=5;
    if [[ -n "$2" ]]; then
        num=$2
    fi
    if [[ -f "$log" ]]; then # rotate logs
        while [[ ${num} -gt 1 ]]; do
            prev=`expr ${num} - 1`
            [[ -f "$log.$prev" ]] && mv "$log.$prev" "$log.$num"
            num=${prev}
        done
        mv "$log" "$log.$num";
    fi
}


option=$1
shift
command=$1
shift
config=$1
shift


export LOG_DIR=${FLOWER_HOME}/logs
mkdir -p "$LOG_DIR"

export FLOWER_IDENT="$USER"
touch "$LOG_DIR"/.flower_test > /dev/null 2>&1
TEST_LOG_DIR=$?
if [[ "${TEST_LOG_DIR}" = "0" ]]; then
  rm -f "$LOG_DIR"/.flower_test
else
  chown "$FLOWER_IDENT" "$LOG_DIR"
fi
log="$LOG_DIR/flower-$FLOWER_IDENT-$command.out"

export PID_DIR=/tmp
pid="$PID_DIR/flower-$FLOWER_IDENT-$command.pid"

case ${option} in
    (start)
        mkdir -p ${PID_DIR}
        if [[ -f ${pid} ]]; then
            if kill -0 `cat ${pid}` > /dev/null 2>&1; then
                echo ${command} running as process `cat $pid`.  Stop it first.
                exit 1
            fi
        fi
        rotate_log ${log}
        echo starting ${command}, logging to ${log}
        nohup nice -n ${FLOWER_NICENESS} ${RUNNER} ${command} ${config} $@ >> "$log" 2>&1 < /dev/null &
        newpid=$!
        echo ${newpid} > $pid
        sleep 2
        # Check if the process has died; in that case we'll tail the log so the user can see
        if ! kill -0 $newpid >/dev/null 2>&1; then
            echo "failed to launch $command:"
            tail -2 "$log" | sed 's/^/  /'
            echo "full log in $log"
        fi
        ;;
    (stop)

        if [ -f $pid ]; then
            if kill -0 `cat $pid` > /dev/null 2>&1; then
                echo stopping $command
                kill `cat $pid`
            else
                echo no $command to stop
            fi
        else
            echo no $command to stop
        fi
        ;;
esac