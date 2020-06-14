package com.java110.things.dao;

import com.java110.things.entity.FssVote;
import com.java110.things.entity.user.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IUserServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 20:40
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
@Mapper
public interface IFssVoteDao {

    /**
     * 保存用户信息
     *
     * @param vote 用户信息
     * @return 返回影响记录数
     */
    int insert(FssVote vote);
}
