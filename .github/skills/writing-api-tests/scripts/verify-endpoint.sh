#!/usr/bin/env bash
# Calls a real HealthHub API endpoint and prints back what it ACTUALLY
# returns — status code and JSON shape — so nobody has to assume it.
# Usage: ./verify-endpoint.sh /appointments
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 </endpoint-path>" >&2
  exit 1
fi

PATH_ARG="$1"
BASE_URL="https://healthhub.qualintia.com/api/v1"
EMAIL="patient@healthhub.test"
PASSWORD="Test123!"

# Already wired: log in and grab the real token the same way
# ApiClient.getAuthToken() does — don't change this part.
TOKEN=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"${EMAIL}\", \"password\": \"${PASSWORD}\"}" \
  | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['token'])")

# TODO: call ${BASE_URL}${PATH_ARG} with the bearer token above, print
# the real HTTP status code AND the response body's JSON structure
# (top-level keys at minimum — don't just dump the raw blob and call it
# done). Someone reading this script's output should immediately know
# whether the real shape matches what api.instructions.md documents,
# without reading JSON by eye.
#
# Hint: curl's -w flag can print the status code separately from -o/body
# output; `python3 -m json.tool` or `jq` (if available) can pretty-print
# and list keys.

echo "TODO: not implemented yet — see the comment above."
