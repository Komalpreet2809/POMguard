FROM jenkins/jenkins:lts

USER root

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        docker.io \
        maven \
        curl \
    && rm -rf /var/lib/apt/lists/*

USER jenkins
