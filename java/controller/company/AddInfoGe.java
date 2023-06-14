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

/**
 *
 * @author rango
 */
@WebServlet(name = "AddInfoGe", urlPatterns = {"/AddInfoGe"})
@MultipartConfig  
public class AddInfoGe extends HttpServlet {
    
    private static final String UPLOAD_DIR = "./img";

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
            out.println("<title>Servlet AddInfoGe</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddInfoGe at " + request.getContextPath() + "</h1>");
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
        //processRequest(request, response);
        
        PrintWriter out = response.getWriter();

        String name = (String) request.getParameter("namesociety");        
        String activity = request.getParameter("activity");
        String capital = request.getParameter("capital");
        String addr = request.getParameter("addr");
        String creationDate = request.getParameter("datecreation");
        try {
            
             // Get the file part from the request
            Part filePart = request.getPart("logo");
            String fileName = InfoGeneral.uploadMyFile(filePart, UPLOAD_DIR, request);
            
            InfoGeneral ig = new InfoGeneral(null, name, activity, capital, creationDate, addr, fileName);
            BddObject.insertInDatabase(ig, null);
            response.sendRedirect("home.jsp?page=infoGeneral");
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispat = request.getRequestDispatcher("home.jsp");
            dispat.forward(request, response);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);  
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
