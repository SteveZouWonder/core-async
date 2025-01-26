apply(plugin = "project")
apply(plugin = "azure-maven")
apply(plugin = "publish")
plugins {
    `java-library`
}

subprojects {
    group = "com.foodtruck"
    version = "1.0.0"

    repositories {
        maven {
            url = uri("https://maven.codelibs.org/")
            content {
                includeGroup("org.codelibs.elasticsearch.module")
            }
        }
        maven {
            url = uri("https://neowu.github.io/maven-repo/")
            content {
                includeGroupByRegex("core\\.framework.*")
                excludeModule("core.framework.mysql", "mysql-connector-j")
            }
        }
    }

    configure(subprojects.filter { (it.name.endsWith("-interface")) }) {
        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    if (!plugins.hasPlugin("java")) {
        return@subprojects
    }

    configurations.all {
        resolutionStrategy {
            force("com.squareup.okio:okio:3.2.0")
        }
    }

    dependencies {
        implementation(platform("com.wonder:wonder-dependencies:1.0.0"))
    }
}

configure(subprojects.filter { it.name.endsWith("-db-migration") }) {
    apply(plugin = "db-migration")

    dependencies {
        runtimeOnly("com.mysql:mysql-connector-j")
    }
}

configure(subprojects.filter { (it.name.endsWith("-interface") || it.name.endsWith("-interface-v2")) }) {
    apply(plugin = "lib")
    dependencies {
        implementation("com.wonder:core-ng-api")
    }
}

configure(listOf(
        project("core-async"),
        project("core-async-test")
    )) {
    apply(plugin = "lib")
    dependencies {
        implementation("com.wonder:core-ng")
        testImplementation("com.wonder:core-ng-test")
    }
}

