package controller;

import DAO.UserDAO;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import model.User;

@WebServlet(name = "UserServlet", value = "/UserServlet")
public class UserServlet extends HttpServlet {

  public static final long serialVersionUID = 1L;
  private UserDAO userDAO;

  public void inti() {
    userDAO = new UserDAO();
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String action = request.getParameter("action");
    if (action == null) {
      action = "";
      try {
        switch (action) {
          case "create":
            insertUser(request, response);
            break;
          case "edit":
            updateUser(request, response);
            break;
          case "search":
            searchByContry(request, response);
            break;
        }
      } catch (SQLException e) {
        throw new ServletException(e);
      }
    }
  }

  private void searchByContry(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String country = request.getParameter("country");
    List<User> listUser = userDAO.selectUserByCountry(country);
    request.setAttribute("listUser", listUser);
    RequestDispatcher dispatcher = request.getRequestDispatcher("view/list.jsp");
    dispatcher.forward(request, response);
  }


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String action = request.getParameter("action");
    if (action == null) {
      action = "";
    }
    try {
      switch (action) {
        case "create":
          showNewForm(request, response);
          break;
        case "edit":
          showEditForm(request, response);
          break;
        case "delete":
          deleteUser(request, response);
          break;
        case "sort":
          sortByName(request, response);
          break;
        case "permision":
          addUserPermision(request, response);
          break;
        case "search":
          searchByContry(request, response);
          break;
        case "test-without-tran":
          testWithoutTran(request, response);
          break;
        case "test-use-tran":
          testUseTran(request, response);
          break;
        default:
          listUser(request, response);
          break;
      }
    } catch (SQLException ex) {
      throw new ServletException(ex);
    }
  }

  private void testUseTran(HttpServletRequest request, HttpServletResponse response) {
    userDAO.insertUpdateUseTransaction();
  }

  private void testWithoutTran(HttpServletRequest request, HttpServletResponse response) {
    userDAO.insertUpdateWithoutTransaction();
  }

  private void addUserPermision(HttpServletRequest request, HttpServletResponse response) {
    User user = new User("quan", "quan.nguyen@codegym.vn", "vn");
    int[] permision = {1, 2, 4};
    userDAO.addUserTransaction(user, permision);
  }

  private void sortByName(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<User> listUser = userDAO.selectAllUserSort();
    request.setAttribute("listUser", listUser);
    RequestDispatcher dispatcher = request.getRequestDispatcher("view/list.jsp");
    dispatcher.forward(request, response);
  }

  private void listUser(HttpServletRequest request, HttpServletResponse response)
      throws SQLException, IOException, ServletException {
    List<User> listUser = userDAO.selectAllUser();
    request.setAttribute("listUser", listUser);
    RequestDispatcher dispatcher = request.getRequestDispatcher("view/list.jsp");
    dispatcher.forward(request, response);
  }

  private void showNewForm(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    RequestDispatcher dispatcher = request.getRequestDispatcher("view/create.jsp");
    dispatcher.forward(request, response);
  }

  private void showEditForm(HttpServletRequest request, HttpServletResponse response)
      throws SQLException, ServletException, IOException {
    int id = Integer.parseInt(request.getParameter("id"));

    User existingUser = userDAO.getUserById(id);
    RequestDispatcher dispatcher = request.getRequestDispatcher("view/edit.jsp");
    request.setAttribute("user", existingUser);
    dispatcher.forward(request, response);

  }

  private void insertUser(HttpServletRequest request, HttpServletResponse response)
      throws SQLException, IOException, ServletException {
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String country = request.getParameter("country");
    User newUser = new User(name, email, country);
    userDAO.insertUserStore(newUser);
    RequestDispatcher dispatcher = request.getRequestDispatcher("view/create.jsp");
    dispatcher.forward(request, response);
  }

  private void updateUser(HttpServletRequest request, HttpServletResponse response)
      throws SQLException, IOException, ServletException {
    int id = Integer.parseInt(request.getParameter("id"));
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String country = request.getParameter("country");

    User book = new User(id, name, email, country);
    userDAO.updateUser(book);
    RequestDispatcher dispatcher = request.getRequestDispatcher("view/edit.jsp");
    dispatcher.forward(request, response);
  }

  private void deleteUser(HttpServletRequest request, HttpServletResponse response)
      throws SQLException, IOException, ServletException {
    int id = Integer.parseInt(request.getParameter("id"));
    userDAO.deleteUser(id);
    listUser(request, response);
  }
}

