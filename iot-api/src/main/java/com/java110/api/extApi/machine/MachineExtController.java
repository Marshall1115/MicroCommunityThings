/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.api.extApi.machine;

import com.alibaba.fastjson.JSONObject;
import com.java110.api.ws.BarrierGateControlWebSocketServer;
import com.java110.core.BaseController;
import com.java110.core.entity.car.BarrierGateControlDto;
import com.java110.core.service.car.ICarInoutService;
import com.java110.core.service.community.ICommunityService;
import com.java110.core.service.machine.IMachineService;
import com.java110.core.service.openDoor.IManualOpenDoorLogService;
import com.java110.core.service.parkingArea.IParkingAreaService;
import com.java110.core.util.Assert;
import com.java110.core.util.BeanConvertUtil;
import com.java110.core.util.DateUtil;
import com.java110.core.util.SeqUtil;
import com.java110.entity.accessControl.UserFaceDto;
import com.java110.entity.car.CarInoutDto;
import com.java110.entity.community.CommunityDto;
import com.java110.entity.machine.MachineDto;
import com.java110.entity.openDoor.ManualOpenDoorLogDto;
import com.java110.entity.parkingArea.ParkingAreaDto;
import com.java110.entity.parkingArea.ParkingBoxAreaDto;
import com.java110.entity.parkingArea.ParkingBoxDto;
import com.java110.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.entity.response.ResultDto;
import com.java110.intf.inner.IVideoInnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 设备对外 控制类
 * <p>
 * 完成小区添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/machine")
public class MachineExtController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(MachineExtController.class);

    @Autowired
    IMachineService machineServiceImpl;

    @Autowired
    ICommunityService communityServiceImpl;

    @Autowired
    private ICarInoutService carInoutServiceImpl;


    @Autowired
    private IParkingAreaService parkingAreaServiceImpl;

    @Autowired
    private IVideoInnerService videoInnerServiceImpl;

    @Autowired
    private IManualOpenDoorLogService manualOpenDoorLogServiceImpl;

    /**
     * 添加设备信息
     * <p>
     *
     * @param reqParam {
     *                 "machineCode":""
     *                 machine_name
     *                 machine_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_machine_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addMachine", method = RequestMethod.POST)
    public ResponseEntity<String> addMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含设备编码");
        Assert.hasKeyAndValue(reqJson, "machineName", "未包含设备名称");
        Assert.hasKeyAndValue(reqJson, "machineTypeCd", "未包含设备类型");
        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "locationType", "未包含位置类型");
        Assert.hasKeyAndValue(reqJson, "locationObjId", "未包含位置ID");
        Assert.hasKeyAndValue(reqJson, "extCommunityId", "未包含外部小区编码");
        Assert.hasKeyAndValue(reqJson, "direction", "未包含设备方向");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(reqJson.getString("extCommunityId"));
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        Assert.listOnlyOne(communityDtos, "未找到小区信息");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        machineDto.setMachineId(SeqUtil.getId());
        machineDto.setCommunityId(communityDtos.get(0).getCommunityId());
        if (!reqJson.containsKey("machineVersion")) {
            machineDto.setMachineVersion("v1.0");
        }
        if (!reqJson.containsKey("machineIp")) {
            machineDto.setMachineIp("192.168.1.1:80");
        }
        ResultDto result = machineServiceImpl.saveMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改设备信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC小区",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateMachine", method = RequestMethod.POST)
    public ResponseEntity<String> updateMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");


        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);


        ResultDto result = machineServiceImpl.updateMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 删除设备信息
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteMachine", method = RequestMethod.POST)
    public ResponseEntity<String> deleteMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extMachineId", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.deleteMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 远程开门
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/openDoor", method = RequestMethod.POST)
    public ResponseEntity<String> openDoor(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.openDoor(machineDto, null, reqJson);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 远程开门
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/closeDoor", method = RequestMethod.POST)
    public ResponseEntity<String> closeDoor(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.closeDoor(machineDto, null);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 获取二维码
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getQRcode", method = RequestMethod.POST)
    public ResponseEntity<String> getQRcode(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "userId", "未包含人员ID");
        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含人员设备");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        UserFaceDto userFaceDto = BeanConvertUtil.covertBean(reqJson, UserFaceDto.class);
        ResultDto result = machineServiceImpl.getQRcode(userFaceDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 重启设备
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/restartMachine", method = RequestMethod.POST)
    public ResponseEntity<String> restartMachine(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "machineCode", "未包含外部设备编码");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        MachineDto machineDto = BeanConvertUtil.covertBean(reqJson, MachineDto.class);
        ResultDto result = machineServiceImpl.restartMachine(machineDto);

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 重启设备
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/customCarInOut", method = RequestMethod.POST)
    public ResponseEntity<String> customCarInOut(@RequestBody String reqParam) throws Exception {

        JSONObject paramObj = JSONObject.parseObject(reqParam);
        Assert.hasKeyAndValue(paramObj, "extMachineId", "请求报文中未包含设备ID");
        Assert.hasKeyAndValue(paramObj, "carNum", "请求报文中未包含车辆编号");
        Assert.hasKeyAndValue(paramObj, "type", "请求报文中未包含类型");

        MachineDto machineDto = new MachineDto();
        machineDto.setExtMachineId(paramObj.getString("extMachineId"));
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
        parkingAreaTextDto.setCarNum(paramObj.getString("carNum"));
        resultDto = machineServiceImpl.openDoor(machineDto, parkingAreaTextDto, paramObj);

        return ResultDto.createResponseEntity(resultDto);
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
            carInoutDto.setPayType(acceptJson.containsKey("payType") ? acceptJson.getString("payType") : CarInoutDto.PAY_TYPE_CASH);
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
        carInoutDto.setPaId(paId);
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
        carInoutDto.setPaId(paId);
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

    /**
     * 播放视频
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/playVideo", method = RequestMethod.POST)
    public ResponseEntity<String> playVideo(@RequestBody String reqParam) throws Exception {
        JSONObject paramIn = JSONObject.parseObject(reqParam);

        return videoInnerServiceImpl.doPlay(paramIn);
    }

    /**
     * 存活检测
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/heartbeatVideo", method = RequestMethod.POST)
    public ResponseEntity<String> heartbeatVideo(@RequestBody String reqParam) throws Exception {

        JSONObject paramIn = JSONObject.parseObject(reqParam);
        ///play/{deviceId}/{channelId}/{mediaProtocol}
        return videoInnerServiceImpl.heartbeatVideo(paramIn);

    }


    /**
     * 重启设备
     * <p>
     *
     * @param reqParam {
     *                 "extMachineId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/byeVideo", method = RequestMethod.POST)
    public ResponseEntity<String> byeVideo(@RequestBody String reqParam) throws Exception {

        JSONObject paramIn = JSONObject.parseObject(reqParam);
        return videoInnerServiceImpl.bye(paramIn);
    }

    /**
     * 查询手动开闸记录
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getManualOpenDoorLogs", method = RequestMethod.POST)
    public ResponseEntity<String> getManualOpenDoorLogs(@RequestBody String reqParam) throws Exception {

        JSONObject paramIn = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(paramIn, "extCommunityId", "未包含小区");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setExtCommunityId(paramIn.getString("extCommunityId"));
        List<CommunityDto> communityDtos = communityServiceImpl.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "未找到小区信息");


        ManualOpenDoorLogDto manualOpenDoorLogDto = BeanConvertUtil.covertBean(paramIn, ManualOpenDoorLogDto.class);

        manualOpenDoorLogDto.setCommunityId(communityDtos.get(0).getCommunityId());

        ResultDto resultDto = manualOpenDoorLogServiceImpl.getManualOpenDoorLog(manualOpenDoorLogDto);
        return super.createResponseEntity(resultDto);
    }


}
