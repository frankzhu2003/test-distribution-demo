plugins {
    `java-library`
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.test {
    // force tests to always run; see https://www.stefan-oehme.com/stop-rerunning-tests for inspiration
    inputs.property("integration.date", System.currentTimeMillis())

    onlyIf { true }
    useJUnitPlatform()
    //maxParallelForks = 2
    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
        displayGranularity = 1
    }

    develocity.predictiveTestSelection {
        enabled.set(true)
    }
    develocity.testDistribution {
        maxLocalExecutors.set(8)
        maxRemoteExecutors.set(4)
        enabled.set(true)
//        remoteExecutionPreferred.set(true)
    }

    outputs.upToDateWhen { true }
    outputs.cacheIf { true }
    finalizedBy(tasks.jacocoTestReport)

   // doLast {
   //     System.out.println("\nPredictive Test Selection: 12 test classes selected, 23 test classes not selected (saving 1h 9m 53s)")
   // }

}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

fun isSmokeTest(): Boolean = System.getProperties().contains("SMOKE_TEST")
