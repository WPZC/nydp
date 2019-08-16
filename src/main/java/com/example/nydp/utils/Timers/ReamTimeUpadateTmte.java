package com.example.nydp.utils.Timers;

import com.example.nydp.entity.RealTimeDateInfo;
import com.example.nydp.service.RealTimeService;
import com.example.nydp.service.TerminalService;
import com.example.nydp.utils.Context;
import com.example.nydp.utils.SpringUtils;
import com.timer.TimerFactory;
import com.timer.TimerRun;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class ReamTimeUpadateTmte extends TimerRun {

    @Autowired
    TerminalService service = SpringUtils.getApplicationContext().getBean(TerminalService.class);

    @Autowired
    RealTimeService realTimeService = SpringUtils.getApplicationContext().getBean(RealTimeService.class);

    @Override
    public void run() {
        List<RealTimeDateInfo> realTimeDateInfos = realTimeService.realEquis();
        for (int i = 0;i<realTimeDateInfos.size();i++){
            //判断设备是否在线
            if(Context.realtimeChannel.get(realTimeDateInfos.get(i).getTerminalno())==null){
                realTimeService.updateK10(realTimeDateInfos.get(i).getTerminalno());
                service.updateTerminalState("1",realTimeDateInfos.get(i).getTerminalno());
            }else{//对在线的设备进行按时间筛选
                if(realTimeDateInfos.get(i).getK10()==0){//筛选在线的
                    //判断是否超过了5分钟，超过则离线
                    System.out.println(new Date().getTime()-realTimeDateInfos.get(i).getUpdatedate().getTime());
                    if((new Date().getTime()-realTimeDateInfos.get(i).getUpdatedate().getTime())>300000){
                        realTimeService.updateK10(realTimeDateInfos.get(i).getTerminalno());
                        service.updateTerminalState("1",realTimeDateInfos.get(i).getTerminalno());
                        //离线以后可以清除map中的数据
                    }
                }
            }
        }
    }
}
