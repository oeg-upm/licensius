package ldc.auth;

import java.sql.*;
import ldc.LdcConfig;
import org.apache.log4j.Logger;

/**
 * Class to test SQLITE access
 * It is recommended to have installed this software component: https://github.com/sqlitebrowser/sqlitebrowser/releases
 * @author vroddon
 */
public class SQLite {

    static final Logger logger = Logger.getLogger("ldc");

    public static void main(String args[]) {

        boolean test = test();
        try{
            createTables();
            addUser("victor","1234");
            addUser("a","b");
        }catch(Exception e){}
        boolean b = authenticate("a","c");
        boolean c = authenticate("victor", "1234");
        System.out.println("Resultado " + b+" "+c);
    }
    
    public static String getCadena()
    {
        String d = LdcConfig.getDataFolder();
        String cadena = "jdbc:sqlite:"+d+"/db";
        logger.debug("Cadena de consexión SQLite: " + cadena);
        return cadena;
    }
    

    public static boolean test() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(getCadena());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean authenticate(String u, String p) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(getCadena());
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS;");
            while (rs.next()) {
                String us = rs.getString("USER");
                String pa = rs.getString("PASSWORD");
                if (us.equals(u) && pa.equals(p)) {
                    rs.close();
                    stmt.close();
                    c.close();
                    return true;
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Operation done successfully");
        return false;
    }

    public static void addUser(String u, String p) {
        logger.info("Añadiendo usuario " + u);
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(getCadena());
            stmt = c.createStatement();
            String sql = "INSERT INTO USERS (USER,PASSWORD) "
                    + "VALUES ('"+u+"', '"+p+"');";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Crea las tabla
     */
    public static void createTables() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(getCadena());
            stmt = c.createStatement();
            String sql = "CREATE TABLE USERS "
                    + "(USER             TEXT PRIMARY KEY     NOT NULL,"
                    + " PASSWORD           TEXT    NOT NULL)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE `OWNERS` (\n" +
"	`USER`	TEXT NOT NULL,\n" +
"	`DATASET`	TEXT NOT NULL,\n" +
"	PRIMARY KEY(USER,DATASET)\n" +
");";
            stmt.executeUpdate(sql);
            
            stmt.close();
            c.close();
            logger.info("Se crearon las tablas en SQLite");
        } catch (Exception e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    public static void setOwner(String user, String dataset)
    {
        logger.info("Insertando dataset" + user + " " + dataset);
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(getCadena());
            stmt = c.createStatement();
            String sql = "INSERT INTO OWNERS (USER,DATASET) "
                    + "VALUES ('"+user+"', '"+dataset+"');";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public static String getOwner(String dataset)
    {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(getCadena());
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM OWNERS;");
            while (rs.next()) {
                String us = rs.getString("USER");
                String pa = rs.getString("DATASET");
                if (pa.equals(dataset)) {
                    rs.close();
                    stmt.close();
                    c.close();
                    return us;
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Operation done successfully");
        return "";
    }

    

}
