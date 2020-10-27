package client.part1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SkierServletClient {
  private final static Integer POST_REQUEST = 100;
  private final static Integer GET_REQUEST = 5;
  private final static Integer THIRD_PHASE_GET_REQUEST = 10;
  private static final Logger LOGGER = LogManager.getLogger();
  public static void main(String[] args) throws Exception {
    Integer maxThreads = null;
    String IPAddress = null;
    Integer numSkiers = 50000;
    Integer numLifts = 40;
    Integer skiDays = 1;
    String resortID = "SilverMt"; // String | ID of the resort the skier is at
    Integer numThreadsInPhaseOneAndThree;
    CountDownLatch firstPhaseEndCounter;
    CountDownLatch secondPhaseStartCounter;
    CountDownLatch secondPhaseEndCounter;
    CountDownLatch thirdPhaseStartCounter;
    CountDownLatch thirdPhaseEndCounter;
    // Get command line arguments
    for (int i = 0; i < args.length; i++) {
      if (i % 2 != 0) {
        continue;
      } else if (i + 1 >= args.length || args[i + 1].charAt(0) == '-') {
        throw new CommandLineArgumentError("No enough argument in command line");
      } else if (args[i].equals("-i")) {
        resortID = args[i + 1];
        continue;
      } else if (args[i].equals("-p")) {
        IPAddress = args[i + 1];
        continue;
      }
      Integer tempValue;
      try {
        tempValue = Integer.parseInt(args[i + 1]);
      } catch (NumberFormatException e) {
        throw e;
      }
       if (args[i].equals("-t")) {
         maxThreads = tempValue;
         if (maxThreads <= 0) {
           throw new CommandLineArgumentError("The maxThreads is beyond the limitation");
         }
       } else if (args[i].equals("-s")) {
         numSkiers = tempValue;
         if (numSkiers < 0) {
           throw new CommandLineArgumentError("The numSkiers is beyond the limitation");
         }
       } else if (args[i].equals("-l")) {
         numLifts = tempValue;
         if (numLifts < 5 || numLifts > 60) {
           throw new CommandLineArgumentError("The numLifts is beyond the limitation");
         }
       } else if (args[i].equals("-d")) {
         skiDays = tempValue;
         if (skiDays < 0) {
           throw new CommandLineArgumentError("The numSkiers is beyond the limitation");
         }
       }
    }

    if (IPAddress == null || maxThreads == null) {
      throw new CommandLineArgumentError("The number of command line argument is not enough");
    }
    LOGGER.info("The command line input is: " + "maxThread: " + maxThreads.toString() +
        ", IP address: " + IPAddress + ", numSkiers: " + numSkiers.toString() +
        ", numLifts: " + numLifts.toString() + ", skiDays: " + skiDays.toString() + ", resortID: " + resortID);
    // Handle the situation which the max thread is less than 4
    if (maxThreads <= 4) {
      numThreadsInPhaseOneAndThree = maxThreads;
    } else {
      numThreadsInPhaseOneAndThree = maxThreads / 4;
    }
    // Create the count down latch for three phases
    firstPhaseEndCounter = new CountDownLatch(numThreadsInPhaseOneAndThree);
    secondPhaseStartCounter = new CountDownLatch(numThreadsInPhaseOneAndThree / 10);
    secondPhaseEndCounter = new CountDownLatch(maxThreads);
    thirdPhaseStartCounter = new CountDownLatch(maxThreads / 10);
    thirdPhaseEndCounter = new CountDownLatch(numThreadsInPhaseOneAndThree);
    Integer firstSkierIDFactor = numSkiers / numThreadsInPhaseOneAndThree;
    Integer secondSkierIDFactor = numSkiers / maxThreads;
    ServletResult finalResult = new ServletResult();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    Date date = new Date();
    LOGGER.info("The first phase start");
    LOGGER.info(formatter.format(date));
    Long startTime = System.currentTimeMillis();
    for (int i = 0; i < numThreadsInPhaseOneAndThree; i++) {
      Runnable thread = new ServletClientThread(i * firstSkierIDFactor + 1,
          (i + 1) * firstSkierIDFactor, 1, 90, resortID, skiDays, numLifts,
          firstPhaseEndCounter, secondPhaseStartCounter, POST_REQUEST, GET_REQUEST, finalResult, IPAddress);
      new Thread(thread).start();
    }
    // wait for the end of 10% first phase threads
    secondPhaseStartCounter.await();
    date = new Date();
    LOGGER.info("The second phase can start");
    LOGGER.info(formatter.format(date));
    for (int i = 0; i < maxThreads; i++) {
      Runnable thread = new ServletClientThread(i * secondSkierIDFactor + 1,
          (i + 1) * secondSkierIDFactor, 91, 360, resortID, skiDays, numLifts,
          secondPhaseEndCounter, thirdPhaseStartCounter, POST_REQUEST, GET_REQUEST, finalResult, IPAddress);
      new Thread(thread).start();
    }
    // wait for the end of 10% second phase threads
    thirdPhaseStartCounter.await();
    date = new Date();
    LOGGER.info("The third phase can start");
    LOGGER.info(formatter.format(date));
    for (int i = 0; i < numThreadsInPhaseOneAndThree; i++) {
      Runnable thread = new ServletClientThread(i * firstSkierIDFactor + 1,
          (i + 1) * firstSkierIDFactor, 361, 420, resortID, skiDays, numLifts,
          thirdPhaseEndCounter, null, POST_REQUEST, THIRD_PHASE_GET_REQUEST,
          finalResult, IPAddress);
      new Thread(thread).start();
    }
    // Wait for the end of three phases
    firstPhaseEndCounter.await();
    secondPhaseEndCounter.await();
    thirdPhaseEndCounter.await();
    Long wallTime = System.currentTimeMillis() - startTime;
    finalResult.setWallTime(wallTime);
    try {
      finalResult.calculateRequestPerSecond();
    } catch (IllegalArgumentException e) {
      LOGGER.error("The result is not correct, please check your input");
      return;
    }
    LOGGER.info(finalResult);
    LOGGER.info("The correct number of request is: " +
        (numThreadsInPhaseOneAndThree * (POST_REQUEST + GET_REQUEST) +
        maxThreads * (POST_REQUEST + GET_REQUEST) +
        numThreadsInPhaseOneAndThree * (POST_REQUEST + THIRD_PHASE_GET_REQUEST)));
    date = new Date();
    LOGGER.info("All phases are end");
    LOGGER.info(formatter.format(date));
  }
}
