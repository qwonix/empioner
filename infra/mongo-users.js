// Init runs only on an empty volume. Existing installs must apply this as admin.
const appDatabase = process.env.MONGODB_DATABASE;
const appUser = process.env.MONGODB_USER;
if (!appDatabase || !appUser || !process.env.MONGODB_PASSWORD || !process.env.MONGODB_EXPORTER_PASSWORD) {
  throw new Error("MongoDB application and monitoring credentials are required");
}
if (["admin", "config", "local"].includes(appDatabase) || ["empioner_metrics", process.env.MONGO_INITDB_ROOT_USERNAME].includes(appUser)) {
  throw new Error("Use distinct application, administrative and monitoring identities");
}
function ensureUser(database, user, password, roles) {
  const options = {pwd: password, roles};
  if (database.getUser(user)) database.updateUser(user, options);
  else database.createUser({user, ...options});
}
ensureUser(db.getSiblingDB(appDatabase), appUser, process.env.MONGODB_PASSWORD,
  [{role: "readWrite", db: appDatabase}]);
ensureUser(db.getSiblingDB("admin"), "empioner_metrics", process.env.MONGODB_EXPORTER_PASSWORD,
  [{role: "clusterMonitor", db: "admin"}, {role: "read", db: "local"}]);
