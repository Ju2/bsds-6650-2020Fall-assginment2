package client.part2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The type Skier servlet client.
 */
public class SkierServletClient {
  private final static Integer POST_REQUEST = 1000;
  private final static Integer GET_REQUEST = 5;
  private final static Integer THIRD_PHASE_GET_REQUEST = 10;
  private final static Logger LOGGER = LogManager.getLogger();
  private final static String OUTPUT_FILE_NAME = "ResponseRecord.csv";

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
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
    BufferedWriter bufferedWriter;
    ServletResult servletResult;
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
      switch (args[i]) {
        case "-t":
          maxThreads = tempValue;
          if (maxThreads <= 0) {
            throw new CommandLineArgumentError("The maxThreads is beyond the limitation");
          }
          break;
        case "-s":
          numSkiers = tempValue;
          if (numSkiers < 0) {
            throw new CommandLineArgumentError("The numSkiers is beyond the limitation");
          }
          break;
        case "-l":
          numLifts = tempValue;
          if (numLifts < 5 || numLifts > 60) {
            throw new CommandLineArgumentError("The numLifts is beyond the limitation");
          }
          break;
        case "-d":
          skiDays = tempValue;
          if (skiDays < 0) {
            throw new CommandLineArgumentError("The numSkiers is beyond the limitation");
          }
          break;
      }
    }

    if (IPAddress == null || maxThreads == null) {
      throw new CommandLineArgumentError("The number of command line argument is not enough");
    }
    LOGGER.info("The command line input is: " + "maxThread: " + maxThreads.toString() +
        ", IP address: " + IPAddress + ", numSkiers: " + numSkiers.toString() +
        ", numLifts: " + numLifts.toString() + ", skiDays: " + skiDays.toString() + ", resortID: " + resortID);
    if (maxThreads <= 4) {
      numThreadsInPhaseOneAndThree = maxThreads;
    } else {
      numThreadsInPhaseOneAndThree = maxThreads / 4;
    }
    firstPhaseEndCounter = new CountDownLatch(numThreadsInPhaseOneAndThree);
    secondPhaseStartCounter = new CountDownLatch(numThreadsInPhaseOneAndThree / 10);
    secondPhaseEndCounter = new CountDownLatch(maxThreads);
    thirdPhaseStartCounter = new CountDownLatch(maxThreads / 10);
    thirdPhaseEndCounter = new CountDownLatch(numThreadsInPhaseOneAndThree);
    Integer firstPhaseTempSkierID = numSkiers / numThreadsInPhaseOneAndThree;
    Integer secondPhaseTempSkierID = numSkiers / maxThreads;
    try {
      File responseRecordFile = new File(OUTPUT_FILE_NAME);
      Files.deleteIfExists(responseRecordFile.toPath());
      responseRecordFile.createNewFile();
      bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME, true));
    } catch (IOException e) {
      LOGGER.error("Can't write to this file, please check the file name.");
      return;
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    LOGGER.info("The first phase starts.");
    LOGGER.info(formatter.format(date));
    Long startTime = System.currentTimeMillis();
    servletClientPhase(firstPhaseTempSkierID, 1, 90, resortID, skiDays, numLifts,
        firstPhaseEndCounter, secondPhaseStartCounter, POST_REQUEST, GET_REQUEST,
        IPAddress, numThreadsInPhaseOneAndThree, bufferedWriter);

    date = new Date();
    LOGGER.info("The second phase starts");
    LOGGER.info(formatter.format(date));

    servletClientPhase(secondPhaseTempSkierID, 91, 360, resortID, skiDays, numLifts,
        secondPhaseEndCounter, thirdPhaseStartCounter, POST_REQUEST, GET_REQUEST,
        IPAddress, maxThreads, bufferedWriter);

    date = new Date();
    LOGGER.info("The third phase starts");
    LOGGER.info(formatter.format(date));

    servletClientPhase(firstPhaseTempSkierID, 361, 420, resortID, skiDays, numLifts,
        thirdPhaseEndCounter, null, POST_REQUEST, THIRD_PHASE_GET_REQUEST,
        IPAddress, numThreadsInPhaseOneAndThree, bufferedWriter);

    firstPhaseEndCounter.await();
    secondPhaseEndCounter.await();
    thirdPhaseEndCounter.await();
    bufferedWriter.close();
    Long wallTime = System.currentTimeMillis() - startTime;
    servletResult = getFinalResult(wallTime);
    LOGGER.info(servletResult);
    LOGGER.info("The correct number of request is: " +
        (numThreadsInPhaseOneAndThree * (POST_REQUEST + GET_REQUEST) +
        maxThreads * (POST_REQUEST + GET_REQUEST) +
        numThreadsInPhaseOneAndThree * (POST_REQUEST + THIRD_PHASE_GET_REQUEST * 2)));
    date = new Date();
    LOGGER.info("All phases end");
    LOGGER.info(formatter.format(date));
  }

  /**
   *
   * @param factor the factor of the skier ID
   * @param startTime the start of the time
   * @param endTime the end of the time
   * @param resortID the resort ID
   * @param dayID the day ID
   * @param numLifts the number of lifts
   * @param firstPhaseCountDown first phase count down latch
   * @param secondPhaseCountDown second phase count down latch
   * @param postRequestCount post request count requirement
   * @param getRequestCount get request count requirement
   * @param IPAddress IP address
   * @param numThread number of the threads
   * @param bufferedWriter the buffered writer to write the final .csv file
   */
  private static void servletClientPhase (Integer factor, Integer startTime, Integer endTime,
      String resortID, Integer dayID, Integer numLifts, CountDownLatch firstPhaseCountDown,
      CountDownLatch secondPhaseCountDown, Integer postRequestCount, Integer getRequestCount,
      String IPAddress, Integer numThread, BufferedWriter bufferedWriter) {
    for (int i = 0; i < numThread; i++) {
      Runnable thread = new ServletClientThread(i * factor + 1,
          (i + 1) * factor, startTime, endTime, resortID, dayID, numLifts,
          firstPhaseCountDown, secondPhaseCountDown, postRequestCount, getRequestCount,
          IPAddress, bufferedWriter);
      new Thread(thread).start();
    }
    if (secondPhaseCountDown == null) {
      return;
    }
    try {
      secondPhaseCountDown.await();
    } catch (InterruptedException e ) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param wallTime the total wall time
   * @return Servlet Result
   * Read the csv file, get the status code, request type, latency. Calculate the mean, p99, median
   * of the request latency.
   */
  private static ServletResult getFinalResult (Long wallTime) {
    String line;
    String requestType = "null";
    Long latency = 0L;
    List<Long> postResponseList = new ArrayList<>();
    List<Long> getSkierDayVerticalResponseList = new ArrayList<>();
    List<Long> getSkierResortTotalResponseList = new ArrayList<>();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(OUTPUT_FILE_NAME))) {
      while ((line = bufferedReader.readLine()) != null) {
        String[] results = line.split(", ");
        for (String result: results) {
          String[] temp = result.split(": ");
          switch (temp[0]) {
            case "Request Type":
              requestType = temp[1];
              break;
            case "Latency":
              latency = Long.parseLong(temp[1]);
              break;
          }
        }
        if (requestType.equals("POST")) {
          postResponseList.add(latency);
        } else if (requestType.equals("GET_SKIER_DAY_VERTICAL")) {
          getSkierDayVerticalResponseList.add(latency);
        } else if (requestType.equals("GET_SKIER_RESORT_TOTAL")) {
          getSkierResortTotalResponseList.add(latency);
        }
      }
    } catch (Exception e) {
      LOGGER.error(e);
      return null;
    }
    Collections.sort(postResponseList);
    Collections.sort(getSkierDayVerticalResponseList);
    Collections.sort(getSkierResortTotalResponseList);
    try {
      ServletResult servletResult = new ServletResult(
          postResponseList.size() + getSkierDayVerticalResponseList.size() + getSkierResortTotalResponseList.size(),
          wallTime, postResponseList.stream().mapToLong(Long::longValue).sum() / postResponseList.size(),
          getSkierDayVerticalResponseList.stream().mapToLong(Long::longValue).sum() / getSkierDayVerticalResponseList.size(),
          getSkierResortTotalResponseList.stream().mapToLong(Long::longValue).sum() / getSkierResortTotalResponseList.size(),
          postResponseList.get(postResponseList.size() / 2),
          getSkierDayVerticalResponseList.get(getSkierDayVerticalResponseList.size() / 2),
          getSkierResortTotalResponseList.get(getSkierResortTotalResponseList.size() / 2),
          postResponseList.get(postResponseList.size() * 99 / 100),
          getSkierDayVerticalResponseList.get(getSkierDayVerticalResponseList.size() * 99 / 100),
          getSkierResortTotalResponseList.get(getSkierResortTotalResponseList.size() * 99 / 100),
          postResponseList.get(postResponseList.size() - 1),
          getSkierDayVerticalResponseList.get(getSkierDayVerticalResponseList.size() - 1),
          getSkierResortTotalResponseList.get(getSkierResortTotalResponseList.size() - 1)
      );
      servletResult.calThroughput();
      return servletResult;
    } catch (Exception e) {
      LOGGER.error(e);
      return null;
    }
  }
}
