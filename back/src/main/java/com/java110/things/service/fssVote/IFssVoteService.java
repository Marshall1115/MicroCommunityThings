package com.java110.things.service.fssVote;

import com.java110.things.entity.FssVote;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IFssVoteService {

    /**
     * 保存用户信息
     * @param vote 用户信息
     * @return
     * @throws Exception
     */

    public ResultDto insert(FssVote vote) throws Exception;


}
