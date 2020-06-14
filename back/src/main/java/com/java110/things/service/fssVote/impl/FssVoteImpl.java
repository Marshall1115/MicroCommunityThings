package com.java110.things.service.fssVote.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IFssVoteDao;
import com.java110.things.entity.FssVote;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.fssVote.IFssVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("fssVoteServiceImpl")
public class FssVoteImpl implements IFssVoteService {

    @Autowired
    private IFssVoteDao voteDao;

    /**
     * 添加用户信息
     *
     * @param vote 添加用户
     * @return
     */
    @Override
    public ResultDto insert(FssVote vote) throws Exception {
        vote.setCreateTime(new Date());
        int count = voteDao.insert(vote);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }



}
