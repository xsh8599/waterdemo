package com.kob.backend.service.imp.water;

import com.kob.backend.dto.WaterAreaResult;
import com.kob.backend.mapper.ImageMapper;
import com.kob.backend.pojo.Image;
import com.kob.backend.service.water.WaterAreaService;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.opencv.core.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class WaterAreaServiceImpl implements WaterAreaService {

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public List<WaterAreaResult> calculateWaterArea(MultipartFile[] files) throws Exception {
        List<WaterAreaResult> results = new ArrayList<>();

        for (MultipartFile file : files) {
            // 保存文件到服务器
            String fileName = file.getOriginalFilename();
            String filePath = "upload/" + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            // 加载图像
            Mat src = Imgcodecs.imread(filePath);

            // 将图像转换为灰度图像
            Mat gray = new Mat();
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

            // 进行阈值分割，得到二值化图像
            Mat binary = new Mat();
            Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY);

            // 进行形态学处理
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
            Imgproc.morphologyEx(binary, binary, Imgproc.MORPH_OPEN, kernel);

            // 计算水体面积
            double waterArea = calculateWaterArea(binary);

            // 保存结果到数据库
            Image image = new Image();
            image.setName(fileName);
            image.setPath(filePath);
            imageMapper.insert(image);

            WaterAreaResult result = new WaterAreaResult();
            result.setImageId(image.getId());
            result.setWaterArea(waterArea);
            results.add(result);
        }

        return results;
    }

    private double calculateWaterArea(Mat binary) {
        // 寻找轮廓
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // 计算水体面积
        double waterArea = 0;
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > 1000) { // 过滤面积较小的轮廓
                waterArea += area;
            }
        }

        return waterArea;
    }
}