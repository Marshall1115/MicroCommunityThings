package com.java110.things.Controller.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.FssVote;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.service.fssVote.IFssVoteService;
import com.java110.things.service.user.IUserService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:20
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
@RestController
@RequestMapping(path = "/api/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userServiceImpl;

    @Autowired
    private IFssVoteService voteService;

    @RequestMapping(path = "/saveFssVote", method = RequestMethod.POST)
    public ResponseEntity<String> saveFssVote(@RequestBody String param, HttpServletRequest request) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "title1", "请选择愿意或不愿意。");
        Assert.hasKeyAndValue(paramObj, "building", "请选择楼栋");
        Assert.hasKeyAndValue(paramObj, "room", "请输入房间号");
        Assert.hasKeyAndValue(paramObj, "name", "请输入姓名");
        Assert.hasKeyAndValue(paramObj, "tel", "请输入电话");
        paramObj.put("fromIp",this.getIpAddress(request));
        ResultDto resultDto = voteService.insert(BeanConvertUtil.covertBean(paramObj, FssVote.class));
        return super.createResponseEntity(resultDto);
    }

    private String getIpAddress(HttpServletRequest request) {
        String url = request.getRequestURL().toString();// 获得客户端发送请求的完整url
        String ip2 = request.getRemoteAddr();// 返回发出请求的IP地址
        String params = request.getQueryString();// 返回请求行中的参数部分
        String host = request.getRemoteHost();// 返回发出请求的客户机的主机名
        int port = request.getRemotePort();// 返回发出请求的客户机的端口号。
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip+";"+";"+ip2+url+";"+params+";"+host+";"+port;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody String param, HttpServletRequest request) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "username", "请求报文中未包含用户名信息");

        Assert.hasKeyAndValue(paramObj, "password", "请求报文中未包含密码信息");

        ResultDto resultDto = userServiceImpl.login(BeanConvertUtil.covertBean(paramObj, UserDto.class));
        request.setAttribute(SystemConstant.COOKIE_AUTH_TOKEN, ((JSONObject) resultDto.getData()).getString("token"));
        return super.createResponseEntity(resultDto);
    }

    @RequestMapping(path = "/info", method = RequestMethod.GET)
    public ResponseEntity<String> getUserInfo(HttpServletRequest request) throws Exception {
        String userId = super.getUserId(request);
        Assert.hasText(userId, "用户还未登录");

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);

        ResultDto resultDto = userServiceImpl.getUser(userDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 退出登录
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "token", "请求报文中未包含token信息");

        ResultDto resultDto = userServiceImpl.loginOut(paramObj.getString("token"));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 修改密码
     *
     * @param param 新旧密码
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity<String> changePassword(HttpServletRequest request, @RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "oldpwd", "请求报文中未包含旧密码");
        Assert.hasKeyAndValue(paramObj, "newpwd", "请求报文中未包含新密码");
        String userId = super.getUserId(request);
        Assert.hasText(userId, "用户还未登录");
        String oldpwd = paramObj.get("oldpwd").toString();
        String newpwd = paramObj.get("newpwd").toString();
        ResultDto resultDto = userServiceImpl.changePassword(userId, oldpwd, newpwd);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 用户列表
     *
     * @param page 页数
     * @param row  每页显示的数量
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getUsers", method = RequestMethod.GET)
    public ResponseEntity<String> getUsers(@RequestParam int page,
                                           @RequestParam int row,
                                           @RequestParam(name = "username", required = false) String username,
                                           @RequestParam(name = "tel", required = false) String tel) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setPage(page);
        userDto.setRow(row);
        userDto.setUsername(username);
        userDto.setTel(tel);
        ResultDto resultDto = userServiceImpl.getUserList(userDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 添加用户
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/insertUser", method = RequestMethod.POST)
    public ResponseEntity<String> insertUser(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
//        Assert.hasKeyAndValue(paramObj, "userId", "请求报文中未包含用户ID");
        Assert.hasKeyAndValue(paramObj, "username", "请求报文中未包含用户账户名称");
        Assert.hasKeyAndValue(paramObj, "tel", "请求报文中未包电话");
        paramObj.put("userId", SeqUtil.getId());
        // Assert.hasKeyAndValue(paramObj, "password", "请求报文中未包密码");
        ResultDto resultDto = userServiceImpl.insertUser(BeanConvertUtil.covertBean(paramObj, UserDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 更细用户
     *
     * @param param 请求报文
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateUser", method = RequestMethod.POST)
    public ResponseEntity<String> updateUser(@RequestBody String param) throws Exception {
        JSONObject paramObj = super.getParamJson(param);
        Assert.hasKeyAndValue(paramObj, "id", "请求报文中未包含用户表ID");
        Assert.hasKeyAndValue(paramObj, "userId", "请求报文中未包含用户ID");
        Assert.hasKeyAndValue(paramObj, "username", "请求报文中未包含用户账户名称");
        Assert.hasKeyAndValue(paramObj, "tel", "请求报文中未包电话");
        ResultDto resultDto = userServiceImpl.updateUser(BeanConvertUtil.covertBean(paramObj, UserDto.class));
        return super.createResponseEntity(resultDto);
    }

    /**
     * 删除用户
     *
     * @param uid 表id
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/deleteUser", method = RequestMethod.POST)
    public ResponseEntity<String> deleteUser(@RequestBody String uid) throws Exception {
        JSONObject paramObj = super.getParamJson(uid);
        Assert.hasKeyAndValue(paramObj, "userId", "请求报文中未包含用户ID");

        ResultDto resultDto = userServiceImpl.deleteUser(BeanConvertUtil.covertBean(paramObj, UserDto.class));
        return super.createResponseEntity(resultDto);
    }

    public IUserService getUserServiceImpl() {
        return userServiceImpl;
    }

    public void setUserServiceImpl(IUserService userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }
}
