package com.java110.barrier.engine;

import com.java110.core.service.car.ICarInoutService;
import com.java110.core.service.parkingCouponCar.IParkingCouponCarService;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingCouponCar.ParkingCouponCarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarOutLogEngine extends CarEngine{


    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private IParkingCouponCarService parkingCouponCarServiceImpl;

    public void saveCarOutLog(String carNum,
                              MachineDto machineDto,
                              List<ParkingAreaDto> parkingAreaDtos,
                              String state,
                              String remark) throws Exception {
        saveCarOutLog(carNum,machineDto,parkingAreaDtos,state,remark,null);
    }

    public void saveCarOutLog(String carNum,
                              MachineDto machineDto,
                              List<ParkingAreaDto> parkingAreaDtos,
                              String state,
                              String remark,
                              List<ParkingCouponCarDto> parkingCouponCarDtos) throws Exception {

        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);


        carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setCarType("1");
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(getDefaultPaId(parkingAreaDtos));
        carInoutDto.setState(state);
        carInoutDto.setRemark(remark);
        carInoutDto.setPhotoJpg(machineDto.getPhotoJpg());
        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            carInoutDto.setPayCharge(carInoutDtos.get(0).getPayCharge());
            carInoutDto.setRealCharge(carInoutDtos.get(0).getRealCharge());
            carInoutDto.setPayType(carInoutDtos.get(0).getPayType());
        } else {
            carInoutDto.setPayCharge("0");
            carInoutDto.setRealCharge("0");
            carInoutDto.setPayType("1");
        }
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setParkingCouponCarDtos(parkingCouponCarDtos);
        carInoutServiceImpl.saveCarInout(carInoutDto);

        if (CarInoutDto.STATE_IN_FAIL.equals(state)) {
            return;
        }

        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            CarInoutDto tmpCarInoutDto = new CarInoutDto();
            tmpCarInoutDto.setInoutId(carInoutDtos.get(0).getInoutId());
            tmpCarInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutServiceImpl.updateCarInout(tmpCarInoutDto);
        }

        if(parkingCouponCarDtos == null || parkingCouponCarDtos.size() < 1){
            return ;
        }
        //停车劵核销
        ParkingCouponCarDto tmpParkingCouponCarDto = null;
        for(ParkingCouponCarDto parkingCouponCarDto : parkingCouponCarDtos) {
            tmpParkingCouponCarDto = new ParkingCouponCarDto();
            tmpParkingCouponCarDto.setPccId(parkingCouponCarDto.getPccId());
            tmpParkingCouponCarDto.setState(ParkingCouponCarDto.STATE_F);
            tmpParkingCouponCarDto.setRemark("车辆出场核销");
            parkingCouponCarServiceImpl.updateParkingCouponCar(tmpParkingCouponCarDto);
        }


//        //异步上报HC小区管理系统
//        carCallHcServiceImpl.carInout(carInoutDto);
    }
}
