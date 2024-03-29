package com.java110.core.dao;

import com.java110.entity.machine.MachineAttrDto;
import com.java110.entity.machine.MachineDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IMachineServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IMachineServiceDao {

    /**
     * 保存设备信息
     *
     * @param machineDto 设备信息
     * @return 返回影响记录数
     */
    int saveMachine(MachineDto machineDto);

    /**
     * 查询设备信息
     *
     * @param machineDto 设备信息
     * @return
     */
    List<MachineDto> getMachines(MachineDto machineDto);

    /**
     * 查询设备信息
     *
     * @param machineDto 设备信息
     * @return
     */
    List<MachineDto> queryBoxMachines(MachineDto machineDto);


    /**
     * 查询设备总记录数
     *
     * @param machineDto 设备信息
     * @return
     */
    long getMachineCount(MachineDto machineDto);

    /**
     * 修改设备信息
     *
     * @param machineDto 设备信息
     * @return 返回影响记录数
     */
    int updateMachine(MachineDto machineDto);

    /**
     * 查询属性
     *
     * @param machineDto
     * @return
     */
    List<MachineAttrDto> getMachineAttrs(MachineAttrDto machineDto);

    int saveMachineAttr(MachineAttrDto machineAttrDto);

    int updateMachineAttr(MachineAttrDto machineAttrDto);
}
