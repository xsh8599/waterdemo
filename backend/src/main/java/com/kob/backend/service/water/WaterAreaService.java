package com.kob.backend.service.water;

import com.kob.backend.dto.WaterAreaResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WaterAreaService {
    List<WaterAreaResult> calculateWaterArea(MultipartFile[] files) throws Exception;
}
