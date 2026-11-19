#!/bin/bash
# Drive the Central Portal OSSRH-staging-API compat endpoints (status/drop/publish) using the
# nexus-releases Portal token from ~/.m2/settings.xml. The token is never printed.
#   ./mvn-central-staging.sh status                            # list staging repositories (any client IP)
#   ./mvn-central-staging.sh drop <repository-key>             # delete one staging repository
#   ./mvn-central-staging.sh publish [user_managed|automatic]  # push the open staging repository to the Portal
#
# The compat service keys every staging repository by the CLIENT IP Maven deployed from
# ("<id>/<ip>/<namespace>--default-repository"). Its search defaults to ip=client and the
# defaultRepository upload endpoint resolves the repository by the caller's IP, so a curl that
# connects over another address (e.g. IPv6 while Maven used IPv4) sees nothing / gets
# "No repository found" (hit on 2026-11-18, release 26.06-2.3). Therefore: list with ip=any and
# publish the repository BY KEY instead of relying on the caller's IP.
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
list_repos() { curl -sS -H "Authorization: Bearer $AUTH" "$BASE/manual/search/repositories?profile_id=$NS&ip=any"; }
open_repo_key() { # the single OPEN repository of our namespace, or fail
  list_repos | python3 -c '
import sys,json
keys=[r["key"] for r in json.load(sys.stdin)["repositories"] if r["state"]=="open"]
if len(keys)!=1: sys.exit("expected exactly one OPEN staging repository, found %d: %s" % (len(keys), keys))
print(keys[0])'
}
cmd="${1:-status}"
case "$cmd" in
  status)  list_repos | python3 -m json.tool ;;
  drop)    curl -sS -X DELETE -H "Authorization: Bearer $AUTH" "$BASE/manual/drop/repository/$2"; echo "(dropped $2)" ;;
  publish) key=$(open_repo_key); echo "publishing staging repository: $key"
           curl -sS -w '\nHTTP %{http_code}\n' -X POST -H "Authorization: Bearer $AUTH" \
             "$BASE/manual/upload/repository/$key?publishing_type=${2:-user_managed}"
           echo "(publish requested: ${2:-user_managed}; the Portal deployment id appears in 'status' as portal_deployment_id)" ;;
  *) echo "usage: $0 status|drop <key>|publish [user_managed|automatic]"; exit 1 ;;
esac
