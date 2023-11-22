package dev.gether.getsklep.database;

import dev.gether.getsklep.GetSklep;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Database {

    private Logger logger;
    public Database() {

        logger = Bukkit.getServer().getLogger();
        createTable();
    }

    public abstract void openConnection();
    public abstract Connection getConnection();

    private void createTable() {
        try {
            String createClansTable = "CREATE TABLE IF NOT EXISTS " + tableClans + " ("
                    + "id INT(10) AUTO_INCREMENT PRIMARY KEY,"
                    + "tag VARCHAR(10),"
                    + "owner_uuid VARCHAR(100),"
                    + "deputy_uuid VARCHAR(100))";

            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + tableUsers + " ("
                    + "id INT(10) AUTO_INCREMENT PRIMARY KEY,"
                    + "uuid VARCHAR(100),"
                    + "username VARCHAR(100),"
                    + "kills INT(11) DEFAULT 0,"
                    + "deaths INT(11) DEFAULT 0,"
                    + "points INT(11) DEFAULT 0,"
                    + "clan_tag VARCHAR(100))";


            String createAllianceTable = "CREATE TABLE IF NOT EXISTS " + tableAlliance + " ("
                    + "clan_tag1 VARCHAR(20),"
                    + "clan_tag2 VARCHAR(20))";

            Statement stmt = getConnection().createStatement();
            stmt.execute(createClansTable);
            stmt.execute(createUsersTable);
            stmt.execute(createAllianceTable);
            stmt.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Nie można stworzyć tabeli!", e);
        }
    }

    public void closeConnection() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.close();
                logger.log(Level.INFO, "Zamknięto połączenie z bazą danych.");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Nie można zamknąć połączenia z bazą danych!", e);
            }
        }
    }

    public boolean isConnected() {
        Connection connection = getConnection();
        try {
            return (connection != null && !connection.isClosed());
        } catch (SQLException e) {
            return false;
        }
    }


}
