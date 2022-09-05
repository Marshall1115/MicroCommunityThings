package com.java110.barrier.engine;

import com.java110.core.util.StringUtil;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ParkingAreaTextCacheDto;

import java.util.List;

public class CarEngine {

    protected String getDefaultPaId(List<ParkingAreaDto> parkingAreaDtos) {
        String defaultPaId = "";
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            if ("T".equals(parkingAreaDto.getDefaultArea())) {
                defaultPaId = parkingAreaDto.getPaId();
            }

        }
        if (StringUtil.isEmpty(defaultPaId)) {
            defaultPaId = parkingAreaDtos.get(0).getPaId();
        }

        return defaultPaId;
    }


    /**
     * 替换配置中的配置
     *
     * @param parkingAreaTextCacheDto
     * @param carNum
     * @param hours
     * @param min
     * @param payCharge
     */
    protected void replaceParkingAreaTextCache(ParkingAreaTextCacheDto parkingAreaTextCacheDto, String carNum, String hours, String min, String payCharge, String day) {

        if (parkingAreaTextCacheDto == null) {
            return;
        }
        String replaceAfter = "";
        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText1())) {
            replaceAfter = parkingAreaTextCacheDto.getText1()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge);
            parkingAreaTextCacheDto.setText1(replaceAfter);
        }

        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText2())) {
            replaceAfter = parkingAreaTextCacheDto.getText2()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setText2(replaceAfter);
        }

        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText3())) {
            replaceAfter = parkingAreaTextCacheDto.getText3()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setText3(replaceAfter);
        }
        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getText4())) {
            replaceAfter = parkingAreaTextCacheDto.getText4()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setText4(replaceAfter);
        }
        if (!StringUtil.isEmpty(parkingAreaTextCacheDto.getVoice())) {
            replaceAfter = parkingAreaTextCacheDto.getVoice()
                    .replaceAll("carNum", carNum)
                    .replaceAll("hours", hours)
                    .replaceAll("min", min)
                    .replaceAll("day", day)
                    .replaceAll("payCharge", payCharge)
            ;
            parkingAreaTextCacheDto.setVoice(replaceAfter);
        }
    }
}
