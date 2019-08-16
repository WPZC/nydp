package com.example.nydp.dao;

import com.example.nydp.entity.RealTimeDateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;

public interface RealTimeInfoDao extends JpaRepository<RealTimeDateInfo,Integer>{

    @Transactional //注解用于提交事务，若没有带上这句，会报事务异常提示。
    @Modifying(clearAutomatically = true) //自动清除实体里保存的数据。
    @Query(value = "update RealTimeDateInfo rd set rd.updatedate=?1,rd.ad1=?2,rd.ad2=?3,rd.ad3=?4,rd.ad4=?5,rd.ad5=?6,rd.ad6=?7,rd.ad7=?8,rd.ad8=?10,rd.k10=0,rd.k9=?11,rd.k5=?12,rd.k6=?13 where rd.terminalno=?9")
    int updateRealTimeInfo(Date date,String ad1,String ad2,String ad3,String ad4,String ad5,String ad6,String ad7,String terminalno,String ad8,Integer k9,Integer k5,Integer k6);

    @Query(nativeQuery = true,value = "select COUNT(*) FROM RealTimeDateInfo WHERE terminalno=?1")
    int selectOneRealTimeInfo(String terminalno);

    @Query(value = "select rd from RealTimeDateInfo rd where rd.terminalno=?1")
    RealTimeDateInfo selectRealTimeInfoOne(String sbid);

    @Transactional //注解用于提交事务，若没有带上这句，会报事务异常提示。
    @Modifying(clearAutomatically = true) //自动清除实体里保存的数据。
    @Query(value = "update RealTimeDateInfo rd set rd.ad9=?1 where rd.terminalno=?2")
    int updateSetWd(String ad9,String terminalno);

    @Transactional //注解用于提交事务，若没有带上这句，会报事务异常提示。
    @Modifying(clearAutomatically = true) //自动清除实体里保存的数据。
    @Query(value = "update RealTimeDateInfo rd set rd.updatedate=?1,rd.k10 = 0 where rd.terminalno=?2")
    int updateTimeInfo(Date time, String sbid);

    @Query(value = "select rd from RealTimeDateInfo rd")
    List<RealTimeDateInfo> realTimeList();

    @Transactional //注解用于提交事务，若没有带上这句，会报事务异常提示。
    @Modifying(clearAutomatically = true) //自动清除实体里保存的数据。
    @Query(value = "update RealTimeDateInfo rd set rd.k10 = 1 where rd.terminalno=?1")
    int updatek10(String sbid);

    @Transactional //注解用于提交事务，若没有带上这句，会报事务异常提示。
    @Modifying(clearAutomatically = true) //自动清除实体里保存的数据。
    @Query(value = "update RealTimeDateInfo rd set rd.k9 = 1 where rd.terminalno=?1")
    int updatek9(String sbid,int k9);

    RealTimeDateInfo findByTerminalid(Long id);
}
