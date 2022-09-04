package com.java110.barrier.engine;

import com.java110.core.service.car.ICarInoutService;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarInLogEngine extends CarEngine{

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    /**
     * 保存经常记录
     *
     * @param carNum
     * @param type
     * @param machineDto
     * @param parkingAreaDtos
     * @param state
     * @param remark
     * @throws Exception
     */
    public void saveCarInLog(String carNum, String type, MachineDto machineDto, List<ParkingAreaDto> parkingAreaDtos, String state, String remark) throws Exception {

        //2.0 进场
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setCarType(type);
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setState(state);
        carInoutDto.setRemark(remark);
        carInoutServiceImpl.saveCarInout(carInoutDto);
    }

}
