FROM maven AS builder

WORKDIR /usr/src/app

COPY pom.xml .
COPY src src

RUN mvn package

FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi

COPY --chown=1001:0 --from=builder /usr/src/app/src/main/liberty/config /config

RUN features.sh

ARG TLS=true
ARG SEC_SSO_PROVIDERS="oidc"

ENV SEC_TLS_TRUSTDEFAULTCERTS=true
ENV SEC_IMPORT_K8S_CERTS=true

COPY --chown=1001:0 --from=builder /usr/src/app/target/sample-cognito.war /config/apps

RUN configure.sh
