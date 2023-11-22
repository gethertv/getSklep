package dev.gether.getsklep.config;

import dev.gether.getsklep.database.DatabaseType;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class DatabaseConfig extends OkaeriConfig {

    @Comment("Type database SQLite / MYSQL")
    @Comment("Typ bazy danych SQLite / MYSQL")
    public DatabaseType databaseType = DatabaseType.SQLITE;

    @Comment("Ignore this if you chosen SQLITE")
    @Comment("Pomiń to jeżeli ustawiłeś SQLITE")
    public String host = "localhost";

    @Comment("User MySQL")
    @Comment("Nazwa użytkownika MySQL")
    public String username = "root";

    @Comment("Password MySQL")
    @Comment("Hasło MySQL")
    public String password = "pass";

    @Comment("Name of database")
    @Comment("Nazwa bazy danych")
    public String database = "database";

    @Comment("Port MySQL")
    public String port = "3306";

    @Comment("Use SSL")
    @Comment("Używać SSL")
    public boolean ssl = false;
}
