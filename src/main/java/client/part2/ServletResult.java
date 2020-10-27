package client.part2;

/**
 * The type Servlet result.
 */
public class ServletResult {
  private Integer totalRequestCount;
  private Long totalWallTime;
  private Long postMeanResponseTime;
  private Long getSkierDayVerticalMeanResponseTime;
  private Long getSkierResortTotalMeanResponseTime;
  private Long postMedianResponseTime;
  private Long getSkierDayVerticalMedianResponseTime;
  private Long getSkierResortTotalMedianResponseTime;
  private Integer throughput;
  private Long postP99ResponseTime;
  private Long getSkierDayVerticalP99ResponseTime;
  private Long getSkierResortTotalP99ResponseTime;
  private Long postMaxResponseTime;
  private Long getSkierDayVerticalMaxResponseTime;
  private Long getSkierResortTotalMaxResponseTime;

  public ServletResult(Integer totalRequestCount, Long totalWallTime,
      Long postMeanResponseTime, Long getSkierDayVerticalMeanResponseTime,
      Long getSkierResortTotalMeanResponseTime, Long postMedianResponseTime,
      Long getSkierDayVerticalMedianResponseTime,
      Long getSkierResortTotalMedianResponseTime,
      Long postP99ResponseTime, Long getSkierDayVerticalP99ResponseTime,
      Long getSkierResortTotalP99ResponseTime, Long postMaxResponseTime,
      Long getSkierDayVerticalMaxResponseTime, Long getSkierResortTotalMaxResponseTime) {
    this.totalRequestCount = totalRequestCount;
    this.totalWallTime = totalWallTime;
    this.postMeanResponseTime = postMeanResponseTime;
    this.getSkierDayVerticalMeanResponseTime = getSkierDayVerticalMeanResponseTime;
    this.getSkierResortTotalMeanResponseTime = getSkierResortTotalMeanResponseTime;
    this.postMedianResponseTime = postMedianResponseTime;
    this.getSkierDayVerticalMedianResponseTime = getSkierDayVerticalMedianResponseTime;
    this.getSkierResortTotalMedianResponseTime = getSkierResortTotalMedianResponseTime;
    this.postP99ResponseTime = postP99ResponseTime;
    this.getSkierDayVerticalP99ResponseTime = getSkierDayVerticalP99ResponseTime;
    this.getSkierResortTotalP99ResponseTime = getSkierResortTotalP99ResponseTime;
    this.postMaxResponseTime = postMaxResponseTime;
    this.getSkierDayVerticalMaxResponseTime = getSkierDayVerticalMaxResponseTime;
    this.getSkierResortTotalMaxResponseTime = getSkierResortTotalMaxResponseTime;
  }

  /**
   * Calculate the through out for the result.
   */
  public void calThroughput () {
    this.throughput = this.totalRequestCount / (this.totalWallTime.intValue() / 1000);
  }

  @Override
  public String toString() {
    return "*****************Final Result******************\n" +
        "Total Request Count = " + totalRequestCount + "\n" +
        "Total Wall Time =  " + totalWallTime + "ms\n" +
        "POST Request Mean Response Time = " + postMeanResponseTime + "ms\n" +
        "Get Skier Day Vertical Request Mean Response Time = " + this.getSkierDayVerticalMeanResponseTime + "ms\n" +
        "Get Skier Resort Total Request Mean Response Time = " + this.getSkierResortTotalMeanResponseTime + "ms\n" +
        "POST Request Median Response Time = " + postMedianResponseTime + "ms\n" +
        "Get Skier Day Vertical Request Median Response Time = " + this.getSkierDayVerticalMedianResponseTime + "ms\n" +
        "Get Skier Resort Total Request Median Response Time = " + this.getSkierResortTotalMedianResponseTime + "ms\n" +
        "throughput = " + throughput + " request per second\n" +
        "POST Request P99 Response Time = " + postP99ResponseTime + "ms\n" +
        "Get Skier Day Vertical Request P99 Response Time = " + this.getSkierDayVerticalP99ResponseTime + "ms\n" +
        "Get Skier Resort Total Request P99 Response Time = " + this.getSkierResortTotalP99ResponseTime + "ms\n" +
        "POST Request Max Response Time = " + postMaxResponseTime + "ms\n" +
        "Get Skier Day Vertical Request Max Response Time = " + this.getSkierDayVerticalMaxResponseTime + "ms\n" +
        "Get Skier Resort Total Request Max Response Time = " + this.getSkierResortTotalMaxResponseTime + "ms\n" +
        "*****************************************";
  }
}
