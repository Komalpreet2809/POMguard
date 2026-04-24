# Pomguard

Eco-Code Dependency Auditor - a Spring Boot web app that audits Maven `pom.xml` files against the Maven Central Repository.

Upload a `pom.xml` and get a color-coded report:

- **GREEN** - up to date
- **YELLOW** - newer version available
- **UNKNOWN** - not found on Maven Central / no version declared

> Academic project focused on demonstrating **Docker**, **Jenkins**, and **GitHub Actions**.

---

## Run locally (no Docker)

> Requires JDK 17. If your system default is a different JDK (e.g. JDK 8 for another project), dot-source the included activation script first — it sets JDK 17 only for the current PowerShell window:
>
> ```powershell
> . .\activate.ps1
> ```
>
> Close the window to revert to your system JDK.

```bash
mvn spring-boot:run
```

Then open http://localhost:8080.

Run tests:

```bash
mvn test
```

---

## Run with Docker

Build the image:

```bash
docker build -t pomguard:latest .
```

Run it:

```bash
docker run --rm -p 8080:8080 pomguard:latest
```

The app is now at http://localhost:8080.

---

## GitHub Actions CI

The workflow in [.github/workflows/ci.yml](.github/workflows/ci.yml) runs on every push and pull request to `main`:

1. **Build & Test** - sets up JDK 17, runs `mvn verify`, uploads test reports + jar as artifacts.
2. **Docker Build & Push** - builds the Docker image and (on push to `main`) pushes it to GHCR at `ghcr.io/komalpreet2809/pomguard`.

No extra secrets are required - `GITHUB_TOKEN` is provided automatically.

To pull the published image:

```bash
docker pull ghcr.io/komalpreet2809/pomguard:latest
```

---

## Jenkins in Docker

Jenkins runs as a container using the official image extended with Docker CLI + Maven. Config is in [jenkins/Dockerfile](jenkins/Dockerfile) and [docker-compose.yml](docker-compose.yml).

### Start Jenkins

```bash
docker compose up -d --build
```

Jenkins UI: http://localhost:8081

Get the initial admin password:

```bash
docker exec pomguard-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Create the pipeline

1. **New Item** -> name: `pomguard` -> **Pipeline** -> OK.
2. Scroll to **Pipeline** section.
3. Definition: **Pipeline script from SCM**.
4. SCM: **Git**, Repository URL: `https://github.com/Komalpreet2809/POMguard.git`.
5. Branch: `*/main`.
6. Script Path: `Jenkinsfile`.
7. Save -> **Build Now**.

### What the pipeline does

Stages defined in [Jenkinsfile](Jenkinsfile):

1. **Checkout** - pulls the repo.
2. **Build & Test** - runs `mvn verify`, publishes JUnit report + archives the jar.
3. **Docker Build** - builds `pomguard:<BUILD_NUMBER>` and `pomguard:latest`.
4. **Smoke Test** - runs the container and waits for the Docker `HEALTHCHECK` to report `healthy`.

### How Jenkins builds Docker images

The compose file mounts the host's Docker socket (`/var/run/docker.sock`) into the Jenkins container, so `docker` commands inside the pipeline use the host's Docker daemon. This is the "Docker-out-of-Docker" pattern.

### Stop Jenkins

```bash
docker compose down
```

To also remove the persisted Jenkins data:

```bash
docker compose down -v
```

---

## Project structure

```
.
├── .github/workflows/ci.yml      # GitHub Actions CI
├── Dockerfile                    # Multi-stage build for the app
├── Jenkinsfile                   # Jenkins pipeline
├── docker-compose.yml            # Jenkins service
├── jenkins/Dockerfile            # Jenkins image (LTS + docker CLI + maven)
├── pom.xml                       # Maven build
└── src/
    ├── main/java/com/pomguard/
    │   ├── PomguardApplication.java
    │   ├── controller/AuditController.java
    │   ├── model/                (Dependency, AuditResult)
    │   └── service/              (PomParser, MavenCentralClient, VersionComparator, AuditService)
    ├── main/resources/
    │   ├── application.properties
    │   └── templates/            (index.html, result.html)
    └── test/java/com/pomguard/service/
        ├── PomParserTest.java
        └── VersionComparatorTest.java
```
