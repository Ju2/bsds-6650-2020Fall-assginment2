package client.part1;

/**
 * The type Servlet result.
 */
public class ServletResult {
  private int successRequest;
  private int failRequest;
  private Long wallTime;
  private Integer requestPerSecond;

  /**
   * Instantiates a new Servlet result.
   */
  public ServletResult() {
    this.successRequest = 0;
    this.failRequest = 0;
    this.wallTime = null;
    this.requestPerSecond = null;
  }

  /**
   * Gets success request.
   *
   * @return the success request
   */
  public Integer getSuccessRequest() {
    return successRequest;
  }

  /**
   * Gets fail request.
   *
   * @return the fail request
   */
  public Integer getFailRequest() {
    return failRequest;
  }

  /**
   * Gets wall time.
   *
   * @return the wall time
   */
  public Long getWallTime() {
    return wallTime;
  }

  /**
   * Gets request per second.
   *
   * @return the request per second
   */
  public Integer getRequestPerSecond() {
    return requestPerSecond;
  }

  /**
   * Increase the count of success request.
   */
  synchronized public void incSuccessRequest() {
      this.successRequest++;
  }

  /**
   * Increase the count of failure request.
   */
  synchronized public void incFailRequest() {
    this.failRequest++;
  }

  /**
   * Sets wall time.
   *
   * @param wallTime the wall time
   */
  public void setWallTime(Long wallTime) {
    this.wallTime = wallTime;
  }

  /**
   * Calculate request per second.
   */
  public void calculateRequestPerSecond() {
    if (this.wallTime == null || (this.successRequest == 0 && this.failRequest == 0)) {
      throw new IllegalArgumentException("There is no enough argument for the result");
    }
    long wallTimeSeconds = this.wallTime / 1000;
    this.requestPerSecond = (int)((this.successRequest + this.failRequest) / wallTimeSeconds);
  }

  @Override
  public String toString() {
    return "ServletResult: " +
        "successRequest = " + successRequest +
        ", failRequest = " + failRequest +
        ", wallTime = " + wallTime + "ms" +
        ", requestPerSecond = " + requestPerSecond +
        " request per second";
  }
}
