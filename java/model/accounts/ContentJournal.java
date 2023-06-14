/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.accounts;

import model.company.Devise;
import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;
import database.ConnectionBase;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import mapping.BddObject;
import utilities.DatePattern;
import utilities.DateUtil;


/**
 *
 * @author rango;
 */
@TableAnnotation(nameTable = "content_journal", sequence = "ct_jour_seq", prefix = "CJOU")
public class ContentJournal {
    @ColumnField(column = "idContentJ",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idContentJ;
    
    @ColumnField(column = "idJournal",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "journal", value_key_other_table = "idJournal")
    private String idJournal;
    
    @ColumnField(column = "idPiece",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "pieceref", value_key_other_table = "idPiece")
    private String idPiece;
    
    @ColumnField(column = "ref",data_type = DataType.VARCHAR)
    private String ref;
    
    @ColumnField(column = "dateJournal",data_type = DataType.DATE)
    private java.sql.Date dateJournal;
    
    @ColumnField(column = "idCoGe",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "coge", value_key_other_table = "idCoGe")
    private String idCoGe;
    
    @ColumnField(column = "idCoTiers",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "tiers", value_key_other_table = "idCoTiers")
    private String idCoTiers;
    
    @ColumnField(column = "libelle",data_type = DataType.VARCHAR)
    private String libelle;
    
    @ColumnField(column = "idDevise",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "devise", value_key_other_table = "idDevise")
    private String idDevise;
    
    @ColumnField(column = "debit",data_type = DataType.NUMERIC)
    private Float debit;
    
    @ColumnField(column = "credit",data_type = DataType.NUMERIC)
    private Float credit;
    
    private Journal journal;
    
    private CompteGeneral coge;
    
    private CompteTiers tiers;
    
    private Devise devise;
    
    /**
     * Converting format from EXCEL
     * @param toCast
     * @return 
     */
    public static float convertToFloat(String toCast) throws Exception{
        try {
            if(toCast.trim().equals("") == true) return 0.0f;
            toCast = ContentJournal.deleteSpace(toCast); // Delete spaces
            toCast = toCast.replace(",", "."); // remove spaces and replace comma with period
        //    toCast = toCast.replace("Â", "");
            float result = Float.valueOf(toCast); // parse the string as a float
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }    
    }
    
    public static String deleteSpace(String str){
        String res = "";  
        for(int i=0; i<str.length();i++){  
            if(str.charAt(i) != ' '){
                char c = str.charAt(i);  
                res += c;
            }
        }
        return res;
    }

    
    /**
     * INserting a list of ecriture in databases
     * @param all
     * @param connection
     * @throws Exception 
     */
    public static void insertTempEcritures(List<ContentJournal> all, Connection connection) throws Exception{
          boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }       

        try{         
            for(ContentJournal cj : all){
                BddObject.insertInDatabase(cj, connection);
            }
            
            if(isOpen == false) connection.commit();            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on inserting temporary list of content_journal");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * Deleting a contentJournal by its id AND Revalidate the journal to INVALID
     * @param connection
     * @throws Exception 
     */
    public void deleteContent(Connection connection) throws Exception{
         boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }       
        PreparedStatement stm = null;
        try{
            Journal temp = this.getJournal();
            temp.setValide(1);
            BddObject.updatingObject(temp, connection);
            
            stm = connection.prepareStatement("DELETE FROM content_journal WHERE idContentJ = ?");
            stm.setString(1, this.getIdContentJ());
            stm.execute();
            
            if(isOpen == false) connection.commit();            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on delete the content_journal");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    
    /**
     * Error if the content_j is in actual exercice
     * @param connection
     * @return
     * @throws Exception 
     */
    public boolean isInActualExercise(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }       
        
        try{
            Exercice actual = Exercice.actualExercice(connection);
            if(this.getDateJournal().after(actual.getFinExo()) == true || this.getDateJournal().before(actual.getDebutExo()) == true){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on knowing if the content_journal is in actual exercice");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }

    // Usefull functions
    /**
     * Get the reference piece for view
     * @param connection
     * @return
     * @throws Exception 
     */   
    public String myPieceNotation(Connection connection) throws Exception{
        if(this.getIdPiece() == null || this.getRef() == null) return null;
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }          
        try{
            PieceRef rc = BddObject.findById(new PieceRef(this.getIdPiece(), null, null), null);
            return rc.getRef() + this.getRef();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the reference piece of the content_journal");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * If a list of contentJOurnal are in certain Exercice
     * @param list
     * @param connection
     * @return
     * @throws Exception 
     */
    public static boolean areInExo(Exercice lastExo, List<ContentJournal> list, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }          
        try{
            if(list !=  null){
                for(ContentJournal co : list){
                    if(DateUtil.isSqlDateBetween(co.getDateJournal(), lastExo.getDebutExo(), lastExo.getFinExo()) == false){
                        return false;
                    }
                }
                return true;
            }     
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on inserting a list of content journal in db");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * Get all contentJOurnal of general accounts within a period
     * @param coge
     * @param connection
     * @return
     * @throws Exception 
     */
    public static List<ContentJournal> compteContentCurrentExo(Exercice lastExo, CompteGeneral coge, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }     
        
        try{
            ContentJournal cj = new ContentJournal();
            cj.setIdCoGe(coge.getIdCoGe());
            List<ContentJournal> temp = BddObject.find(cj, connection);
            List<ContentJournal> result = new ArrayList<>();
            
            for(ContentJournal co : temp){
                if(DateUtil.isSqlDateBetween(co.getDateJournal(), lastExo.getDebutExo(), lastExo.getFinExo()) == true){
                    result.add(co);
                }
            }
            
            return result;
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new Exception("Error on getting all contentJOurnal of a general account within a period");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    
    /**
     * Inesrting a lot of content journal
     * @param all
     * @param connection
     * @throws Exception 
     */
    public static void insertListContent(List<ContentJournal> all, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }          
        try{
            if(all !=  null){
                for(ContentJournal cj : all){
                    BddObject.insertInDatabase(cj, connection);
                }
            }
            if(isOpen == false) connection.commit();                       
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on inserting a list of content journal in db");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * Get the balance of a list of a contentJournal
     * @param all
     * @return 
     */
    public static float[] balanceLedger(List<ContentJournal> all){
        float sumDebit = 0.0f;
        float sumCredit = 0.0f;
        
        for(ContentJournal cj : all){
            if(cj.getDebit() != null) sumDebit += cj.getDebit();
            if(cj.getCredit() != null) sumCredit += cj.getCredit();
        }
        float[] result = new float[2];
        result[0] = sumDebit;
        result[1] = sumCredit;
        return result;
    }
    
    /**
     * Deleting all content journal where journal is invalid
     * @param journal
     * @param connection
     * @throws Exception 
     */
    public static void clearInvalidContentJournal(Journal journal, Connection connection) throws Exception{
        if(journal.getValide() == 0) throw new Exception("System can not clear valid journal");
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }  
        
        PreparedStatement stm = null;
        
        try {
            stm = connection.prepareStatement("DELETE FROM content_journal WHERE idJournal = ?");
            stm.setString(1, journal.getIdJournal());
            stm.execute();
            
            if(isOpen == false) connection.commit();                       
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on clearing all invalid content_journal");
        } finally{
            stm.close();
            if(isOpen == false) connection.close();
        }  
    }
    
    
    // Getters and setters
    public String getIdContentJ(){
        return idContentJ;
    }

    public void setIdContentJ(String idContentJ) {
        this.idContentJ = idContentJ;
    }

    public String getIdJournal() {
        return idJournal;
    }

    public void setIdJournal(String idJournal) {
        this.idJournal = idJournal;
    }

    public Date getDateJournal() {
        return dateJournal;
    }

    public void setDateJournal(Date dateJournal) {
        this.dateJournal = dateJournal;
    }
    
    public void setDateJournal(String dateHtml) throws Exception{
        try {
            if(dateHtml != null){
                java.sql.Date theTime = DateUtil.stringToSqlDate(java.sql.Date.class, dateHtml, DatePattern.YYYY_MM_DD);
                this.setDateJournal(theTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the date of the contentJOurnal");
        }
    }


    public String getIdCoGe() {
        return idCoGe;
    }

    public void setIdCoGe(String idCoGe) {
        this.idCoGe = idCoGe;
    }

    public String getIdCoTiers() {
        return idCoTiers;
    }

    public void setIdCoTiers(String idCoTiers) {
        this.idCoTiers = idCoTiers;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getIdDevise() {
        return idDevise;
    }

    public void setIdDevise(String idDevise) {
        this.idDevise = idDevise;
    }

    public Float getDebit() {
        return debit;
    }

    public void setDebit(Float debit) {
        this.debit = debit;
    }

    public void setDebit(String valueHtml) throws Exception{
        try {
            if(valueHtml != null){
                Float temp = Float.valueOf(valueHtml);
                if(temp < 0) throw new Exception("Negative number");
                this.setDebit(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the value of the Debit");
        }
    }
    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }
    
    public void setCredit(String valueHtml) throws Exception{
        try {
            if(valueHtml != null){
                Float temp = Float.valueOf(valueHtml);
                if(temp < 0) throw new Exception("Negative number");
                this.setCredit(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the value of the Credit");
        }
    }

    public CompteGeneral getCoge() throws Exception{
        if(this.getIdCoGe() != null){
            Connection connection = null;
            try {
                ConnectionBase cb = new ConnectionBase();
                connection = cb.dbConnect();
                CompteGeneral cj = new CompteGeneral(this.getIdCoGe(), null, null, null);
                return BddObject.findById(cj, connection);

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Error on getting the compte general of the contentJOurnal");
            } finally{
                connection.close();
            }
        }else{
            return null;
        }       
    }

    public void setCoge(CompteGeneral coge) {
        this.coge = coge;
    }

    public CompteTiers getTiers() throws Exception{
        if(this.getIdCoTiers()!= null){
            Connection connection = null;
            try {
                ConnectionBase cb = new ConnectionBase();
                connection = cb.dbConnect();
                CompteTiers cj = new CompteTiers(this.getIdCoTiers(), null, null, null);
                return BddObject.findById(cj, connection);

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Error on getting the compte tiers of the contentJOurnal");
            } finally{
                connection.close();
            }
        }else{
            return null;
        }
    }

    public void setTiers(CompteTiers tiers) {
        this.tiers = tiers;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Devise getDevise() throws Exception{
        if(this.getIdDevise()!= null){
            Connection connection = null;
            try {
                ConnectionBase cb = new ConnectionBase();
                connection = cb.dbConnect();
                Devise cj = new Devise(this.getIdDevise(), null, null, null);
                return BddObject.findById(cj, connection);

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Error on getting the devise of the contentJOurnal");
            } finally{
                connection.close();
            }
        }else{
            return null;
        }
    }

    public void setDevise(Devise devise) {
        this.devise = devise;
    }

    public Journal getJournal() throws Exception{
        if(this.getIdJournal()!= null){
            Connection connection = null;
            try {
                ConnectionBase cb = new ConnectionBase();
                connection = cb.dbConnect();
                Journal cj = new Journal();
                cj.setIdJournal(this.getIdJournal());
                return BddObject.findById(cj, connection);

            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Error on getting the journal of the contantJournal");
            } finally{
                connection.close();
            }
        }else{
            return null;
        }
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }
    
    public String getIdPiece() {
        return idPiece;
    }

    public void setIdPiece(String idPiece) {
        this.idPiece = idPiece;
    }
    
    
    
}
