/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import mapping.SGBD;

/**
 *
 * @author rango
 */
public class ConnectionBase {
    private static final SGBD sgbd = SGBD.POSTGRESQL;       // BY default
    private static final String nameDatabase = "accounting";
    private static final String user = "postgres";
    private static final String mdp = "mdpProm15";
    
 /**
 *
 * Connection 
 * 
 * @param sgbd oracle or postgres
 * @param user 
 * @param mdp
 * @param nameDatabase 
 * @return
 * @throws Exception
 */
    public Connection dbConnect() throws Exception{
        Connection temp = null;

        try {      
            if(sgbd == SGBD.ORACLE){
                Class.forName("oracle.jdbc.driver.OracleDriver");
                temp = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:TITA", user, mdp);
                
            }else if(sgbd == SGBD.POSTGRESQL){
                Class.forName("org.postgresql.Driver");
                temp = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+nameDatabase, user, mdp);
            }

            temp.setAutoCommit(false);
            // System.out.println(temp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Connection failed");
        }
        return temp;
    }
}
