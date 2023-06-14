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
import java.util.Calendar;
import mapping.BddObject;
import model.accounts.ContentJournal;
import model.company.Devise;
import model.company.DeviseChange;
import utilities.DateUtil;

/**
 *
 * @author rango
 */
@WebServlet(name = "DeleteContent", urlPatterns = {"/DeleteContent"})
public class DeleteContent extends HttpServlet {

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
            out.println("<title>Servlet MajDevise</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MajDevise at " + request.getContextPath() + "</h1>");
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
        //processRequest(request, response);
            String idContent = request.getParameter("idcontent");
            String idCojou = request.getParameter("idcojo");
            String idjournal = request.getParameter("idjournal");
            String red = request.getParameter("red");
            
            try {
                ContentJournal cj = new ContentJournal();
                cj.setIdJournal(idjournal);
                cj.setIdContentJ(idContent);
                
                cj.deleteContent(null);
                if(red.equals("new") == true){
                    response.sendRedirect("home.jsp?page=newjournal&&idcojo=" + idCojou + "&&idjournal=" + idjournal);
                } else if(red.equals("mod") == true){
                    response.sendRedirect("home.jsp?page=modifyecriture&&idcojo=" + idCojou +"&&idjournal=" + idjournal);
                }
                
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
                RequestDispatcher dispat = request.getRequestDispatcher("home.jsp?page=newjournal&&idcojo=" + idCojou + "&&idjournal=" + idjournal + "&&etat=3");
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
