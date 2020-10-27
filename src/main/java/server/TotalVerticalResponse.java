package server;

public class TotalVerticalResponse {
  private String resortID;
  private Integer totalVert;

  public TotalVerticalResponse(String resortID, Integer totalVert) {
    this.resortID = resortID;
    this.totalVert = totalVert;
  }

  public String getResortID() {
    return resortID;
  }

  public void setResortID(String resortID) {
    this.resortID = resortID;
  }

  public Integer getTotalVert() {
    return totalVert;
  }

  public void setTotalVert(Integer totalVert) {
    this.totalVert = totalVert;
  }

  @Override
  public String toString() {
    return "TotalVerticalForSkierResponse{" +
        "resortID='" + resortID + '\'' +
        ", totalVert=" + totalVert +
        '}';
  }
}
