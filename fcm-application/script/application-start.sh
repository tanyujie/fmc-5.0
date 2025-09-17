#!/bin/bash
# 获取当前sh脚本所在目录，export关键字能让参数在子sh脚本文件中访问
export curdir=$(cd `dirname $0`; pwd)

# 获取当前执行的脚本文件的父目录。
export workdir=$(dirname $curdir)

# 若目录$(workdir)/tmp/java-dump/存在，则删除再创建
if [ -d "$workdir/tmp/java-dump/" ];then
rm -rf dir "$workdir/tmp/java-dump/";
mkdir "$workdir/tmp/java-dump/";
fi;

# 若目录$workdir/tmp/java-io/存在，则删除再创建
if [ -d "$workdir/tmp/java-io/" ];then
rm -rf dir "$workdir/tmp/java-io/";
mkdir "$workdir/tmp/java-io/";
fi;

# 若目录$workdir/gclogs/不存在，则创建
if [ ! -d "$workdir/gclogs/" ];then
mkdir -p "$workdir/gclogs/";
fi;

jarpath=$workdir/lib/fcm-application.jar

# jvm参数变量定义 
jvmArgs="-server \
 -Xss512K \
 -XX:InitialHeapSize=1024M \
 -XX:MaxHeapSize=1024M \
 -XX:MetaspaceSize=256M \
 -XX:MaxMetaspaceSize=256M \
 -XX:AutoBoxCacheMax=1000000 \
 -XX:+UseG1GC \
 -XX:SurvivorRatio=4 \
 -XX:G1HeapRegionSize=16M \
 -XX:MaxGCPauseMillis=200 \
 -XX:+UnlockExperimentalVMOptions \
 -XX:G1NewSizePercent=10 \
 -XX:G1MaxNewSizePercent=75 \
 -XX:ParallelGCThreads=8 \
 -XX:ConcGCThreads=4 \
 -XX:InitiatingHeapOccupancyPercent=45 \
 -XX:G1MixedGCLiveThresholdPercent=65 \
 -XX:G1HeapWastePercent=10 \
 -XX:G1MixedGCCountTarget=8 \
 -XX:G1OldCSetRegionThresholdPercent=10 \
 -XX:G1ReservePercent=10 \
 -Djava.security.egd=file:/dev/./urandom \
 -XX:HeapDumpPath=\"$workdir/tmp/java-dump\" \
 -Djava.io.tmpdir=\"$workdir/tmp/java-io\" \
 -Xlog:gc=\"trace:file=$workdir/gclogs/application-gc-%p-%t.log:time,tags,tid,pid:filecount=5,filesize=52428800:safepoint\" \
 -jar \"$jarpath\" --server.port=18275 --spring.profiles.active=prod"

echo "JVM参数明细：$jvmArgs";

# 传入JVM参数，开始执行
/bin/sh -c "/home/fcm/jdk-14.0.2/bin/java $jvmArgs &"