package com.java110.core.service.fee.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.adapt.IComputeTempCarFee;
import com.java110.core.constant.ResponseConstant;
import com.java110.core.constant.SystemConstant;
import com.java110.core.dao.ITempCarFeeConfigServiceDao;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.CarMachineProcessFactory;
import com.java110.core.factory.CarProcessFactory;
import com.java110.core.factory.MappingCacheFactory;
import com.java110.core.service.car.ICarInoutService;
import com.java110.core.service.fee.ITempCarFeeConfigService;
import com.java110.core.service.hc.ICarCallHcService;
import com.java110.core.service.machine.IMachineService;
import com.java110.core.service.parkingArea.IParkingAreaService;
import com.java110.core.util.Assert;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import com.java110.core.util.StringUtil;
import com.java110.entity.PageDto;
import com.java110.entity.car.*;
import com.java110.entity.fee.TempCarPayOrderDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ParkingBoxAreaDto;
import com.java110.entity.parkingArea.ParkingBoxDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.entity.response.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TempCarFeeConfigServiceImpl
 * @Description TODO 小区管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("tempCarFeeConfigServiceImpl")
public class TempCarFeeConfigServiceImpl implements ITempCarFeeConfigService {

    @Autowired
    private ITempCarFeeConfigServiceDao carServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMachineService machineService;

    @Autowired
    IParkingAreaService parkingAreaServiceImpl;

    @Autowired
    private ITempCarFeeConfigService tempCarFeeConfigServiceImpl;

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    @Autowired
    private ICarCallHcService carCallHcServiceImpl;

    @Autowired
    IMachineService machineServiceImpl;


    /**
     * 添加小区信息
     *
     * @param tempCarFeeConfigDto 小区对象
     * @return
     */
    @Override
    public ResultDto saveTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {
        ResultDto resultDto = null;

        int count = carServiceDao.saveTempCarFeeConfig(tempCarFeeConfigDto);

        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    /**
     * 查询小区信息
     *
     * @param tempCarFeeConfigDto 小区信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {

        if (tempCarFeeConfigDto.getPage() != PageDto.DEFAULT_PAGE) {
            tempCarFeeConfigDto.setPage((tempCarFeeConfigDto.getPage() - 1) * tempCarFeeConfigDto.getRow());
        }
        long count = carServiceDao.getTempCarFeeConfigCount(tempCarFeeConfigDto);
        int totalPage = (int) Math.ceil((double) count / (double) tempCarFeeConfigDto.getRow());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtoList = null;
        if (count > 0) {
            tempCarFeeConfigDtoList = carServiceDao.getTempCarFeeConfigs(tempCarFeeConfigDto);
            //刷新人脸地
        } else {
            tempCarFeeConfigDtoList = new ArrayList<>();
        }

        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, tempCarFeeConfigDtoList);
        return resultDto;
    }

    @Override
    public List<TempCarFeeConfigDto> queryTempCarFeeConfigs(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {
        if (tempCarFeeConfigDto.getPage() != PageDto.DEFAULT_PAGE) {
            tempCarFeeConfigDto.setPage((tempCarFeeConfigDto.getPage() - 1) * tempCarFeeConfigDto.getRow());
        }
        List<TempCarFeeConfigDto> tempCarFeeConfigDtoList = null;

        tempCarFeeConfigDtoList = carServiceDao.getTempCarFeeConfigs(tempCarFeeConfigDto);
        //刷新人脸地
        return tempCarFeeConfigDtoList;

    }

    @Override
    public ResultDto updateTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {

        int count = carServiceDao.updateTempCarFeeConfig(tempCarFeeConfigDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


    @Override
    public ResultDto deleteTempCarFeeConfig(TempCarFeeConfigDto tempCarFeeConfigDto) throws Exception {
        ResultDto resultDto = null;

        tempCarFeeConfigDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = carServiceDao.updateTempCarFeeConfig(tempCarFeeConfigDto);
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public int saveTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigDto) throws Exception {
        int count = carServiceDao.saveTempCarFeeConfigAttr(tempCarFeeConfigDto);
        return count;
    }

    @Override
    public List<TempCarFeeConfigAttrDto> queryTempCarFeeConfigAttrs(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto) throws Exception {
        return carServiceDao.getTempCarFeeConfigAttrs(tempCarFeeConfigAttrDto);
    }

    @Override
    public int deleteTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto) throws Exception {
        return carServiceDao.deleteTempCarFeeConfigAttr(tempCarFeeConfigAttrDto);
    }

    @Override
    public ResultDto getTempCarFeeOrder(CarDto carDto) throws Exception {

        // 查询停车场对应设备 是否为 第三方平台
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(carDto.getExtPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        TempCarPayOrderDto tempCarPayOrderDto = null;
        if (machineDtos != null && machineDtos.size() > 0) {
            tempCarPayOrderDto = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).getNeedPayOrder(machineDtos.get(0), carDto);
            if (tempCarPayOrderDto == null) {
                return new ResultDto(ResultDto.ERROR, "查询失败");
            }
            JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(tempCarPayOrderDto));
            data.put("inTime", DateUtil.getFormatTimeString(tempCarPayOrderDto.getInTime(), DateUtil.DATE_FORMATE_STRING_A));
            data.put("queryTime", DateUtil.getFormatTimeString(tempCarPayOrderDto.getQueryTime(), DateUtil.DATE_FORMATE_STRING_A));
            return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG, data);
        }
        //这里预留 调用自己的 算费系统算费 暂不实现
        machineDto = new MachineDto();
        machineDto.setExtPaId(carDto.getExtPaId());
        //machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        machineDtos = machineService.queryBoxMachines(machineDto);

        if (machineDtos == null && machineDtos.size() < 1) {
            return new ResultDto(ResultDto.ERROR, "未找到设备信息");
        }

        tempCarPayOrderDto = getCustomeTempCarFeeOrder(carDto, machineDtos.get(0));

        if (tempCarPayOrderDto == null) {
            return new ResultDto(ResultDto.ERROR, "查询失败");
        }
        JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(tempCarPayOrderDto));
        data.put("inTime", DateUtil.getFormatTimeString(tempCarPayOrderDto.getInTime(), DateUtil.DATE_FORMATE_STRING_A));
        data.put("queryTime", DateUtil.getFormatTimeString(tempCarPayOrderDto.getQueryTime(), DateUtil.DATE_FORMATE_STRING_A));
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG, data);

    }

    /**
     * 调用自己的算费逻辑
     *
     * @param carDto
     * @param machineDto
     * @return
     */
    private TempCarPayOrderDto getCustomeTempCarFeeOrder(CarDto carDto, MachineDto machineDto) throws Exception {
        ParkingAreaDto parkingAreaDto = new ParkingAreaDto();
        parkingAreaDto.setExtPaId(carDto.getExtPaId());
        parkingAreaDto.setCommunityId(machineDto.getCommunityId());
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreas(parkingAreaDto);

        Assert.listOnlyOne(parkingAreaDtos, "未找到停车场信息");

        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(carDto.getCarNum());
        carInoutDto.setPaId(parkingAreaDtos.get(0).getPaId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_REPAY});
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos == null || carInoutDtos.size() == 0) {
            throw new IllegalArgumentException("未查询到进场记录");
        }


        TempCarFeeConfigDto tempCarFeeConfigDto = new TempCarFeeConfigDto();
        tempCarFeeConfigDto.setPaId(parkingAreaDtos.get(0).getPaId());
        tempCarFeeConfigDto.setCommunityId(machineDto.getCommunityId());
        List<TempCarFeeConfigDto> tempCarFeeConfigDtos = tempCarFeeConfigServiceImpl.queryTempCarFeeConfigs(tempCarFeeConfigDto);

        if (tempCarFeeConfigDtos == null || tempCarFeeConfigDtos.size() < 1) {
            throw new IllegalArgumentException("未查询到临时车费用规则");
        }

        IComputeTempCarFee computeTempCarFee = ApplicationContextFactory.getBean(tempCarFeeConfigDtos.get(0).getRuleId(), IComputeTempCarFee.class);
        TempCarFeeResult result = computeTempCarFee.computeTempCarFee(carInoutDtos.get(0), tempCarFeeConfigDtos.get(0));


        TempCarPayOrderDto tempCarPayOrderDto = new TempCarPayOrderDto();
        tempCarPayOrderDto.setExtPaId(carDto.getExtPaId());
        tempCarPayOrderDto.setInTime(DateUtil.getDateFromString(carInoutDtos.get(0).getOpenTime(), DateUtil.DATE_FORMATE_STRING_A));
        tempCarPayOrderDto.setQueryTime(DateUtil.getCurrentDate());
        tempCarPayOrderDto.setCarNum(carDto.getCarNum());
        tempCarPayOrderDto.setPayCharge(result.getPayCharge());
        tempCarPayOrderDto.setAmount(result.getPayCharge());
        tempCarPayOrderDto.setStopTimeTotal(result.getHours() * 60 + result.getMin());
        tempCarPayOrderDto.setPaId(parkingAreaDtos.get(0).getPaId());
        tempCarPayOrderDto.setOrderId(carInoutDtos.get(0).getInoutId());
        return tempCarPayOrderDto;
    }

    @Override
    public ResultDto notifyTempCarFeeOrder(TempCarPayOrderDto tempCarPayOrderDto) throws Exception {
        ResultDto resultDto = null;
        MachineDto machineDto = new MachineDto();
        machineDto.setLocationObjId(tempCarPayOrderDto.getExtPaId());
        machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos != null && machineDtos.size() > 0) {
            resultDto = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).notifyTempCarFeeOrder(machineDtos.get(0), tempCarPayOrderDto);
            return resultDto;
        }
        //这里预留 调用自己的 算费系统算费 暂不实现
        machineDto = new MachineDto();
        machineDto.setExtPaId(tempCarPayOrderDto.getExtPaId());
        //machineDto.setLocationType(MachineDto.LOCATION_TYPE_PARKING_AREA);
        machineDtos = machineService.queryBoxMachines(machineDto);

        if (machineDtos == null && machineDtos.size() < 1) {
            return new ResultDto(ResultDto.ERROR, "未找到设备信息");
        }

        resultDto = notifyCustomTempCarFeeOrder(machineDtos.get(0), tempCarPayOrderDto);
        return resultDto;
    }

    /**
     * 优化
     *
     * @param machineDto
     * @param tempCarPayOrderDto
     * @return
     */
    private ResultDto notifyCustomTempCarFeeOrder(MachineDto machineDto, TempCarPayOrderDto tempCarPayOrderDto) throws Exception {

        /**
         *  postParameters.put("plateNum", tempCarPayOrderDto.getCarNum());
         *         postParameters.put("orderId", tempCarPayOrderDto.getOrderId());
         *         postParameters.put("amount", tempCarPayOrderDto.getAmount());
         *         postParameters.put("payTime", tempCarPayOrderDto.getPayTime());
         *         postParameters.put("payType", tempCarPayOrderDto.getPayType());
         */
        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setInoutId(tempCarPayOrderDto.getOrderId());
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_REPAY});
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos == null || carInoutDtos.size() == 0) {
            return new ResultDto(ResultDto.ERROR, "支付订单不存在");
        }
        carInoutDto = new CarInoutDto();
        carInoutDto.setState(CarInoutDto.STATE_PAY);
        carInoutDto.setPayType(tempCarPayOrderDto.getPayType());
        carInoutDto.setPayTime(tempCarPayOrderDto.getPayTime());
        carInoutDto.setRealCharge(tempCarPayOrderDto.getAmount() + "");
        if (tempCarPayOrderDto.getPayCharge() != 0) {
            carInoutDto.setPayCharge(tempCarPayOrderDto.getPayCharge() + "");
        }
        carInoutDto.setInoutId(tempCarPayOrderDto.getOrderId());
        carInoutServiceImpl.updateCarInout(carInoutDto);

        //通知HC 支付完成
        carInoutDto.setCarNum(carInoutDtos.get(0).getCarNum());
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setCommunityId(carInoutDtos.get(0).getCommunityId());
        carCallHcServiceImpl.notifyTempCarFeeOrder(carInoutDto);

        //场内二维码支付
        if (StringUtil.isEmpty(tempCarPayOrderDto.getExtMachineId())) {
            return new ResultDto(ResultDto.SUCCESS, "支付成功");
        }
        machineDto = new MachineDto();
        machineDto.setExtMachineId(tempCarPayOrderDto.getExtMachineId());
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        Assert.listOnlyOne(machineDtos, "设备不存在");
        machineDto = machineDtos.get(0);

        String manualTriggerOrOpenDoor = MappingCacheFactory.getValue("manualTriggerOrOpenDoor");
        if ("MANUAL_TRIGGER".equals(manualTriggerOrOpenDoor)) {
            //门口二维码支付
            CarMachineProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).manualTrigger(machineDtos.get(0));
        } else {
            JSONObject param = new JSONObject();
            param.put("carNum", carInoutDtos.get(0).getCarNum());
            param.put("payCharge", tempCarPayOrderDto.getPayCharge());
            param.put("amount", tempCarPayOrderDto.getAmount());
            param.put("payType", tempCarPayOrderDto.getPayType());
            uploadcarout(machineDto, param);
            ResultParkingAreaTextDto parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, carInoutDtos.get(0).getCarNum(),
                    "一路平安", "", "", carInoutDtos.get(0).getCarNum() + ",一路平安", carInoutDtos.get(0).getCarNum());
            parkingAreaTextDto.setCarNum(carInoutDtos.get(0).getCarNum());
            machineServiceImpl.openDoor(machineDto, parkingAreaTextDto,new JSONObject());
        }

        return new ResultDto(ResultDto.SUCCESS, "支付成功");
    }


    /**
     * 出场上报
     *
     * @param machineDto
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarout(MachineDto machineDto, JSONObject acceptJson) throws Exception {

        String paId = "";

        //查询 岗亭
        ParkingBoxDto parkingBoxDto = new ParkingBoxDto();
        parkingBoxDto.setExtBoxId(machineDto.getLocationObjId());
        parkingBoxDto.setCommunityId(machineDto.getCommunityId());
        parkingBoxDto.setDefaultArea(ParkingBoxAreaDto.DEFAULT_AREA_TRUE);
        List<ParkingAreaDto> parkingAreaDtos = parkingAreaServiceImpl.queryParkingAreasByBox(parkingBoxDto);
        //Assert.listOnlyOne(parkingAreaDtos, "停车场不存在");
        if (parkingAreaDtos == null || parkingAreaDtos.size() < 1) {
            throw new IllegalArgumentException("停车场不存在");
        }

        paId = parkingAreaDtos.get(0).getPaId();

        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setPaId(paId);
        carInoutDto.setStates(new String[]{CarInoutDto.STATE_IN, CarInoutDto.STATE_PAY});
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutServiceImpl.updateCarInout(carInoutDto);
        }
        carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setCarType("1");
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(paId);
        carInoutDto.setState("3");
        carInoutDto.setRemark("岗亭口 扫码出场");
        if (acceptJson.containsKey("payCharge")) {
            carInoutDto.setPayCharge(acceptJson.getString("payCharge"));
        } else {
            carInoutDto.setPayCharge(acceptJson.getString("amount"));
        }
        carInoutDto.setRealCharge(acceptJson.getString("amount"));
        carInoutDto.setPayType(acceptJson.getString("payType"));
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutServiceImpl.saveCarInout(carInoutDto);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }

}
