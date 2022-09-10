package com.java110.barrier.adapter;

import com.java110.barrier.engine.IInCarEngine;
import com.java110.barrier.engine.IInOutCarTextEngine;
import com.java110.barrier.engine.IOutCarEngine;
import com.java110.barrier.engine.SendInfoEngine;
import com.java110.core.adapt.IComputeTempCarFee;
import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.entity.car.*;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ParkingAreaTextCacheDto;
import com.java110.entity.parkingArea.ParkingBoxDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.ParkingAreaTextFactory;
import com.java110.core.factory.TempCarFeeFactory;
import com.java110.core.service.app.IAppService;
import com.java110.core.service.car.ICarBlackWhiteService;
import com.java110.core.service.car.ICarInoutService;
import com.java110.core.service.car.ICarService;
import com.java110.core.service.community.ICommunityService;
import com.java110.core.service.fee.ITempCarFeeConfigService;
import com.java110.core.service.hc.ICarCallHcService;
import com.java110.core.service.parkingArea.IParkingAreaService;
import com.java110.core.service.parkingBox.IParkingBoxService;
import com.java110.core.util.Assert;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import com.java110.core.util.StringUtil;
import com.java110.intf.inner.IApiInnerService;
import com.java110.intf.inner.IGatewayNettySocketHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 摄像头业务处理类
 */
@Service
public class CallCarServiceImpl implements ICallCarService {
    Logger logger = LoggerFactory.getLogger(CallCarServiceImpl.class);
    @Autowired
    private ICarBlackWhiteService carBlackWhiteServiceImpl;

    @Autowired
    private ICarService carServiceImpl;

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private ICarCallHcService carCallHcServiceImpl;

    @Autowired
    private ITempCarFeeConfigService tempCarFeeConfigServiceImpl;

    @Autowired
    private IParkingAreaService parkingAreaServiceImpl;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IParkingBoxService parkingBoxServiceImpl;


    @Autowired
    private IAppService appServiceImpl;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IApiInnerService apiInnerServiceImpl;

    @Autowired
    private IOutCarEngine outCarEngine;

    @Autowired
    private IInCarEngine inCarEngine;

    @Autowired
    private SendInfoEngine sendInfoEngine;


    @Override
    public ResultParkingAreaTextDto ivsResult(String type, String carNum, MachineDto machineDto, IInOutCarTextEngine inOutCarTextEngine) throws Exception {


        String machineDirection = machineDto.getDirection();
        ResultParkingAreaTextDto resultParkingAreaTextDto = null;
        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setExtBoxId(machineDto.getLocationObjId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        if("无牌车".equals(carNum)){
        }

        BarrierGateControlDto barrierGateControlDto = new BarrierGateControlDto(BarrierGateControlDto.ACTION_INOUT, carNum, machineDto);
        sendInfoEngine.sendInfo(barrierGateControlDto, machineDto.getLocationObjId(), machineDto);
        switch (machineDirection) {
            case MachineDto.MACHINE_DIRECTION_ENTER: // 车辆进场
                if("无牌车".equals(carNum)){
                    resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_ERROR, "无牌车","请扫码入场","","","无牌车,请扫码入场", carNum);
                }else {
                    resultParkingAreaTextDto = inCarEngine.enterParkingArea(type, carNum, machineDto, parkingAreaDtos, inOutCarTextEngine);
                }
                break;
            case MachineDto.MACHINE_DIRECTION_OUT://车辆出场
                if("无牌车".equals(carNum)){
                    resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_ERROR, "无牌车","请扫码出场","","","无牌车,请扫码出场", carNum);
                }else {
                    resultParkingAreaTextDto = outCarEngine.outParkingArea(type, carNum, machineDto, parkingAreaDtos,inOutCarTextEngine);
//                if (resultParkingAreaTextDto.getCode()
//                        == ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS
//                        || resultParkingAreaTextDto.getCode()
//                        == ResultParkingAreaTextDto.CODE_FREE_CAR_OUT_SUCCESS
//                        || resultParkingAreaTextDto.getCode()
//                        == ResultParkingAreaTextDto.CODE_MONTH_CAR_OUT_SUCCESS
//                        || resultParkingAreaTextDto.getCode()
//                        == ResultParkingAreaTextDto.CODE_TEMP_CAR_OUT_SUCCESS
//                ) {
//                    carOut(carNum, machineDto);
//                }
                }
                break;
            default:
                resultParkingAreaTextDto = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_ERROR, "系统异常");
        }

        return resultParkingAreaTextDto;
    }

    /**
     * 车辆出场 记录
     *
     * @param carNum
     */
    private void carOut(String carNum, MachineDto machineDto) throws Exception {
        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutServiceImpl.updateCarInout(carInoutDto);
        }
        carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carNum);
        carInoutDto.setCarType("1");
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState(CarInoutDto.STATE_OUT);
        carInoutDto.setRemark("正常出场");
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
        carInoutServiceImpl.saveCarInout(carInoutDto);
//        //异步上报HC小区管理系统
//        carCallHcServiceImpl.carInout(carInoutDto);
    }














}
