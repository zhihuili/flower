运行LoadSimulation测试用例

1. 搭建scala环境
```
https://www.scala-lang.org/download/
```
2. 编译 LoadSimulation.scala
```
mvn scala:compile compile
```

3. 运行 LoadSimulation
```
在flower.sample目录执行以下命令
mvn gatling:test -Dgatling.simulationClass=com.ly.train.flower.sample.web.performance.LoadSimulation -Dbase.url=http://localhost:8080/ -Dtest.path=flow -Dsim.users=300

-Dbase.url=http://localhost:8080/ 填写当前的WebService地址
-Dtest.path=flow url的路径
-Dsim.users=300  模拟300个用户访问   
```