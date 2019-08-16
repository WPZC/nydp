package com.example.nydp.mapper;

import com.example.nydp.entity.TerminalInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface TerminalInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table terminal_info
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long terminalId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table terminal_info
     *
     * @mbggenerated
     */
    int insert(TerminalInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table terminal_info
     *
     * @mbggenerated
     */
    int insertSelective(TerminalInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table terminal_info
     *
     * @mbggenerated
     */
    TerminalInfo selectByPrimaryKey(Long terminalId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table terminal_info
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(TerminalInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table terminal_info
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(TerminalInfo record);
}