package client.part2;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.SkierVertical;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The servlet client thread
 */
public class ServletClientThread implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String PRE_PATH = "http://";
  private static final String POST_PATH = ":8080/CS6650_assignment2_war_exploded";
  private static final String EC2_POST_PATH = ":8080/CS6650_assignment2_war";
  private Integer startSkierID;
  private Integer endSkierID;
  private Integer startTime;
  private Integer endTime;
  private String resortID;
  private Integer dayID;
  private Integer numLifts;
  private CountDownLatch firstPhaseCountDown;
  private CountDownLatch secondPhaseCountDown;
  private Integer postRequestCount;
  private Integer getRequestCount;
  private String urlAddress;
  private BufferedWriter bufferedWriter;

  /**
   * Instantiates a new Servlet client thread.
   *
   * @param startSkierID the start skier id
   * @param endSkierID the end skier id
   * @param startTime the start time
   * @param endTime the end time
   * @param resortID the resort id
   * @param dayID the day id
   * @param numLifts the num lifts
   * @param firstPhaseCountDown the first phase count down
   * @param secondPhaseCountDown the second phase count down
   * @param postRequestCount the post request count
   * @param getRequestCount the get request count
   * @param IPAddress the ip address
   * @param bufferedWriter the buffered writer
   */
  public ServletClientThread(Integer startSkierID, Integer endSkierID, Integer startTime,
      Integer endTime,
      String resortID, Integer dayID, Integer numLifts, CountDownLatch firstPhaseCountDown,
      CountDownLatch secondPhaseCountDown, Integer postRequestCount, Integer getRequestCount,
      String IPAddress, BufferedWriter bufferedWriter) {
    this.startSkierID = startSkierID;
    this.endSkierID = endSkierID;
    this.startTime = startTime;
    this.endTime = endTime;
    this.resortID = resortID;
    this.dayID = dayID;
    this.numLifts = numLifts;
    this.firstPhaseCountDown = firstPhaseCountDown;
    this.secondPhaseCountDown = secondPhaseCountDown;
    this.postRequestCount = postRequestCount;
    this.getRequestCount = getRequestCount;
    this.bufferedWriter = bufferedWriter;
    if (IPAddress.equals("localhost")) {
      this.urlAddress = PRE_PATH + IPAddress + POST_PATH;
    } else {
      this.urlAddress = PRE_PATH + IPAddress + EC2_POST_PATH;
    }

  }

  @Override
  public void run() {
    Integer tempSkierID;
    Integer tempLiftID;
    Integer tempTime;
    Long startTime;
    Long latency;
    Integer responseCode;
    SkiersApi apiInstance = new SkiersApi();
    ApiClient client = new ApiClient();
    client.setBasePath(this.urlAddress);
    apiInstance.setApiClient(client);
    // Send 100 post requests to the server.
    for (int i = 0; i < this.postRequestCount; i++) {
      tempSkierID = ThreadLocalRandom.current().nextInt(this.startSkierID, this.endSkierID + 1);
      tempLiftID = ThreadLocalRandom.current().nextInt(1, this.numLifts + 1);
      tempTime = ThreadLocalRandom.current().nextInt(this.startTime, this.endTime + 1);
      startTime = System.currentTimeMillis();
      responseCode = this.postRequest(tempSkierID, tempLiftID, tempTime, apiInstance);
      latency = System.currentTimeMillis() - startTime;
      this.writeToFile("Start Time: " + startTime.toString() + ", Request Type: POST, Latency: " +
          latency.toString() + ", Response Code: " + responseCode.toString(), this.bufferedWriter);
    }
//     Send 5 or 10 get Requests to the server
    for (int i = 0; i < this.getRequestCount; i++) {
      tempSkierID = ThreadLocalRandom.current().nextInt(this.startSkierID, this.endSkierID + 1);
      startTime = System.currentTimeMillis();
      responseCode = this.getRequest(tempSkierID, apiInstance);
      latency = System.currentTimeMillis() - startTime;
      this.writeToFile("Start Time: " + startTime.toString() + ", Request Type: GET_SKIER_DAY_VERTICAL, Latency: " +
          latency.toString() + ", Response Code: " + responseCode.toString(), this.bufferedWriter);
    }

    if (this.getRequestCount == 10) {
      for (int i = 0; i < this.getRequestCount; i ++) {
        tempSkierID = ThreadLocalRandom.current().nextInt(this.startSkierID, this.endSkierID + 1);
        startTime = System.currentTimeMillis();
        responseCode = this.getVerticalForResort(tempSkierID, apiInstance);
        latency = System.currentTimeMillis() - startTime;
        this.writeToFile("Start Time: " + startTime.toString() + ", Request Type: GET_SKIER_RESORT_TOTAL, Latency: " +
            latency.toString() + ", Response Code: " + responseCode.toString(), this.bufferedWriter);
      }
    }

    if (this.secondPhaseCountDown != null) {
      this.secondPhaseCountDown.countDown();
    }
    this.firstPhaseCountDown.countDown();
  }

  /**
   *
   * @param skierID
   * @param liftID
   * @param threadTime
   * @param apiInstance
   * @return the response status code
   */
  private Integer postRequest(Integer skierID, Integer liftID, Integer threadTime,
      SkiersApi apiInstance) {
    LiftRide body = new LiftRide(); // LiftRide | information for new lift ride event
    body.setSkierID(skierID.toString());
    body.setLiftID(liftID.toString());
    body.setTime(threadTime.toString());
    body.setResortID(this.resortID);
    body.setDayID(this.dayID.toString() );
    try {
      ApiResponse<Void> response = apiInstance.writeNewLiftRideWithHttpInfo(body);
      return response.getStatusCode();
    } catch (ApiException e) {
      e.printStackTrace();
      LOGGER.error(e);
      return e.getCode();
    }
  }

  /**
   *
   * @param skierID the skier's ID
   * @param apiInstance api instance in the current thread
   * @return the response status code
   * Send the get request to the server and return the response status code.
   */
  private Integer getRequest(Integer skierID, SkiersApi apiInstance) {
    try {
      ApiResponse<SkierVertical> response = apiInstance
          .getSkierDayVerticalWithHttpInfo(this.resortID, this.dayID.toString(),
              skierID.toString());
      return response.getStatusCode();
    } catch (ApiException e) {
      e.printStackTrace();
      LOGGER.error(e);
      return e.getCode();
    }
  }

  private Integer getVerticalForResort (Integer skierID, SkiersApi apiInstance) {
    try {
      List<String> tempResortIDList = new ArrayList<>();
      tempResortIDList.add(this.resortID);
      ApiResponse<SkierVertical> response = apiInstance
          .getSkierResortTotalsWithHttpInfo(Integer.toString(skierID), tempResortIDList);
      return response.getStatusCode();
    } catch (ApiException e) {
      e.printStackTrace();
      LOGGER.error(e);
      return e.getCode();
    }
  }

  private synchronized void writeToFile(String message, BufferedWriter bufferedWriter) {
    try {
      bufferedWriter.write(message + "\n");
    } catch (IOException e) {
      e.printStackTrace();
      LOGGER.error(e);
    }
  }
}
