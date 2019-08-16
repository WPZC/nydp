package com.example.nydp;

import com.example.nydp.dao.TerminalInfoDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class t1 {

    @Autowired
    TerminalInfoDao dao;

    @Test
    public void test(){
        System.out.println(dao.selectOneTerminalInfo("DFZD0002"));
    }

}
