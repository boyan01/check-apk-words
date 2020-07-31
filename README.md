# check-apk-words

反编译APK，检索 APK 内的关键字，打印关键字所在的文件/类。

对于混淆后的 apk ，提供 mapping.txt ,则支持输出源文件的类名。

`words-gradle-plugin` 支持定位源文件所在的工程/依赖。

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