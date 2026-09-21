#!/bin/bash
# Drive the Central Portal OSSRH-staging-API compat endpoints (status/drop/publish) using the
# nexus-releases Portal token from ~/.m2/settings.xml. The token is never printed.
#   ./mvn-central-staging.sh status                  # list open staging repositories
#   ./mvn-central-staging.sh drop <repository-key>   # delete one staging repository
#   ./mvn-central-staging.sh publish [user_managed|automatic]   # push namespace's staged content to the Portal
set -euo pipefail
BASE=https://ossrh-staging-api.central.sonatype.com
NS=net.sf.sevenzipjbinding
AUTH=$(python3 - <<'PY'
import re,os,base64
s=open(os.path.expanduser('~/.m2/settings.xml')).read()
for m in re.finditer(r'<server>.*?</server>', s, re.S):
    b=m.group(0)
    if re.search(r'<id>\s*nexus-releases\s*</id>', b):
        u=re.search(r'<username>(.*?)</username>',b,re.S).group(1).strip()
        p=re.search(r'<password>(.*?)</password>',b,re.S).group(1).strip()
        tok=base64.b64encode(f"{u}:{p}".encode()).decode()  # keep LAST match (Maven uses last)
print(tok)
PY
)
cmd="${1:-status}"
case "$cmd" in
  status)  curl -sS -H "Authorization: Bearer $AUTH" "$BASE/manual/search/repositories" | python3 -m json.tool ;;
  drop)    curl -sS -X DELETE -H "Authorization: Bearer $AUTH" "$BASE/manual/drop/repository/$2"; echo "(dropped $2)" ;;
  publish) curl -sS -X POST   -H "Authorization: Bearer $AUTH" "$BASE/manual/upload/defaultRepository/$NS?publishing_type=${2:-user_managed}"; echo "(publish requested: ${2:-user_managed})" ;;
  *) echo "usage: $0 status|drop <key>|publish [user_managed|automatic]"; exit 1 ;;
esac
