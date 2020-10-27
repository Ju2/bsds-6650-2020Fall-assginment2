package server;

public class LiftRide {
  private String resortID;
  private Integer dayID;
  private Integer skierID;
  private Long time;
  private Integer liftID;
  private Integer liftVerticalRise;

  public LiftRide(String resortID, Integer dayID, Integer skierID, Long time,
      Integer liftID) {
    this.resortID = resortID;
    this.dayID = dayID;
    this.skierID = skierID;
    this.time = time;
    this.liftID = liftID;
  }

  public String getResortID() {
    return resortID;
  }

  public void setResortID(String resortID) {
    this.resortID = resortID;
  }

  public Integer getDayID() {
    return dayID;
  }

  public void setDayID(Integer dayID) {
    this.dayID = dayID;
  }

  public Integer getSkierID() {
    return skierID;
  }

  public void setSkierID(Integer skierID) {
    this.skierID = skierID;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public Integer getLiftID() {
    return liftID;
  }

  public void setLiftID(Integer liftID) {
    this.liftID = liftID;
  }

  public Integer getLiftVerticalRise() {
    return liftVerticalRise;
  }

  public void setLiftVerticalRise() {
    this.liftVerticalRise = this.liftID * 10;
  }

  @Override
  public String toString() {
    return "LiftRide{" +
        "resortID='" + resortID + '\'' +
        ", dayID=" + dayID +
        ", skierID=" + skierID +
        ", time=" + time +
        ", liftID=" + liftID +
        ", liftVerticalRise=" + liftVerticalRise +
        '}';
  }
}
