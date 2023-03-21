package com.kob.backend.dto;

public class WaterAreaResult {
    private Integer imageId;
    private Double waterArea;

    public WaterAreaResult() {
    }

    public WaterAreaResult(Integer imageId, Double waterArea) {
        this.imageId = imageId;
        this.waterArea = waterArea;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Double getWaterArea() {
        return waterArea;
    }

    public void setWaterArea(Double waterArea) {
        this.waterArea = waterArea;
    }
}