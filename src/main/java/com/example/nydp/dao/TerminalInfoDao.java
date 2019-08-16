package com.example.nydp.dao;

import com.example.nydp.entity.TerminalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TerminalInfoDao extends JpaRepository<TerminalInfo,Integer> {


    /**
     * 查询个数
     * @param sbid
     * @return
     */
    @Query(nativeQuery = true,value = "SELECT COUNT(*) FROM `terminal_info` WHERE Terminal_CommunicationID = ?1")
    int selectOneTerminalInfo(String sbid);

    /**
     * 查询所有
     * @return
     */
    @Query(value = "select t from TerminalInfo t")
    List<TerminalInfo> list();

    /**
     * 查询TerminalId
     * @param sbid
     * @return
     */
    @Query(nativeQuery = true,value = "SELECT * FROM `terminal_info` WHERE Terminal_CommunicationID = ?1")
    TerminalInfo selectTerminalInfo(String sbid);

    /**
     * 查询TerminalId
     * @param sbid
     * @return
     */
    @Query(nativeQuery = true,value = "SELECT * FROM `terminal_info` WHERE Terminal_ID = ?1")
    TerminalInfo selectTerminalInfoSbid(String sbid);

    @Transactional //注解用于提交事务，若没有带上这句，会报事务异常提示。
    @Modifying(clearAutomatically = true) //自动清除实体里保存的数据。
    @Query(value = "update TerminalInfo tr set tr.remarks=?1 where tr.terminalCommunicationid=?2")
    int updateTerminalState(String state,String sbh);


}
