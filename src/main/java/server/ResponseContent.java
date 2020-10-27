package server;

/**
 * The type Response content.
 */
public class ResponseContent {
  private String message;

  /**
   * Instantiates a new Response content.
   *
   * @param message the message
   */
  public ResponseContent(String message) {
    this.message = message;
  }

  /**
   * Gets message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets message.
   *
   * @param message the message
   */
  public void setMessage(String message) {
    this.message = message;
  }
}
