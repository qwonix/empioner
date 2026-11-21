#!/usr/bin/env python3
"""Import an old Mongo JSON export as a separate, ID-only statistics collection.

Run on the database host: python3 scripts/ops/import-historical-users.py /root-only/export.json
The original file is not changed; operational user documents are not modified.
"""
import json
import pathlib
import subprocess
import sys


def main():
    if len(sys.argv) != 2:
        raise SystemExit("usage: import-historical-users.py /path/to/user.json")
    records = json.loads(pathlib.Path(sys.argv[1]).read_text(encoding="utf-8-sig"))
    if not isinstance(records, list):
        raise SystemExit("Expected a JSON array")
    ids = set()
    for record in records:
        value = record.get("_id")
        if isinstance(value, dict) and set(value) == {"value"}:
            value = value["value"]
        if isinstance(value, bool) or not isinstance(value, int) or not 0 < value < 2**63:
            raise SystemExit("Unrecognized user identifier; no data imported")
        ids.add(str(value))
    if not ids:
        raise SystemExit("Empty export; no data imported")
    javascript = r'''
const ids = new Set(USER_IDS);
const uri = "mongodb://" + encodeURIComponent(process.env.MONGODB_USER) + ":" + encodeURIComponent(process.env.MONGODB_PASSWORD) + "@127.0.0.1:27017/" + process.env.MONGODB_DATABASE;
const targetDb = new Mongo(uri).getDB(process.env.MONGODB_DATABASE);
const users = targetDb.getCollection("analytics_historical_users");
// An existing set may be a previous interrupted import. Never discard or mix a different export.
for (const user of users.find({}, {_id: 1})) {
  if (!ids.has(String(user._id))) throw Error("Historical collection contains a different dataset; no changes made");
}
let batch = [];
for (const id of ids) {
  batch.push({updateOne: {filter: {_id: Long.fromString(id)}, update: {$setOnInsert: {_id: Long.fromString(id)}}, upsert: true}});
  if (batch.length === 1000) { users.bulkWrite(batch, {ordered: true}); batch = []; }
}
if (batch.length) users.bulkWrite(batch, {ordered: true});
const actual = users.countDocuments({});
if (actual !== ids.size) throw Error("Historical collection count mismatch");
print(JSON.stringify({unique_users: ids.size, stored: actual}));
'''.replace("USER_IDS", json.dumps(sorted(ids)))
    result = subprocess.run(["docker", "exec", "-i", "mongo", "mongosh", "--quiet", "--nodb", "--file", "/dev/stdin"],
                            input=javascript, text=True, check=True, capture_output=True)
    print(result.stdout.strip())
    print(f"source records: {len(records)}; duplicate identifiers: {len(records) - len(ids)}")


if __name__ == "__main__":
    main()
