package dev.gether.getsklep.database;

import dev.gether.getsklep.GetSklep;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLite {

    private Connection connection;
    private String database;

    private String table = "getsklep";
    private String createTableSQL = "CREATE TABLE IF NOT EXISTS "+table+" (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uuid VARCHAR(100), " +
            "username VARCHAR(100), " +
            "spend_point INT(11) NOT NULL DEFAULT '0')";

    public SQLite(String database){
        super();
        this.database = database;

        openConnection();
        createTable(createTableSQL);
    }

    public void update(String paramString) {
        try {
            Connection connection = getConnection();
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                statement.executeUpdate(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong update : '" + paramString + "'!");
        }
    }

    public void openConnection() {
        File dataFolder = new File(GetSklep.getInstance().getDataFolder(), database+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                GetSklep.getInstance().getLogger().log(Level.SEVERE, "File write error: "+database+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                this.connection = connection;
            }
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException ex) {
            GetSklep.getInstance().getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            GetSklep.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
    }

    public void createTable(String sqlCreate) {

        update(sqlCreate);
    }

    public Connection getConnection() {
        return connection;
    }

    public void loadUser(Player player) {
        if(!playerExists(player.getUniqueId()))
        {
            createUser(player);
            GetSklep.getInstance().getUserSpentPoints().put(player.getUniqueId(), 0);
            return;
        }

        String str = "SELECT * FROM "+table+" WHERE uuid = '" + player.getUniqueId() + "'";
        try {
            ResultSet resultSet = getResult(str);
            while (resultSet.next()) {
                int spendPoint = resultSet.getInt("spend_point");

                GetSklep.getInstance().getUserSpentPoints().put(
                        player.getUniqueId(), spendPoint);
            }

        } catch (SQLException | NullPointerException sQLException) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println(sQLException.getMessage());
                    player.kickPlayer("Bląd! Zgłoś sie na discord!");
                }
            }.runTask(GetSklep.getInstance());
        }
    }

    public void updateUser(Player player) {
        Integer points = GetSklep.getInstance().getUserSpentPoints().get(player.getUniqueId());

        update("UPDATE "+table+" SET " +
                "spend_point = '"+points+"' " +
                "WHERE uuid = '"+player.getUniqueId()+"'");
    }

    public void createUser(Player player)
    {
        update("INSERT INTO "+table+" (uuid, username) VALUES ('"+player.getUniqueId()+"', '"+player.getName()+"')");
    }


    public boolean playerExists(UUID uuid) {
        return (getPlayerID(uuid) != 0);
    }

    private int getPlayerID(UUID uuid) {
        return getInt("id", "SELECT id FROM "+table+" WHERE uuid='" + uuid.toString() + "'");
    }
    private int getInt(String paramString1, String paramString2) {
        try {
            ResultSet resultSet = getResult(paramString2);
            if (resultSet.next()) {
                int i = resultSet.getInt(paramString1);
                resultSet.close();
                return i;
            }
        } catch (SQLException sQLException) {
            return 0;
        }
        return 0;
    }

    public ResultSet getResult(String paramString) {
        ResultSet resultSet = null;
        Connection connection = getConnection();
        try {
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                resultSet = statement.executeQuery(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong when want get result: '" + paramString + "'!");
        }
        return resultSet;
    }
    public void disconnect()
    {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isConnected() {
        return (getConnection() != null);
    }


}