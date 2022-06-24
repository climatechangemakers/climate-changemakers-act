!#/bin/bash

set -e
set -u
set -o pipefail

curl --url https://theunitedstates.io/congress-legislators/legislators-current.json --output legislators-current.json
curl --url https://theunitedstates.io/congress-legislators/legislators-district-offices.json --output legislators-district-offices.json
curl --url https://theunitedstates.io/congress-legislators/legislators-historical.json --output legislators-historical.json
curl --url https://theunitedstates.io/congress-legislators/legislators-social-media.json --output legislators-social-media.json
curl --url https://soapbox.senate.gov/api/active_offices.json | jq > active-offices.json
