package com.java110.things.service.user;

import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import org.springframework.http.ResponseEntity;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IUserService {

     ResultDto login(UserDto userDto) throws Exception;

    /**
     * 查询用户信息
     * @param userDto 用户信息
     * @return 返回用户信息
     * @throws Exception
     */
    ResultDto getUser(UserDto userDto) throws Exception;

    /**
     * 退出登录
     * @param token token信息
     * @return
     * @throws Exception
     */
    ResultDto loginOut(String token) throws Exception;

    /**
     * 修改密码
     * @param uid 用户id
     * @param oldpwd 旧密码
     * @param newpwd 新密码
     * @return
     * @throws Exception
     */
    ResultDto changePassword(String uid,String oldpwd,String newpwd) throws Exception;

    /**
     * 查询用户信息
     * @param userDto 用户信息
     * @return
     * @throws Exception
     */
    public ResultDto getUserList(UserDto userDto)throws Exception;


    /**
     * 保存用户信息
     * @param userDto 用户信息
     * @return
     * @throws Exception
     */

    public ResultDto insertUser(UserDto userDto) throws Exception;

    public ResultDto updateUser(UserDto userDto) throws Exception;

    public ResultDto deleteUser(UserDto userDto)  throws Exception;
}
