package server;

import java.sql.*;
import org.apache.commons.dbcp2.BasicDataSource;

public class TotalVerticalForResortDao {
  private static BasicDataSource dataSource;

  public TotalVerticalForResortDao() {
    dataSource  =  DBCPDataSource.getDataSource();
  }

  public Integer findTotalVerticalForResort (String resortID, String skierID) throws SQLException{
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "SELECT * FROM liftrides.LiftRides WHERE resortId = ? AND skierId = ?";
    conn = dataSource.getConnection();
    preparedStatement = conn.prepareStatement(insertQueryStatement);
    preparedStatement.setString(1, resortID);
    preparedStatement.setInt(2, Integer.parseInt(skierID));
    ResultSet resultSet = preparedStatement.executeQuery();
    Integer totalVerticalForSkier = 0;
    while (resultSet.next()) {
      Integer tempVertical = resultSet.getInt(7);
      totalVerticalForSkier += tempVertical;
    }
    conn.close();
    preparedStatement.close();
    return totalVerticalForSkier;
  }
}
