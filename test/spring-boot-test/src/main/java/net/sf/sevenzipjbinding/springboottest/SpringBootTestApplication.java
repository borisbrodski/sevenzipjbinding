package net.sf.sevenzipjbinding.springboottest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A Spring Boot application using 7-Zip-JBinding. Run as a packaged jar it loads everything, the binding and
 * this code, through Spring Boot's own class loader, which is invisible to the system class loader: the setup of
 * GitHub issue #35. With the profile <code>selftest</code> active {@link SelfTestRunner} runs the archive
 * scenarios and the exit code tells the result.
 */
@SpringBootApplication
public class SpringBootTestApplication {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBootTestApplication.class, args)));
    }
}
