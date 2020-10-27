package server;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Skier servlet.
 */
@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

  protected void doPost(HttpServletRequest req,
      HttpServletResponse res)
      throws IOException {
    res.setContentType("application/json;charset:utf-8");
    String[] urlParts = getUrlPathList(req);
    Gson gson = new Gson();
    // check if we have a url
    if (urlParts == null) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write(gson.toJson(new ResponseContent("failure")));
      return;
    }
    // Validate url path and return the response status code
    if (!isPostRequestUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write(gson.toJson(new ResponseContent("failure")));
    } else {
      try {
        LiftRide requestLiftRide = new Gson().fromJson(req.getReader(), LiftRide.class);
        requestLiftRide.setLiftVerticalRise();
        NewLiftRideDao newLiftRideDao = new NewLiftRideDao();
        newLiftRideDao.createLiftRide(requestLiftRide);
        res.setStatus(HttpServletResponse.SC_OK);
        ResponseContent responseContent = new ResponseContent("success");
        res.getWriter().write(gson.toJson(responseContent));
      } catch (Exception e) {
        e.printStackTrace();
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ResponseContent responseContent = new ResponseContent("failure");
        res.getWriter().write(gson.toJson(responseContent));
      }
    }
  }

  protected void doGet(HttpServletRequest req,
      HttpServletResponse res)
      throws IOException {
    res.setContentType("application/json;charset:utf-8");
    String[] urlParts = getUrlPathList(req);
    if (urlParts == null) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    Gson gson = new Gson();
    // Validate url path and return the response status code
    SkierUrlEnum urlRequestType = isGetRequestUrlValid(urlParts);
    try {
      if (urlRequestType == null) {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        ResponseContent responseContent = new ResponseContent("failure");
        res.getWriter().write(gson.toJson(responseContent));
      } else if (urlRequestType == SkierUrlEnum.TOTAL_VERTICAL_FOR_SKIER) {
        TotalVerticalForSkierDao totalVerticalForSkierDao = new TotalVerticalForSkierDao();
        Integer totalVertical = totalVerticalForSkierDao.findTotalVerticalForSkier(urlParts[1], urlParts[3], urlParts[5]);
        TotalVerticalResponse response = new TotalVerticalResponse(urlParts[1], totalVertical);
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(gson.toJson(response));
      } else if (urlRequestType == SkierUrlEnum.TOTAL_VERTICAL_FOR_RESORT) {
        Map<String, String[]> parameters = req.getParameterMap();
        String resortID = parameters.get("resort")[0];
        TotalVerticalForResortDao totalVerticalForResortDao = new TotalVerticalForResortDao();
        Integer totalVertical = totalVerticalForResortDao.findTotalVerticalForResort(resortID, urlParts[1]);
        TotalVerticalResponse response = new TotalVerticalResponse(resortID, totalVertical);
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(gson.toJson(response));
      }else {
        res.setStatus(HttpServletResponse.SC_OK);
        ResponseContent responseContent = new ResponseContent("success");
        res.getWriter().write(gson.toJson(responseContent));
      }
    } catch (Exception e) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      ResponseContent responseContent = new ResponseContent(e.getMessage());
      res.getWriter().write(gson.toJson(responseContent));
    }

  }

  private String[] getUrlPathList(HttpServletRequest req) {
    String urlPath = req.getPathInfo();

    // check if we have a url
    if (urlPath == null || urlPath.isEmpty()) {
      return null;
    }
    return urlPath.split("/");
  }

  private boolean isPostRequestUrlValid(String[] urlPath) {
    // urlPath  = "/liftrides"
    return urlPath.length == 2 && urlPath[1].equals("liftrides");
  }

  private SkierUrlEnum isGetRequestUrlValid(String[] urlPath) {
    // urlPath  = "/day/top10vert"
    // urlPath  = "/{skierID}/vertical"
    // urlPath  = "/{resortID}/days/{dayID}/skiers/{skiersID}"
    if (urlPath.length == 3 && urlPath[1].equals("day") && urlPath[2].equals("top10vert")) {
      return SkierUrlEnum.RESORT;
    } else if (urlPath.length == 3 && urlPath[2].equals("vertical")) {
      try {
        Integer.parseInt(urlPath[1]);
        return SkierUrlEnum.TOTAL_VERTICAL_FOR_RESORT;
      } catch (NumberFormatException e) {
        return null;
      }
    } else if (urlPath.length == 6 && urlPath[2].equals("days") && urlPath[4].equals("skiers")) {
      try {
        int dayID = Integer.parseInt(urlPath[3]);
        if (dayID < 0 || dayID > 366) {
          return null;
        }
        Integer.parseInt(urlPath[5]);
        return SkierUrlEnum.TOTAL_VERTICAL_FOR_SKIER;
      } catch (NumberFormatException e) {
        return null;
      }
    } else {
      return null;
    }
  }
}
