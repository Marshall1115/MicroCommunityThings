package com.java110.core.service.cityArea.impl;

import com.java110.core.service.cityArea.ICityAreaService;
import com.java110.core.dao.ICityAreaServiceDao;
import com.java110.entity.cityArea.CityAreaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 城市编码实现类
 */
@Service
public class CityAreaServiceImpl implements ICityAreaService {
    @Autowired
    private ICityAreaServiceDao cityAreaServiceDaoImpl;

    @Override
    public List<CityAreaDto> getCityAreas(CityAreaDto cityAreaDto) {
        return cityAreaServiceDaoImpl.getCityAreas(cityAreaDto);
    }
}
