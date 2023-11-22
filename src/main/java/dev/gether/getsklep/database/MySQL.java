package dev.gether.getsklep.database;

import dev.gether.getsklep.GetSklep;
import dev.gether.getsklep.config.DatabaseConfig;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

public class MySQL extends Database {

    private final GetSklep plugin;
    private Connection connection;
    private String host;
    private String username;
    private String password;
    private String database;
    private String port;
    private boolean ssl;

    public MySQL(GetSklep plugin, DatabaseConfig databaseConfig) {
        this.plugin = plugin;
    }

    public void openConnection() {
        try {
            long startTime = System.currentTimeMillis();
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", username);
            properties.setProperty("password", password);
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("useSSL", String.valueOf(ssl));
            properties.setProperty("requireSSL", String.valueOf(ssl));
            properties.setProperty("verifyServerCertificate", "false");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, properties);

            long endTime = System.currentTimeMillis();
            plugin.getLogger().log(Level.INFO, "Połączono z bazą danych w " + (endTime - startTime) + "ms");
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Nie można połączyć się z bazą danych!", e);
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
}
