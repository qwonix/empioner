const user = encodeURIComponent(process.env.MONGODB_USER);
const password = encodeURIComponent(process.env.MONGODB_PASSWORD);
const database = encodeURIComponent(process.env.MONGODB_DATABASE);
const connection = new Mongo(`mongodb://${user}:${password}@127.0.0.1:27017/${database}?authSource=${database}&serverSelectionTimeoutMS=3000`);
const result = connection.getDB(process.env.MONGODB_DATABASE).runCommand({ping: 1});
if (result.ok !== 1) quit(1);
