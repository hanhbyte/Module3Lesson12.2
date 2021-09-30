package DAO;

import java.sql.SQLException;
import java.util.List;
import model.User;

public interface IUserDAO {

  void insertUser(User user) throws SQLException;

  User selectUser(int id);

  List<User> selectAllUser();

  boolean deleteUser(int id) throws SQLException;

  boolean updateUser(User user) throws SQLException;

  List<User> selectAllUserSort();

  List<User> selectUserByCountry(String country);

  User getUserById(int id);

  void insertUserStore(User user) throws SQLException;

  void addUserTransaction(User user, int[] permission);

  void insertUpdateWithoutTransaction();

  void insertUpdateUseTransaction();
}
