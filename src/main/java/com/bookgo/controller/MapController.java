package com.bookgo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import com.bookgo.service.LibraryService;
import com.bookgo.vo.LibraryInfoVO;
import com.bookgo.vo.RouteVO;
import com.bookgo.vo.RouteSummaryVO;
import com.bookgo.vo.GuideVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bookgo/map")
public class MapController {

    private static final Logger logger = LoggerFactory.getLogger(MapController.class);

    @Autowired
    private LibraryService libraryService;

    // 네이버 디벨로퍼 API 키 (장소 검색에 사용)
    private static final String NAVER_DEVELOPER_CLIENT_ID = "eT0pidEez2sszspFzxfj";
    private static final String NAVER_DEVELOPER_CLIENT_SECRET = "TJ6a_N5sVV";

    // 네이버 클라우드 플랫폼 API 키 (지도 경로 검색에 사용)
    private static final String NAVER_CLOUD_CLIENT_ID = "5t0okt77tk";
    private static final String NAVER_CLOUD_CLIENT_SECRET = "fzDDZb3c1BHGbOlJB5XRKO98e58yaPELiRgzOXoh";

    // 맵 페이지를 렌더링하는 메서드
    @GetMapping
    public String showMap(Model model) {
        logger.info("지도 페이지 진입");
        List<LibraryInfoVO> libraries = libraryService.getAllLibraries();
        model.addAttribute("libraries", libraries);
        return "bookgo/map"; // bookgo/map.html을 렌더링
    }

    // 도서관 데이터를 가져오는 API
    @GetMapping("/libraries")
    @ResponseBody
    public List<LibraryInfoVO> getLibraries() {
        logger.info("도서관 데이터 요청");
        return libraryService.getAllLibraries();
    }

    // 주소 검색을 처리하는 API
    @GetMapping("/search-address")
    @ResponseBody
    public Map<String, Double> searchAddress(@RequestParam String query) {
        logger.info("주소 검색 요청: {}", query);
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + query;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", NAVER_CLOUD_CLIENT_ID);
        headers.set("X-NCP-APIGW-API-KEY", NAVER_CLOUD_CLIENT_SECRET);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, org.springframework.http.HttpMethod.GET, entity, Map.class);

        Map<String, Double> coordinates = new HashMap<>();
        if (response.getBody() != null && response.getBody().get("addresses") != null) {
            List<Map<String, Object>> addresses = (List<Map<String, Object>>) response.getBody().get("addresses");
            if (!addresses.isEmpty()) {
                Map<String, Object> firstAddress = addresses.get(0);
                coordinates.put("lat", Double.parseDouble((String) firstAddress.get("y")));
                coordinates.put("lng", Double.parseDouble((String) firstAddress.get("x")));
            }
        }

        return coordinates;
    }

    // 장소 검색을 처리하는 API (네이버 디벨로퍼 API 키 사용)
    @GetMapping("/search-place")
    @ResponseBody
    public List<Map<String, String>> searchPlace(@RequestParam String query) {
        logger.info("장소 검색 요청: {}", query);
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://openapi.naver.com/v1/search/local.json?query=" + query + "&display=5";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", NAVER_DEVELOPER_CLIENT_ID);
        headers.set("X-Naver-Client-Secret", NAVER_DEVELOPER_CLIENT_SECRET);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, org.springframework.http.HttpMethod.GET, entity, Map.class);

        List<Map<String, String>> places = new ArrayList<>();
        if (response.getBody() != null && response.getBody().get("items") != null) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
            for (Map<String, Object> item : items) {
                Map<String, String> place = new HashMap<>();
                place.put("title", (String) item.get("title"));
                place.put("address", (String) item.get("address"));
                place.put("roadAddress", (String) item.get("roadAddress"));
                place.put("mapx", (String) item.get("mapx"));
                place.put("mapy", (String) item.get("mapy"));
                places.add(place);
            }
        }

        return places;
    }

    // 경로 안내를 처리하는 API
    @GetMapping("/get-directions")
    @ResponseBody
    public ResponseEntity<?> getDirections(@RequestParam double startLat,
                                           @RequestParam double startLng,
                                           @RequestParam double destLat,
                                           @RequestParam double destLng,
                                           @RequestParam String mode) {
        logger.info("경로 안내 요청: 시작 좌표({}, {}), 목적지 좌표({}, {}), 모드: {}", startLat, startLng, destLat, destLng, mode);

        String apiUrl = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving" +
                "?start=" + startLng + "," + startLat +
                "&goal=" + destLng + "," + destLat +
                "&option=" + mode;

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", NAVER_CLOUD_CLIENT_ID);
            headers.set("X-NCP-APIGW-API-KEY", NAVER_CLOUD_CLIENT_SECRET);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, org.springframework.http.HttpMethod.GET, entity, Map.class);

            if (response.getBody() != null && (int) response.getBody().get("code") == 0) {
                Map<String, Object> routeMap = (Map<String, Object>) response.getBody().get("route");

                if (routeMap == null || routeMap.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid route data found.");
                }

                List<Object> routeDataList = (List<Object>) routeMap.get("traoptimal");

                if (routeDataList == null || routeDataList.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid route data found for mode: " + mode);
                }

                Map<String, Object> routeData = (Map<String, Object>) routeDataList.get(0);
                RouteSummaryVO summary = new RouteSummaryVO();
                Map<String, Object> summaryMap = (Map<String, Object>) routeData.get("summary");

                summary.setStartLat(startLat);
                summary.setStartLng(startLng);
                summary.setGoalLat(destLat);
                summary.setGoalLng(destLng);
                summary.setDistance(((Number) summaryMap.get("distance")).doubleValue());
                summary.setDuration(((Number) summaryMap.get("duration")).longValue());
                summary.setTollFare((int) summaryMap.get("tollFare"));
                summary.setTaxiFare((int) summaryMap.get("taxiFare"));
                summary.setFuelPrice((int) summaryMap.get("fuelPrice"));

                List<GuideVO> guides = new ArrayList<>();
                List<Map<String, Object>> guideList = (List<Map<String, Object>>) routeData.get("guide");
                if (guideList != null) {
                    for (Map<String, Object> guide : guideList) {
                        GuideVO guideVO = new GuideVO();
                        guideVO.setInstruction((String) guide.get("instructions"));
                        guideVO.setDistance((int) guide.get("distance"));
                        guides.add(guideVO);
                    }
                }

                List<double[]> path = new ArrayList<>();
                List<List<Double>> pathData = (List<List<Double>>) routeData.get("path");
                if (pathData != null) {
                    for (List<Double> coords : pathData) {
                        path.add(new double[]{coords.get(1), coords.get(0)});
                    }
                }

                RouteVO routeVO = new RouteVO();
                routeVO.setSummary(summary);
                routeVO.setGuides(guides);
                routeVO.setPath(path);

                return ResponseEntity.ok(routeVO);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No valid route found or invalid API response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러: " + e.getMessage());
        }
    }
}
