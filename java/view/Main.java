/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import database.ConnectionBase;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import jdk.internal.org.jline.terminal.impl.ExecPty;
import mapping.BddObject;
import mapping.SGBD;
import model.accounts.CodeJournal;
import model.accounts.CompteGeneral;
import model.accounts.CompteTiers;
import model.accounts.ContentJournal;
import model.accounts.Exercice;
import model.accounts.RacineCompte;
import model.company.Devise;
import model.company.DeviseChange;
import model.accounts.Journal;
import model.accounts.PieceRef;
import model.accounts.InfoCompta;
import model.company.InfoGeneral;
import model.company.Nif;
import model.company.Rcs;
import model.employe.Employe;
import utilities.Ordering;

/**
 *
 * @author rango
 */
public class Main {
    public static void main(String[] args) throws Exception{
        System.out.println("DAO Project");
        System.out.println("");
        
        Connection connection = null;
        ConnectionBase cb = new ConnectionBase();
        
        try {
            connection = cb.dbConnect();
            System.out.println(cb);
            
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(2022, 0, 1);
//            calendar.add(Calendar.YEAR, 1);
//            calendar.add(Calendar.DAY_OF_MONTH, -1);

            
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            
        } finally{
            connection.close();
        }
        
    }
    
}
