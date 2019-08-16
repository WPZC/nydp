package com.example.nydp.service;

import com.example.nydp.entity.RealTimeDateInfo;

import java.util.Date;
import java.util.List;

public interface RealTimeService {

    /**
     * 存为实时
     * @param dateInfo
     * @return
     */
    int saveRealTimeInfo(RealTimeDateInfo dateInfo);

    /**
     * 存为历史数据
     * @param dateInfo
     * @return
     */
    int saveRealTimeLogInfo(RealTimeDateInfo dateInfo);

    /**
     * 更新实事数据
     * @param dateInfo
     * @return
     */
    int updateRealTimeInfo(RealTimeDateInfo dateInfo);

    /**
     * 查询实事数据中是否存在设备
     * @param no 设备号
     * @return
     */
    int selectOneRealTimeInfo(String no);

    /**
     * 修改继电器状态
     * @param sbid
     * @param state
     * @return
     */
    int updateKState(String sbid,String state,String k);

    /**
     * 设置温度
     * @param ad9
     * @param sbid
     * @return
     */
    int updateSetWd(String ad9,String sbid);

    /**
     * 根据设备id查询
     * @param sbid
     * @return
     */
    RealTimeDateInfo selectRealTimeOne(String sbid);

    /**
     * 根据心跳修改时间实事数据时间
     * @param time
     * @param sbid
     * @return
     */
    int updateTimerInfo(Date time, String sbid);

    /**
     * 修改是否在线设备的状态
     * @return
     */
    int updateK10(String sbid);

    /**
     * 修改设备状态
     * @return
     */
    int updateK9(String sbid,int k9);

    /**
     * 查询所有设备
     * @return
     */
    List<RealTimeDateInfo> realEquis();

    /**
     * 获去实时数据
     * @param id
     * @return
     */
    RealTimeDateInfo getRealTimeData(Long id);
}
