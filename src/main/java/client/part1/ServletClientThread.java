package client.part1;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The type Servlet client thread.
 */
public class ServletClientThread implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final String PRE_PATH = "http://";
  private static final String POST_PATH = ":8080/CS6650_assignment1_war_exploded";
  private static final String EC2_POST_PATH = ":8080/CS6650_assignment1_war";
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
  private ServletResult finalResult;
  private String urlAddress;

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
   * @param finalResult the final result
   * @param IPAddress the ip address
   */
  public ServletClientThread(Integer startSkierID, Integer endSkierID, Integer startTime,
      Integer endTime,
      String resortID, Integer dayID, Integer numLifts, CountDownLatch firstPhaseCountDown,
      CountDownLatch secondPhaseCountDown, Integer postRequestCount, Integer getRequestCount,
      ServletResult finalResult, String IPAddress) {
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
    this.finalResult = finalResult;
    if (IPAddress.equals("localhost")) {
      this.urlAddress = PRE_PATH + IPAddress + POST_PATH;
    } else {
      this.urlAddress = PRE_PATH + IPAddress + EC2_POST_PATH;
    }
  }

  /**
   * Gets start skier id.
   *
   * @return the start skier id
   */
  public Integer getStartSkierID() {
    return startSkierID;
  }

  /**
   * Gets end skier id.
   *
   * @return the end skier id
   */
  public Integer getEndSkierID() {
    return endSkierID;
  }

  /**
   * Gets start time.
   *
   * @return the start time
   */
  public Integer getStartTime() {
    return startTime;
  }

  /**
   * Gets end time.
   *
   * @return the end time
   */
  public Integer getEndTime() {
    return endTime;
  }

  /**
   * Gets resort id.
   *
   * @return the resort id
   */
  public String getResortID() {
    return resortID;
  }

  /**
   * Gets day id.
   *
   * @return the day id
   */
  public Integer getDayID() {
    return dayID;
  }

  /**
   *  send POST request and GET request by using the swagger API
   */
  @Override
  public void run() {
    Integer tempSkierID;
    Integer tempLiftID;
    Integer tempTime;
    SkiersApi apiInstance = new SkiersApi();
    ApiClient client = new ApiClient();
    client.setBasePath(this.urlAddress);
    apiInstance.setApiClient(client);
    Random random = new Random();
    for (int i = 0; i < this.postRequestCount; i++) {
      tempSkierID = random.nextInt(this.endSkierID + 1 - this.startSkierID) + this.startSkierID;
      tempLiftID = random.nextInt(this.numLifts) + 1;
      tempTime = random.nextInt(this.endTime + 1 - this.startTime) + this.startTime;
      this.postRequest(tempSkierID, tempLiftID, tempTime, apiInstance, this.finalResult);
    }

    for (int i = 0; i < this.getRequestCount; i++) {
      tempSkierID = random.nextInt(this.endSkierID + 1 - this.startSkierID) + this.startSkierID;
      this.getRequest(tempSkierID, apiInstance, this.finalResult);
    }

    if (this.secondPhaseCountDown != null) {
      this.secondPhaseCountDown.countDown();
    }
    this.firstPhaseCountDown.countDown();
  }

  /**
   * @param skierID the random skier ID
   * @param liftID the random lift ID
   * @param tempTime the random time
   * @param apiInstance the api instance to call the swagger api
   * @param finalResult the result which counts the number of successful request and failed request
   *
   * Using the swagger api to send post request to the server
   */
  private void postRequest(Integer skierID, Integer liftID, Integer tempTime, SkiersApi apiInstance,
      ServletResult finalResult) {
    LiftRide body = new LiftRide(); // LiftRide | information for new lift ride event
    body.setSkierID(skierID.toString());
    body.setLiftID(liftID.toString());
    body.setTime(tempTime.toString());
    try {
      apiInstance.writeNewLiftRide(body);
      finalResult.incSuccessRequest();
    } catch (ApiException e) {
      LOGGER.error(e);
      finalResult.incFailRequest();
    }
  }

  /**
   *
   * @param skierID the skier ID
   * @param apiInstance the api instance
   * @param finalResult the servlet result
   *
   * Send GET request by using the swagger API
   */
  private void getRequest(Integer skierID, SkiersApi apiInstance, ServletResult finalResult) {
    try {
      apiInstance.getSkierDayVertical(this.resortID, this.dayID.toString(), skierID.toString());
      finalResult.incSuccessRequest();
    } catch (ApiException e) {
      LOGGER.error(e);
      finalResult.incFailRequest();
    }
  }
}
