# Logger Configuration and Dependency Management

## 1. Critical Version Inconsistencies

The most significant risks come from manually overriding or including specific versions that deviate from the Spring Boot 3.5.x managed BOM (Bill of Materials).

### Jackson Versions (2.19.4)

While 2.19.x is very recent, ensure all Jackson modules (core, databind, annotations, and dataformats) are strictly on the same version. Mixed Jackson versions are the #1 cause of JSON serialization errors.

### Netty & Reactor Netty

You have Netty 4.1.128.Final alongside Reactor Netty 1.2.12. These are typically managed by the `io.projectreactor:reactor-bom`. If you find connection leaks or Epoll issues, let Spring Boot manage these instead of defining them manually.

### Hibernate 6.6.36.Final

Spring Boot 3.5 typically pairs with Hibernate 6.6. If you are experiencing `entityManagerFactory` bean creation errors, this is usually due to a mismatch between `hibernate-core` and `jakarta.persistence-api`.

## 2. Legacy / "Polluted" Classpath Items

These JARs often appear due to transitive dependencies from older libraries and should be excluded:

- **commons-logging:1.2** – Spring 6.x uses `spring-jcl` to bridge logging to SLF4J. Having the actual commons-logging JAR can lead to "Log Factory" initialization loops.
- **httpclient:4.5.14 & httpcore:4.4.16** – Legacy Apache HttpClient 4.x versions. Spring 6 prefers Apache HttpClient 5.x.
- **listenablefuture:9999.0-empty** – A "dummy" artifact used by Guava to prevent conflicts. Indicates you might be pulling in very old Google libraries transitively.

## 3. Architecture Conflicts (Native Binaries)

Platform-specific JARs included:

- `netty-transport-native-epoll:4.1.128.Final-linux-x86_64`
- `netty-resolver-dns-native-macos:4.1.128.Final-osx-x86_64`

> [!IMPORTANT]
> If your build environment (e.g., CI/CD on Linux) differs from your production environment (e.g., Docker), ensure you aren't bundling the macOS native resolver in your final Linux deployment.

## 4. Recommendations for build.gradle

### Use the Spring Boot BOM

Remove explicit versions for any JAR starting with `spring-`, `jackson-`, `hibernate-`, or `micrometer-`.

### Clean up Logging

```gradle
configurations.all {
    exclude group: "commons-logging", module: "commons-logging"
}
```

### Update Oracle Driver

You are using `ojdbc8:12.2.0.1`. For Spring Boot 3 (Java 17+), migrate to `ojdbc11` for better JDBC compatibility with HikariCP 6.x.
