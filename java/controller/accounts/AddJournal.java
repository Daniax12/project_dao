/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.accounts;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mapping.BddObject;
import model.accounts.ContentJournal;
import model.accounts.Journal;
import model.company.Devise;

/**
 *
 * @author rango;
 */
@WebServlet(name = "AddJournal", urlPatterns = {"/AddJournal"})
public class AddJournal extends HttpServlet {

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
            out.println("<title>Servlet AddJournal</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddJournal at " + request.getContextPath() + "</h1>");
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
        
       String cojo = request.getParameter("idcojo");
       String idjournal = request.getParameter("idjournal");
       String date = request.getParameter("dateJournal");
       String pieceref = request.getParameter("pieceref");
       String piece = request.getParameter("piece");
       String idpiece = request.getParameter("selectedref");
       
       String idcompte = request.getParameter("idcompte");
       if(idcompte.equals("") == true) idcompte = null;
       
       String idtiers = request.getParameter("idtiers");
       if(idtiers.equals("") == true) idtiers = null;
       
       String libelle = request.getParameter("libelle");
       String action = request.getParameter("choix");
       // 0 => Debit
       // 1 => Credit
       
       String montant = request.getParameter("montant");
       String devise = request.getParameter("iddevise");
       
       String red = request.getParameter("redir");
              
        try {
            if(devise.equals("") == true){
                devise = Devise.myPrincipale(null).getIdDevise();
            }
            Journal journal = new Journal();
            journal.setIdJournal(idjournal);
            journal = BddObject.findById(journal, null);
            
            journal.setValide(1);
            BddObject.updatingObject(journal, null);
            
            ContentJournal cj = new ContentJournal();
            cj.setIdPiece(idpiece);
            cj.setRef(piece);
            cj.setDateJournal(date);
            cj.setIdCoGe(idcompte);
            cj.setIdCoTiers(idtiers);
            cj.setIdDevise(devise);
            cj.setLibelle(libelle);
            cj.setIdJournal(idjournal);
            
            if(action.equals("0") == true){
                cj.setDebit(montant);
            } else if(action.equals("1") == true){
                cj.setCredit(montant);
            } else{
                String redirect = "home.jsp?page=newjournal&&code="+ cojo +"&&etat=2";
                response.sendRedirect(redirect);
            }
            BddObject.insertInDatabase(cj, null);
            if(red.equals("mod") == true){
                response.sendRedirect("home.jsp?page=modifyecriture&&idcojo=" + cojo +"&&idjournal=" + idjournal);
            } else if(red.equals("new") == true){
                response.sendRedirect("home.jsp?page=newjournal&&idcojo="+ cojo +"&&idjournal=" +idjournal);
            }           
            
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispat = request.getRequestDispatcher("home.jsp?page=newecriture&&code="+ cojo);
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
