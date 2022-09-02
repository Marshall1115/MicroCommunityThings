package com.java110.api.controller.community;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.BaseController;
import com.java110.entity.community.CommunityDto;
import com.java110.entity.response.ResultDto;
import com.java110.core.service.community.ICommunityService;
import com.java110.core.util.Assert;
import com.java110.core.util.BeanConvertUtil;
import com.java110.core.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName CommunityController
 * @Description TODO 小区信息控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/community")
public class CommunityController extends BaseController {

    @Autowired
    private ICommunityService communityServiceImpl;

    /**
     * 添加设备接口类
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveCommunity", method = RequestMethod.POST)
    public ResponseEntity<String> saveCommunity(@RequestBody String param, HttpServletRequest request) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "address", "请求报文中未包含地址");
        Assert.hasKeyAndValue(paramObj, "cityCode", "请求报文中未包含城市地区");

        Assert.hasKeyAndValue(paramObj, "name", "请求报文中未包含小区名称");
        CommunityDto communityDto = BeanConvertUtil.covertBean(paramObj, CommunityDto.class);
        communityDto.setAppId(super.getAppId(request));
        communityDto.setCommunityId(SeqUtil.getId());

        ResultDto resultDto = communityServiceImpl.saveCommunity(communityDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 更新小区信息
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateCommunitys", method = RequestMethod.POST)
    public ResponseEntity<String> updateCommunitys(@RequestBody String param, HttpServletRequest request) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "address", "请求报文中未包含地址");
        Assert.hasKeyAndValue(paramObj, "name", "请求报文中未包含小区名称");
        Assert.hasKeyAndValue(paramObj, "extCommunityId", "请求报文中未包含外部编码");
        CommunityDto communityDto = BeanConvertUtil.covertBean(paramObj, CommunityDto.class);
        ResultDto resultDto = communityServiceImpl.updateCommunity(communityDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 添加设备接口类
     *
     * @param communityId 页数
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getCommunitys", method = RequestMethod.GET)
    public ResponseEntity<String> getCommunitys(@RequestParam String communityId,
                                                @RequestParam int page,
                                                @RequestParam int row,
                                                HttpServletRequest request) throws Exception {

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(communityId);
        communityDto.setPage(page);
        communityDto.setRow(row);

        String appId = super.getAppId(request);

        communityDto.setAppId(appId);

        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除设备 动作
     *
     * @param paramIn 入参
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteCommunity", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCommunity(@RequestBody String paramIn) throws Exception {
        JSONObject paramObj = super.getParamJson(paramIn);

        Assert.hasKeyAndValue(paramObj, "communityId", "请求报文中未包含硬件ID");

        ResultDto resultDto = communityServiceImpl.deleteCommunity(BeanConvertUtil.covertBean(paramObj, CommunityDto.class));
        return super.createResponseEntity(resultDto);
    }
}
