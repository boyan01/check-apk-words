# check-apk-words

~~一个没什么🐔用的工具。~~

反编译APK，检索 APK 内的关键字，打印关键字所在的文件/类。

## words-app

对于混淆后的 apk ，提供 mapping.txt ,则支持输出源文件的类名。

**usages:** 

    Usage: CheckWords options_list
    Arguments:
        apk -> apk path { String }
        words -> senstive words { String, String }
    Options:
        --mapping, -m -> mapping file path { String }
        --ignoreCase -> ignore case when check
        --help, -h -> Usage info




## words-gradle-plugin 

支持定位源文件所在的工程/依赖。

```groovy
apply plugin: 'com.android.application'
apply plugin: "techs.soit.plugin.sensitive-words"

sensitiveWords {
    words = ["google", "chrome"]
}
```

执行 `./gradlew scanReleaseSensitiveWords`

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
