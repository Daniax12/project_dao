/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.company;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import mapping.BddObject;
import model.accounts.CompteGeneral;
import model.accounts.RacineCompte;

/**
 *
 * @author rango;
 */
@WebServlet(name = "ModifyCoge", urlPatterns = {"/ModifyCoge"})
public class ModifyCoge extends HttpServlet {

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
            out.println("<title>Servlet ModifyCoge</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ModifyCoge at " + request.getContextPath() + "</h1>");
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
       // processRequest(request, response);
         
       String id = request.getParameter("idcoge");
       String numero = request.getParameter("numero");
       String intitule = request.getParameter("intitule");     
       String idRacine = request.getParameter("idRacine");
       
        try {
            RacineCompte rc = BddObject.findById(new RacineCompte(idRacine, null, null), null);
            String theNumero = rc.getNumero() + numero;
            
            CompteGeneral temp = new CompteGeneral(id, null,null, null);
            CompteGeneral old = BddObject.findById(temp, null);
            CompteGeneral cg = new CompteGeneral(id, idRacine,  theNumero, intitule);
            cg.updateComptes(old ,null);
            response.sendRedirect("home.jsp?page=compteGeneral&&etat=1");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispat = request.getRequestDispatcher("home.jsp?page=compteGeneral");
            dispat.forward(request, response);
        }   
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
        processRequest(request, response);
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
