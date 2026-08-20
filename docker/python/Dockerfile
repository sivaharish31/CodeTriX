FROM python:3.12-alpine

RUN apk add --no-cache \
    && rm -rf /var/cache/apk/* \
    && pip install --no-cache-dir --upgrade pip

RUN adduser -D -s /bin/sh runner

WORKDIR /code

USER runner

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

CMD ["/bin/sh"]
