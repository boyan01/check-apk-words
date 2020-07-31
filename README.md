## 调试

* 干掉 gradle 守护进程
```shell script
pkill -f '.*GradleDaemon.*'
```

* 执行 debug
```shell script
./gradlew :example:assembleRelease -Dorg.gradle.debug=true

./gradlew :example:scanReleaseSensitiveWords -Dorg.gradle.debug=true
```