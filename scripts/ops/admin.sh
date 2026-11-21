#!/usr/bin/env bash
source "$(dirname "$0")/common.sh"
lock_operations
[[ ${1:-} =~ ^[1-9][0-9]{0,15}$ ]] || die 'Usage: admin.sh TELEGRAM_USER_ID (user must already exist)'
docker exec -e ADMIN_ID="$1" mongo mongosh --quiet --nodb --eval '
  const uri = "mongodb://" + encodeURIComponent(process.env.MONGO_INITDB_ROOT_USERNAME) + ":" + encodeURIComponent(process.env.MONGO_INITDB_ROOT_PASSWORD) + "@127.0.0.1:27017/?authSource=admin";
  const database = new Mongo(uri).getDB(process.env.MONGODB_DATABASE);
  const result = database.user.updateOne({_id: Long(process.env.ADMIN_ID)}, {$addToSet: {roles:"ADMIN"}});
  if (result.matchedCount !== 1) throw new Error("User not found: send /start first");
  print("Administrator role assigned");'
