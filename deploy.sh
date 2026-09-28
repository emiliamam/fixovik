#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed. Install Docker Engine + Docker Compose plugin first." >&2
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "Docker Compose plugin is not available (docker compose)." >&2
  exit 1
fi

if [ ! -f .env ]; then
  if ! command -v openssl >/dev/null 2>&1; then
    echo "openssl is required to generate initial secrets." >&2
    exit 1
  fi

  postgres_password="$(openssl rand -hex 32)"
  redis_password="$(openssl rand -hex 32)"
  app_default_password="$(openssl rand -hex 16)"
  remember_me_key="$(openssl rand -hex 32)"

  cat > .env <<EOF_ENV
POSTGRES_DB=fixovik
POSTGRES_USER=fixovik
POSTGRES_PASSWORD=${postgres_password}
REDIS_PASSWORD=${redis_password}
APP_DEFAULT_PASSWORD=${app_default_password}
APP_REMEMBER_ME_KEY=${remember_me_key}
EOF_ENV

  chmod 600 .env

  cat <<EOF_INFO
Created .env with generated secrets.

Initial application accounts use the generated APP_DEFAULT_PASSWORD:
  admin / ${app_default_password}
  moderator / ${app_default_password}
  user / ${app_default_password}
  master / ${app_default_password}

Change these application passwords after the first login.
EOF_INFO
fi

docker compose up -d --build

echo
printf '%s\n' 'Application status:'
docker compose ps

echo
echo 'Health check:'
docker compose exec -T app curl -fsS http://127.0.0.1:8080/actuator/health
printf '\n'
