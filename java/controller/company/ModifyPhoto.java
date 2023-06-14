/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
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
import model.company.Rcs;
/**
 *
 * @author rango
 */
@WebServlet(name = "ModifyPhoto", urlPatterns = {"/ModifyPhoto"})
@MultipartConfig
public class ModifyPhoto extends HttpServlet {

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
            out.println("<title>Servlet ModifyPhoto</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ModifyPhoto at " + request.getContextPath() + "</h1>");
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
        String action = request.getParameter("action");
        
        String path = "./img";
        
        try {
            
            // Get the file part from the request
            Part filePart = request.getPart("photo");

            // Generate a unique filename
            String fileName = InfoGeneral.uploadMyFile(filePart, path, request);
            String id = request.getParameter("id");
            
            if(action.equals("logo") == true){
                InfoGeneral ig = new InfoGeneral();
                ig.setIdInfoGe(id);
                ig = BddObject.findById(ig, null);
                ig.setLogoPath(fileName);
                BddObject.updatingObject(ig, null);
                response.sendRedirect("home.jsp?page=infoGeneral");
            } else if(action.equals("nif") == true){
                Nif nif = BddObject.findById(new Nif(id, null, null, null), null);
                nif.setPathNIf(fileName);
                BddObject.updatingObject(nif, null);
                response.sendRedirect("home.jsp?page=nifstat");
            } else if(action.equals("rcs") == true){
                Rcs rcs = BddObject.findById(new Rcs(id, null, null, null), null);
                rcs.setPathRcs(fileName);
                BddObject.updatingObject(rcs, null);
                response.sendRedirect("home.jsp?page=nifstat");
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispat = request.getRequestDispatcher("home.jsp");
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
