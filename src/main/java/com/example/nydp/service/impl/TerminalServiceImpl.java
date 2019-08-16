package com.example.nydp.service.impl;

import com.example.nydp.dao.TerminalInfoDao;
import com.example.nydp.entity.TerminalInfo;
import com.example.nydp.mapper.TerminalInfoMapper;
import com.example.nydp.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerminalServiceImpl implements TerminalService {

    @Autowired
    TerminalInfoDao dao;

    @Autowired
    TerminalInfoMapper mapper;

    @Override
    public List<TerminalInfo> getlist() {
        return dao.list();
    }

    @Override
    public int selectEqui(String sbid) {
        return dao.selectOneTerminalInfo(sbid);
    }

    @Override
    public TerminalInfo selectTerminalInfo(String sbid) {
        return dao.selectTerminalInfo(sbid);
    }

    @Override
    public TerminalInfo selectTerminalInfoSbid(String sbid) {
        return dao.selectTerminalInfoSbid(sbid);
    }

    @Override
    public int updateTerminalState(String state, String sbid) {
        return dao.updateTerminalState(state,sbid);
    }

}
