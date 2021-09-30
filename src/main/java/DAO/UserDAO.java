package DAO;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserDAO implements IUserDAO {

  private String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false";
  private String jdbcUsername = "root";
  private String jdbcPassword = "hanh1234";
  private static final String INSERT_USERS_SQL =
      "INSERT INTO users" + "  (name, email, country) VALUES " +
          " (?, ?, ?);";
  private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id =?;";
  private static final String SELECT_ALL_USERS = "select * from users ";
  private static final String SELECT_ALL_USERS_SORT = "select * from users ORDER BY name ;";
  private static final String DELETE_USERS_SQL = "delete from users where id = ?;";
  private static final String UPDATE_USERS_SQL = "update users set name = ?,email= ?, country =? where id = ?;";
  private static final String SELECT_USER_BY_COUNTRY = "select * from users where country =?;";

  private static final String SQL_INSERT = "INSERT INTO EMPLOYEE (NAME, SALARY, CREATED_DATE) VALUES (?,?,?)";
  private static final String SQL_UPDATE = "UPDATE EMPLOYEE SET SALARY=? WHERE NAME=?";
  private static final String SQL_TABLE_CREATE = "CREATE TABLE EMPLOYEE"

      + "("

      + " ID serial,"

      + " NAME varchar(100) NOT NULL,"

      + " SALARY numeric(15, 2) NOT NULL,"

      + " CREATED_DATE timestamp,"

      + " PRIMARY KEY (ID)"

      + ")";

  private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS EMPLOYEE";

  public UserDAO() {
  }

  protected Connection getConnection() {
    Connection connection = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return connection;
  }

  @Override
  public void insertUser(User user) throws SQLException {
    System.out.println(INSERT_USERS_SQL);
    try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
        INSERT_USERS_SQL)) {
      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getEmail());
      System.out.println(preparedStatement);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public User selectUser(int id) {
    User user = null;
    try (Connection connection = getConnection()) {
      PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);
      preparedStatement.setInt(1, id);
      System.out.println(preparedStatement);
      ResultSet result = preparedStatement.executeQuery();
      while (result.next()) {
        String name = result.getString("name");
        String email = result.getString("email");
        String country = result.getString("country");
        user = new User(id, name, email, country);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return user;
  }

  @Override
  public List<User> selectAllUser() {
    List<User> users = new ArrayList<>();
    try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);) {
      System.out.println(preparedStatement);
      ResultSet rs = preparedStatement.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String country = rs.getString("country");
        users.add(new User(id, name, email, country));
      }
    } catch (SQLException e) {
      printSQLException(e);
    }
    return users;
  }


  @Override
  public List<User> selectAllUserSort() {
    List<User> users = new ArrayList<>();
    try (
        Connection connection = getConnection();
        CallableStatement callableStatement = connection.prepareCall("{call selectAllUser()}")
    ) {
      ResultSet resultSet = callableStatement.executeQuery();
      while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String country = resultSet.getString("country");
        users.add(new User(id, name, email, country));
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return users;
  }

  @Override
  public List<User> selectUserByCountry(String contry) {
    List<User> users = new ArrayList<>();
    try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
        SELECT_USER_BY_COUNTRY);) {
      preparedStatement.setString(1, contry);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String country = resultSet.getString("country");
        users.add(new User(id, name, email, country));
      }
    } catch (SQLException e) {
      printSQLException(e);
    }
    return users;
  }

  @Override
  public User getUserById(int id) {
    User user = null;
    String query = "{CALL get_users_by_id}";
    try (Connection connection = getConnection();
        CallableStatement callableStatement = connection.prepareCall(query);) {
      callableStatement.setInt(1, id);
      ResultSet resultSet = callableStatement.executeQuery();
      while (resultSet.next()) {
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String country = resultSet.getString("country");
        user = new User(id, name, email, country);
      }
    } catch (SQLException e) {
      printSQLException(e);
    }
    return user;
  }

  @Override
  public void insertUserStore(User user) throws SQLException {
    String query = "{CALL insert_user(?,?,?)}";
    try (Connection connection = getConnection();
        CallableStatement callableStatement = connection.prepareCall(query);) {
      callableStatement.setString(1, user.getName());
      callableStatement.setString(2, user.getEmail());
      callableStatement.setString(3, user.getCountry());
      callableStatement.executeUpdate();
    } catch (SQLException e) {
      printSQLException(e);
    }
  }

  @Override
  public void addUserTransaction(User user, int[] permission) {
    try (Connection connection = getConnection();
        PreparedStatement addUser = connection.prepareStatement(INSERT_USERS_SQL,
            Statement.RETURN_GENERATED_KEYS);
        PreparedStatement addPermission = connection.prepareStatement(
            "INSERT INTO user_permission(user_id, permission_id)VALUES(?,?)");) {
      connection.setAutoCommit(false);
      addUser.setString(1, user.getName());
      addUser.setString(2, user.getEmail());
      addUser.setString(3, user.getCountry());
      int checkAdd = addPermission.executeUpdate();
      ResultSet resultSet = addUser.getGeneratedKeys();
      if (checkAdd == 1 && resultSet.next()) {
        int idUser = resultSet.getInt(1);
        for (int Permission : permission) {
          addPermission.setInt(1, idUser);
          addPermission.setInt(2, Permission);
          addPermission.executeUpdate();
        }
        resultSet.close();
        connection.commit();
      } else {
        connection.rollback();
      }
    } catch (SQLException e) {
      printSQLException(e);
    }
  }

  @Override
  public void insertUpdateWithoutTransaction() {
    try {
      Connection connection = getConnection();
      Statement statement = connection.createStatement();
      PreparedStatement preInsert = connection.prepareCall(SQL_INSERT);
      PreparedStatement preUpdate = connection.prepareCall(SQL_UPDATE);
      statement.execute(SQL_TABLE_CREATE);
      statement.execute(SQL_TABLE_DROP);
      preInsert.setString(1, "Hanh");
      preInsert.setBigDecimal(2, new BigDecimal(10));
      preInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
      preInsert.execute();
      preUpdate.setBigDecimal(1, new BigDecimal(999.99));
      preUpdate.setString(2, "Hanh");
      preUpdate.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void insertUpdateUseTransaction() {
    try (Connection connection = getConnection();
        Statement statement = connection.createStatement();
        PreparedStatement preInsert = connection.prepareStatement(SQL_INSERT);
        PreparedStatement preUpdate = connection.prepareStatement(SQL_UPDATE)) {
      statement.execute(SQL_TABLE_DROP);
      statement.execute(SQL_TABLE_CREATE);
      connection.setAutoCommit(false);
      preInsert.setString(1, "Hanh");
      preInsert.setBigDecimal(2, new BigDecimal(10));
      preInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
      preInsert.execute();
      preInsert.setString(1, "MyNhat");
      preInsert.setBigDecimal(2, new BigDecimal(20));
      preInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
      preInsert.execute();
      preUpdate.setBigDecimal(2, new BigDecimal(10));
      preUpdate.setString(2, "Hanh");
      preInsert.execute();
      connection.commit();
      connection.setAutoCommit(true);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public boolean deleteUser(int id) throws SQLException {
    boolean rowDeleted;
    try (Connection connection = getConnection();
        CallableStatement callableStatement = connection.prepareCall("{call deleteUser(?)}")) {
      callableStatement.setInt(1, id);
      rowDeleted = callableStatement.execute();
    }
    return rowDeleted;
  }

  @Override
  public boolean updateUser(User user) throws SQLException {
    boolean rowUpdate;
    try (Connection connection = getConnection();
        CallableStatement callableStatement = connection.prepareCall(
            "{call updateUser(?,?,?,?)}")) {
      callableStatement.setString(1, user.getName());
      callableStatement.setString(2, user.getEmail());
      callableStatement.setString(3, user.getCountry());
      callableStatement.setInt(4, user.getId());
      rowUpdate = callableStatement.execute();
    }
    return rowUpdate;
  }

  private void printSQLException(SQLException exception) {
    for (Throwable throwable : exception) {
      if (throwable instanceof SQLException) {
        throwable.printStackTrace(System.err);
        System.err.println("SQLState: " + ((SQLException) throwable).getSQLState());
        System.err.println("Error code: " + ((SQLException) throwable).getErrorCode());
        System.err.println("Message: " + throwable.getMessage());
        Throwable t = exception.getCause();
        while (t != null) {
          System.out.println("Cause: " + t);
          t = t.getCause();
        }
      }
    }
  }
}
