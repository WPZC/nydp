package com.example.nydp.service;

import com.example.nydp.entity.TerminalInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TerminalService {

    /**
     * 获取所有设备
     * @return
     */
    List<TerminalInfo> getlist();

    /**
     * 查询单个设备
     * @param sbid
     * @return
     */
    int selectEqui(String sbid);

    /**
     * 根据设备id查寻
     * @param sbid
     * @return
     */
    TerminalInfo selectTerminalInfo(String sbid);

    /**
     * 根据自定义设备id查寻
     * @param sbid
     * @return
     */
    TerminalInfo selectTerminalInfoSbid(String sbid);

    /**
     * 根据心跳修改状态
     * @param state
     * @param sbid
     * @return
     */
    int updateTerminalState(String state,String sbid);

    /**
     * 根据心跳修改时间实事数据时间
     * @param time
     * @param sbid
     * @return
     */
    //int updateTimerInfo(String time,String sbid);


}
