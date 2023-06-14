/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.accounts;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import model.accounts.CompteGeneral;
import model.accounts.CompteTiers;
import model.accounts.ContentJournal;
import model.accounts.PieceRef;
import model.company.Devise;

/**
 *
 * @author rango
 */
@WebServlet(name = "ImportEcriture", urlPatterns = {"/ImportEcriture"})
@MultipartConfig
public class ImportEcriture extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ImportEcriture</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ImportEcriture at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        
        String newJournal = request.getParameter("idjournalEcriture");
        String cojo = request.getParameter("idcojo");
        
         Part filePart = request.getPart("file");
         
        try  {
            PrintWriter out = response.getWriter();
            InputStream is = filePart.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            boolean firstLine = true;
            List<ContentJournal> all = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                 if (firstLine) {
                    firstLine = false;
                    continue; // skip the first line
                }
                String[] fields = line.split(";");
                
//                for(int i = 0; i < fields.length; i++){
//                    out.println(i + " - str is "+fields[i]);
//                }
//                 out.println("--------------------------------------------------");
                
                String date = fields[0].trim();
                String refCode = fields[1].trim();
                String piece = fields[2].trim();
                String compteCode = fields[3].trim();
                String tiersCode = fields[4].trim();
                String intitule = fields[5].trim();
                String libelle = fields[6].trim();
                String debit = fields[7].trim();
                String credit = "0.0";
                if(fields.length == 9) credit = fields[8].trim();
                
                // toAdd
                ContentJournal cj = new ContentJournal();
                cj.setIdJournal(newJournal);
                
                Devise main = Devise.myPrincipale(null);
                cj.setIdDevise(main.getIdDevise());
                                
                // Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsedDate = dateFormat.parse(date); // parse the string as a java.util.Date
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                cj.setDateJournal(sqlDate);
                
                // Reference Piece
                PieceRef pr = PieceRef.pieceByCode(refCode, null);
                if (pr != null) cj.setIdPiece(pr.getIdPiece());
                
                // Piece
                cj.setRef(piece);
                
                // Code general
                CompteGeneral cog = CompteGeneral.compteByNumero(compteCode, null);
                if(cog != null) cj.setIdCoGe(cog.getIdCoGe());
                
                // Tiers
                CompteTiers ct = CompteTiers.tiersByIntitule(tiersCode, null);
                if(ct != null) cj.setIdCoTiers(ct.getIdCoTiers());
                
                // intitule
                cj.setLibelle(libelle);
                
                // credit and debit
                float the_debit =  ContentJournal.convertToFloat(debit);           
                float the_credit = ContentJournal.convertToFloat(credit);
                
                cj.setCredit(the_credit);
                cj.setDebit(the_debit);
                
                all.add(cj);
            }
            
            ContentJournal.insertListContent(all, null);
            response.sendRedirect("home.jsp?page=newjournal&&idjournal=" + newJournal + "&&idcojo=" + cojo);
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispat = request.getRequestDispatcher("home.jsp?page=newjournal&&idjournal=" + newJournal + "&&idcojo=" + cojo);
            dispat.forward(request, response);
        } 
        
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
