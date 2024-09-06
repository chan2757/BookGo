package com.bookgo.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteVO {
    private RouteSummaryVO summary; // 경로 요약 정보
    private List<GuideVO> guides; // 경로 안내 정보 목록
    private List<double[]> path; // 경로 좌표 리스트

    // Getters and Setters
}
