package server;

import java.sql.*;
import java.util.UUID;
import org.apache.commons.dbcp2.BasicDataSource;

public class NewLiftRideDao {
  private static BasicDataSource dataSource;

  public NewLiftRideDao() {
    dataSource  =  DBCPDataSource.getDataSource();
  }

  public void createLiftRide (LiftRide newLiftRide) throws SQLException{
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO liftrides.LiftRides (uuid, resortID, dayID, skierID, time, liftID, liftVerticalRise) " +
        "VALUES (?,?,?,?,?,?,?)";
    String uuid = UUID.randomUUID().toString().replace("-", "");
    conn = dataSource.getConnection();
    preparedStatement = conn.prepareStatement(insertQueryStatement);
    preparedStatement.setString(1, uuid);
    preparedStatement.setString(2, newLiftRide.getResortID());
    preparedStatement.setInt(3, newLiftRide.getDayID());
    preparedStatement.setInt(4, newLiftRide.getSkierID());
    preparedStatement.setLong(5, newLiftRide.getTime());
    preparedStatement.setInt(6, newLiftRide.getLiftID());
    preparedStatement.setInt(7, newLiftRide.getLiftVerticalRise());
    preparedStatement.executeUpdate();
    conn.close();
    preparedStatement.close();
    }
  }

