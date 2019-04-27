#!/bin/bash

DATE_CURRENT="`date '+%Y-%m-%d'`"
flowerHome=$(cd "$(dirname "$0")/../";pwd)
flowerLibs="${flowerHome}/libs"
MAIN_CLASS="com.ly.train.flower.container.Bootstrap"

#logger info
function INFO() {
	MSG="`date '+%Y-%m-%d %H:%M:%S'` INFO $1"
	echo -e $MSG
	#echo -e $MSG >> "$CRPC_LOGS/crpc.$DATE_CURRENT.log"
}

#logger warn
function WARN() {
	MSG="`date '+%Y-%m-%d %H:%M:%S'` WARN $1"
	echo -e $MSG
	#echo -e $MSG >> "$CRPC_LOGS/crpc.$DATE_CURRENT.log"
}
INFO "start flower"
INFO "flowerHome : ${flowerHome}"
INFO "flowerLibs : ${flowerLibs}"

classpath=$classpath:${flowerLibs}/flower.container-0.1.2.jar:${flowerLibs}/slf4j-api-1.7.26.jar
for jar in ${flowerLibs}/*; do 
	str2="logback"
	if [[ $jar == *$str2* ]]; then
		classpath="$classpath:$jar"
	fi
done
java -classpath  $classpath -Dflower.home=${flowerHome} -Dspring.config.location=conf/spr.xml ${MAIN_CLASS}

