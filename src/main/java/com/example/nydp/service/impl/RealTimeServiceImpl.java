package com.example.nydp.service.impl;

import com.example.nydp.dao.RealTimeInfoDao;
import com.example.nydp.entity.RealTimeDateInfo;
import com.example.nydp.mapper.RealTimeDateInfoMapper;
import com.example.nydp.service.RealTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RealTimeServiceImpl implements RealTimeService {

    @Autowired
    RealTimeInfoDao realTimeInfoDao;

    @Autowired
    RealTimeDateInfoMapper realTimeDateInfoMapper;

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Override
    public int saveRealTimeInfo(RealTimeDateInfo dateInfo) {
        RealTimeDateInfo realTimeDateInfo= realTimeInfoDao.save(dateInfo);
        if (realTimeDateInfo!=null){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public int saveRealTimeLogInfo(RealTimeDateInfo date) {
        realTimeDateInfoMapper.insertLog(date);
        return 0;
    }

    @Override
    public int updateRealTimeInfo(RealTimeDateInfo date) {
        return realTimeInfoDao.updateRealTimeInfo(date.getUpdatedate(),date.getAd1(),date.getAd2(),date.getAd3(),date.getAd4(),date.getAd5(),date.getAd6(),date.getAd7(),date.getTerminalno(),date.getAd8(),date.getK9(),date.getK5(),date.getK6());

    }

    @Override
    public int selectOneRealTimeInfo(String no) {
        return realTimeInfoDao.selectOneRealTimeInfo(no);
    }

    @Override
    public int updateKState(String sbid, String state,String k) {
        return realTimeDateInfoMapper.updateKState(sbid,state,k);
    }

    @Override
    public int updateSetWd(String ad9, String sbid) {
        return realTimeInfoDao.updateSetWd(ad9,sbid);
    }

    @Override
    public RealTimeDateInfo selectRealTimeOne(String sbid) {
        return realTimeInfoDao.selectRealTimeInfoOne(sbid);
    }

    @Override
    public int updateTimerInfo(Date time, String sbid) {
        return realTimeInfoDao.updateTimeInfo(time,sbid);
    }

    @Override
    public int updateK10(String sbid) {
        return realTimeInfoDao.updatek10(sbid);
    }

    @Override
    public int updateK9(String sbid,int k9) {
        return realTimeInfoDao.updatek9(sbid,k9);
    }

    @Override
    public List<RealTimeDateInfo> realEquis() {
        return realTimeInfoDao.realTimeList();
    }

    @Override
    public RealTimeDateInfo getRealTimeData(Long id) {
        return realTimeInfoDao.findByTerminalid(id);
    }
}
