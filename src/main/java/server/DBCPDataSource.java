package server;

import org.apache.commons.dbcp2.*;

public class DBCPDataSource {
  private static BasicDataSource dataSource;

  // NEVER store sensitive information below in plain text!
//  private static final String HOST_NAME = System.getProperty("MySQL_IP_ADDRESS");
//  private static final String PORT = System.getProperty("MySQL_PORT");
//  private static final String DATABASE = "liftrides";
//  private static final String USERNAME = System.getProperty("DB_USERNAME");
//  private static final String PASSWORD = System.getProperty("DB_PASSWORD");
  private static final String RDS_HOSTNAME = System.getProperty("RDS_HOSTNAME");
  private static final String RDS_PORT = System.getProperty("RDS_PORT");
  private static final String RDS_DB_NAME = System.getProperty("RDS_DB_NAME");
  private static final String RDS_USERNAME = System.getProperty("RDS_USERNAME");
  private static final String RDS_PASSWORD = System.getProperty("RDS_PASSWORD");

  static {
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
    dataSource = new BasicDataSource();
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
//    System.out.println(HOST_NAME + " " + PORT + " " + USERNAME + " " + PASSWORD);
//    String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
    String url = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s", RDS_HOSTNAME, RDS_PORT, RDS_DB_NAME
    , RDS_USERNAME, RDS_PASSWORD);
    dataSource.setUrl(url);
//    dataSource.setUsername(USERNAME);
//    dataSource.setPassword(PASSWORD);
//    dataSource.setUsername(RDS_USERNAME);
//    dataSource.setPassword(RDS_PASSWORD);
    dataSource.setInitialSize(10);
    dataSource.setMaxTotal(300000);
  }

  public static BasicDataSource getDataSource() {
    return dataSource;
  }
}
