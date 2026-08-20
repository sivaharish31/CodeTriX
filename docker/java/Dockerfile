FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache \
    && rm -rf /var/cache/apk/*

RUN adduser -D -s /bin/sh runner

WORKDIR /code

USER runner

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"

CMD ["/bin/sh"]
