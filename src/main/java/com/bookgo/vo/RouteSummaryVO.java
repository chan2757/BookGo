package com.bookgo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteSummaryVO {
    private double startLat;
    private double startLng;
    private double goalLat;
    private double goalLng;
    private double distance; // 총 거리 (미터)
    private long duration; // 총 시간 (초)
    private int tollFare;
    private int taxiFare;
    private int fuelPrice;

    // Getters and Setters
}
