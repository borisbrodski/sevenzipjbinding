# Spring Boot test

A minimal Spring Boot application using 7-Zip-JBinding, built with Gradle against the jars of the current
build (never from a repository). `run.sh` runs its Spring Boot tests on the plain class path, packages the
application and runs the jar: there Spring Boot's own class loader, invisible to the system class loader,
loads the binding and the application, the setup of GitHub issue #35.

From inside every callback the application resolves a Spring Boot class and one of its own classes through
the thread's context class loader, looks up a service with `ServiceLoader`, fetches and calls a Spring bean
and logs. Archives are created with two 7-Zip threads so that callbacks run on 7-Zip's own threads whatever
the CPU count; the multi-block extraction needs several CPUs for that.

    ./run.sh ../../../SevenZipJBinding.Debug

This test isn't part of the test bundles that run on every platform. It runs on the build host, once per
test campaign, after the Debug build.
