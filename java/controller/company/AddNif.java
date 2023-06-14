/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.company;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import mapping.BddObject;
import model.company.InfoGeneral;
import model.company.Nif;

/**
 *
 * @author rango
 */
@WebServlet(name = "AddNif", urlPatterns = {"/AddNif"})
@MultipartConfig  
public class AddNif extends HttpServlet {

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
            out.println("<title>Servlet AddNif</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddNif at " + request.getContextPath() + "</h1>");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       // processRequest(request, response);
       
       String intitule = request.getParameter("intituleNif");
       String date = request.getParameter("dateNif");
       
        try {
            
            String path = "./img";
            Part filePart = request.getPart("nifphoto");
            String filename = "";
            if(filePart == null){
                Nif nif = Nif.lastNif(null);
                filename = nif.getPathNIf();
            } else {
                filename = InfoGeneral.uploadMyFile(filePart, path, request);
            }

                       
            Nif nif = new Nif(null, intitule, filename, date);
            BddObject.insertInDatabase(nif, null);
            response.sendRedirect("home.jsp?page=nifstat");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispat = request.getRequestDispatcher("home.jsp?page=nifstat");
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
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
