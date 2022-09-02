package com.java110.api.controller.machine;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.BaseController;
import com.java110.api.ws.BarrierGateControlWebSocketServer;
import com.java110.core.constant.MachineConstant;
import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.core.service.machine.*;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.machine.*;
import com.java110.entity.openDoor.OpenDoorDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.entity.response.ResultDto;
import com.java110.core.service.car.ICarInoutService;
import com.java110.core.service.openDoor.IOpenDoorService;
import com.java110.core.util.Assert;
import com.java110.core.util.BeanConvertUtil;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName MachineController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/machine")
public class MachineController extends BaseController {

    @Autowired
    private IMachineService machineServiceImpl;

    @Autowired
    private IOperateLogService operateLogServiceImpl;

    @Autowired
    private ITransactionLogService transactionLogServiceImpl;

    @Autowired
    private IMachineFaceService machineFaceService;

    @Autowired
    private IOpenDoorService openDoorService;

    @Autowired
    private IMachineCmdService machineCmdService;

    @Autowired
    private ICarInoutService carInoutServiceImpl;

    /**
     * 添加设备接口类
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveMachine", method = RequestMethod.POST)
    public ResponseEntity<String> saveMachine(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "machineMac", "请求报文中未包含硬件mac地址");

        Assert.hasKeyAndValue(paramObj, "machineCode", "请求报文中未包含硬件编码");

        Assert.hasKeyAndValue(paramObj, "machineVersion", "请求报文中未包含硬件版本号");

        Assert.hasKeyAndValue(paramObj, "machineIp", "请求报文中未包含硬件IP");

        Assert.hasKeyAndValue(paramObj, "machineName", "请求报文中未包含硬件名称，如果没有可以填写mac或者IP");

        if (!paramObj.containsKey("machineTypeCd")) {
            paramObj.put("machineTypeCd", MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        }

        paramObj.put("machineId", UUID.randomUUID().toString());


        ResultDto resultDto = machineServiceImpl.saveMachine(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }


    //添加道闸
    @RequestMapping(path = "/saveBarrierGate", method = RequestMethod.POST)
    public ResponseEntity<String> saveBarrierGate(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "machineMac", "请求报文中未包含硬件mac地址");

        Assert.hasKeyAndValue(paramObj, "machineCode", "请求报文中未包含硬件编码");

        Assert.hasKeyAndValue(paramObj, "machineVersion", "请求报文中未包含硬件版本号");

        Assert.hasKeyAndValue(paramObj, "machineIp", "请求报文中未包含硬件IP");

        Assert.hasKeyAndValue(paramObj, "machineName", "请求报文中未包含硬件名称，如果没有可以填写mac或者IP");

        if (!paramObj.containsKey("machineTypeCd")) {
            paramObj.put("machineTypeCd", MachineConstant.MACHINE_TYPE_BARRIER_GATE);
        }

        paramObj.put("machineId", UUID.randomUUID().toString());


        ResultDto resultDto = machineServiceImpl.saveMachine(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachines", method = RequestMethod.GET)
    public ResponseEntity<String> getMachines(@RequestParam int page,
                                              @RequestParam int row,
                                              @RequestParam String machineTypeCd,
                                              @RequestParam String communityId,
                                              @RequestParam(value = "direction", required = false) String direction) throws Exception {

        Assert.hasText(machineTypeCd, "请求报文中未包含设备类型");
        MachineDto machineDto = new MachineDto();
        machineDto.setPage(page);
        machineDto.setRow(row);
        machineDto.setMachineTypeCd(machineTypeCd);
        machineDto.setCommunityId(communityId);
        machineDto.setDirection(direction);

        ResultDto resultDto = machineServiceImpl.getMachine(machineDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 根据类型查询设备
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineCodes", method = RequestMethod.GET)

    public ResponseEntity<String> getMachineCodes(@RequestParam String machineTypeCd) throws Exception {

        Assert.hasText(machineTypeCd, "请求报文中未包含设备类型");
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineTypeCd(machineTypeCd);
        ResultDto resultDto = machineServiceImpl.getMachine(machineDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除设备 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteMachine", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMachine(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        ResultDto resultDto = machineServiceImpl.deleteMachine(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 重启设备 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/startMachine", method = RequestMethod.POST)
    public ResponseEntity<String> startMachine(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        ResultDto resultDto = machineServiceImpl.restartMachine(BeanConvertUtil.covertBean(paramObj, MachineDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 设备开启 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/openDoor", method = RequestMethod.POST)
    public ResponseEntity<String> openDoor(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");
        ResultParkingAreaTextDto parkingAreaTextDto
                = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS,
                "欢迎光临", "", "", "", "欢迎光临", "");

        ResultDto resultDto = machineServiceImpl.openDoor(BeanConvertUtil.covertBean(paramObj, MachineDto.class), parkingAreaTextDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineLogs", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineLogs(@RequestParam int page,
                                                 @RequestParam int row,
                                                 @RequestParam String machineTypeCd,
                                                 @RequestParam String communityId,
                                                 @RequestParam(name = "logId", required = false) String logId,
                                                 @RequestParam(name = "machineCode", required = false) String machineCode,
                                                 @RequestParam(name = "machineName", required = false) String machineName) throws Exception {

        Assert.hasText(machineTypeCd, "请求报文中未包含设备类型");
        OperateLogDto operateLogDto = new OperateLogDto();
        operateLogDto.setPage(page);
        operateLogDto.setRow(row);
        operateLogDto.setMachineTypeCd(machineTypeCd);
        operateLogDto.setLogId(logId);
        operateLogDto.setMachineCode(machineCode);
        operateLogDto.setMachineName(machineName);
        operateLogDto.setCommunityId(communityId);

        ResultDto resultDto = operateLogServiceImpl.getOperateLogs(operateLogDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineFaces", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineFaces(@RequestParam int page,
                                                  @RequestParam int row,
                                                  @RequestParam String machineTypeCd,
                                                  @RequestParam(name = "name", required = false) String name,
                                                  @RequestParam(name = "machineCode", required = false) String machineCode,
                                                  @RequestParam(name = "machineId", required = false) String machineId,
                                                  @RequestParam(name = "machineName", required = false) String machineName) throws Exception {

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setPage(page);
        machineFaceDto.setRow(row);
        machineFaceDto.setMachineId(machineId);
        machineFaceDto.setName(name);
        machineFaceDto.setMachineCode(machineCode);
        machineFaceDto.setMachineName(machineName);
        machineFaceDto.setMachineTypeCd(machineTypeCd);

        ResultDto resultDto = machineFaceService.getMachineFace(machineFaceDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备接口类
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineOpenDoors", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineOpenDoors(@RequestParam int page,
                                                      @RequestParam int row,
                                                      @RequestParam(name = "name", required = false) String logId,
                                                      @RequestParam(name = "machineCode", required = false) String machineCode,
                                                      @RequestParam(name = "machineId", required = false) String machineId,
                                                      @RequestParam(name = "machineName", required = false) String machineName,
                                                      @RequestParam(name = "communityId") String communityId,
                                                      @RequestParam(name = "userName", required = false) String userName) throws Exception {

        OpenDoorDto openDoorDto = new OpenDoorDto();
        openDoorDto.setPage(page);
        openDoorDto.setRow(row);
        openDoorDto.setMachineId(machineId);
        openDoorDto.setMachineCode(machineCode);
        openDoorDto.setMachineName(machineName);
        openDoorDto.setUserName(userName);
        openDoorDto.setCommunityId(communityId);

        ResultDto resultDto = openDoorService.getOpenDoor(openDoorDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 交互日志
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getTranLogs", method = RequestMethod.GET)
    public ResponseEntity<String> getTranLogs(@RequestParam int page,
                                              @RequestParam int row,
                                              @RequestParam(name = "tranId", required = false) String tranId,
                                              @RequestParam(name = "machineCode", required = false) String machineCode,
                                              @RequestParam(name = "machineName", required = false) String machineName) throws Exception {

        TransactionLogDto transactionLogDto = new TransactionLogDto();
        transactionLogDto.setPage(page);
        transactionLogDto.setRow(row);
        transactionLogDto.setTranId(tranId);
        transactionLogDto.setMachineCode(machineCode);
        transactionLogDto.setMachineName(machineName);

        ResultDto resultDto = transactionLogServiceImpl.getTransactionLogs(transactionLogDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加设备指令
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveMachineCmd", method = RequestMethod.POST)
    public ResponseEntity<String> saveMachineCmd(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "machineTypeCd", "请求报文中未包含设备类型");

        Assert.hasKeyAndValue(paramObj, "machineCode", "请求报文中未包含硬件编码");

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        Assert.hasKeyAndValue(paramObj, "cmdCode", "请求报文中未包含命令编码");

        Assert.hasKeyAndValue(paramObj, "cmdName", "请求报文中未包含命令编码名称");

        if (!paramObj.containsKey("machineTypeCd")) {
            paramObj.put("machineTypeCd", MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        }

        paramObj.put("cmdId", UUID.randomUUID().toString());
        paramObj.put("communityId", "99999");
        MachineCmdDto machineCmdDto = BeanConvertUtil.covertBean(paramObj, MachineCmdDto.class);
        machineCmdDto.setObjType(MachineConstant.MACHINE_CMD_OBJ_TYPE_SYSTEM);
        machineCmdDto.setObjTypeValue("-1");

        ResultDto resultDto = machineCmdService.saveMachineCmd(machineCmdDto);
        return super.createResponseEntity(resultDto);
    }

    @RequestMapping(path = "/saveBarrierGateCmd", method = RequestMethod.POST)
    public ResponseEntity<String> saveBarrierGateCmd(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "machineTypeCd", "请求报文中未包含设备类型");

        Assert.hasKeyAndValue(paramObj, "machineCode", "请求报文中未包含硬件编码");

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含硬件ID");

        Assert.hasKeyAndValue(paramObj, "cmdCode", "请求报文中未包含命令编码");

        Assert.hasKeyAndValue(paramObj, "cmdName", "请求报文中未包含命令编码名称");

        if (!paramObj.containsKey("machineTypeCd")) {
            paramObj.put("machineTypeCd", MachineConstant.MACHINE_TYPE_BARRIER_GATE);
        }

        paramObj.put("cmdId", UUID.randomUUID().toString());
        paramObj.put("communityId", "99999");
        MachineCmdDto machineCmdDto = BeanConvertUtil.covertBean(paramObj, MachineCmdDto.class);
        machineCmdDto.setObjType(MachineConstant.MACHINE_CMD_OBJ_TYPE_SYSTEM);
        machineCmdDto.setObjTypeValue("-1");

        ResultDto resultDto = machineCmdService.saveMachineCmd(machineCmdDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 查询指令
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getMachineCmds", method = RequestMethod.GET)
    public ResponseEntity<String> getMachineCmds(@RequestParam int page,
                                                 @RequestParam int row,
                                                 @RequestParam(name = "machineCode", required = false) String machineCode,
                                                 @RequestParam(name = "cmdName", required = false) String cmdName) throws Exception {

        MachineCmdDto machineCmdDto = new MachineCmdDto();
        machineCmdDto.setPage(page);
        machineCmdDto.setRow(row);
        machineCmdDto.setMachineCode(machineCode);
        machineCmdDto.setCmdName(cmdName);
        ResultDto resultDto = machineCmdService.getMachineCmd(machineCmdDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 删除指令
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteMachineCmd", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMachineCmd(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "cmdId", "请求报文中未包含指令ID");

        ResultDto resultDto = machineCmdService.deleteMachineCmd(BeanConvertUtil.covertBean(paramObj, MachineCmdDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 手工入场
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/customCarInOut", method = RequestMethod.POST)
    public ResponseEntity<String> customCarInOut(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "machineId", "请求报文中未包含设备ID");
        Assert.hasKeyAndValue(paramObj, "carNum", "请求报文中未包含车辆编号");
        Assert.hasKeyAndValue(paramObj, "type", "请求报文中未包含类型");

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineId(paramObj.getString("machineId"));
        List<MachineDto> machineDtos = machineServiceImpl.queryMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "设备不存在");
        machineDto = machineDtos.get(0);
        ResultDto resultDto = null;
        ResultParkingAreaTextDto parkingAreaTextDto = null;
        if ("1101".equals(paramObj.getString("type"))) {
            uploadcarin(machineDto, paramObj);
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_IN_SUCCESS, paramObj.getString("carNum"),
                    "欢迎光临", "", "", paramObj.getString("carNum") + ",欢迎光临", paramObj.getString("carNum"));
        } else {
            uploadcarout(machineDto, paramObj);
            parkingAreaTextDto
                    = new ResultParkingAreaTextDto(ResultParkingAreaTextDto.CODE_CAR_OUT_SUCCESS, paramObj.getString("carNum"),
                    "一路平安", "", "", paramObj.getString("carNum") + ",一路平安", paramObj.getString("carNum"));
        }
        resultDto = machineServiceImpl.openDoor(machineDto, parkingAreaTextDto);

        return super.createResponseEntity(resultDto);
    }


    /**
     * 出场上报
     *
     * @param machineDto
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarout(MachineDto machineDto, JSONObject acceptJson) throws Exception {
        //查询是否有入场数据
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState(CarInoutDto.STATE_IN);
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        List<CarInoutDto> carInoutDtos = carInoutServiceImpl.queryCarInout(carInoutDto);

        if (carInoutDtos != null && carInoutDtos.size() > 0) {
            carInoutDto.setState(CarInoutDto.STATE_OUT);
            carInoutDto.setPayType(CarInoutDto.PAY_TYPE_CASH);
            carInoutDto.setPayCharge(acceptJson.getString("amount"));
            carInoutDto.setRealCharge(acceptJson.getString("amount"));
            carInoutDto.setPayTime(DateUtil.getCurrentDate());
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
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState("3");
        carInoutDto.setRemark("手工出场");
        if (acceptJson.containsKey("payCharge")) {
            carInoutDto.setPayCharge(acceptJson.getString("payCharge"));
        } else {
            carInoutDto.setPayCharge(acceptJson.getString("amount"));
        }
        carInoutDto.setRealCharge(acceptJson.getString("amount"));
        carInoutDto.setPayType("1");
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutServiceImpl.saveCarInout(carInoutDto);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工出场", "开门成功");
        BarrierGateControlWebSocketServer.sendInfo(barrierGateControlDto.toString(), machineDto.getLocationObjId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }

    /**
     * 车辆进场记录
     *
     * @param acceptJson
     * @return
     */
    private JSONObject uploadcarin(MachineDto machineDto, JSONObject acceptJson) throws Exception {
        //2.0 手工进场
        CarInoutDto carInoutDto = new CarInoutDto();
        carInoutDto.setCarNum(acceptJson.getString("carNum"));
        carInoutDto.setCarType("1");
        carInoutDto.setCommunityId(machineDto.getCommunityId());
        carInoutDto.setGateName(machineDto.getMachineName());
        carInoutDto.setInoutId(SeqUtil.getId());
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_OUT);
        carInoutDto.setMachineCode(machineDto.getMachineCode());
        carInoutDto.setOpenTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        carInoutDto.setPaId(machineDto.getLocationObjId());
        carInoutDto.setState("1");
        carInoutDto.setInoutType(CarInoutDto.INOUT_TYPE_IN);
        carInoutDto.setRemark("手工进场");
        carInoutServiceImpl.saveCarInout(carInoutDto);

        BarrierGateControlDto barrierGateControlDto
                = new BarrierGateControlDto(BarrierGateControlDto.ACTION_FEE_INFO, acceptJson.getString("carNum"), machineDto, 0, null, acceptJson.getString("carNum") + "手工进场", "开门成功");
        BarrierGateControlWebSocketServer.sendInfo(barrierGateControlDto.toString(), machineDto.getLocationObjId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "成功");
        return jsonObject;
    }
}
