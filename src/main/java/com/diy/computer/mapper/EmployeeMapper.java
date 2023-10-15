package com.diy.computer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diy.computer.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
//    一般的增删改查已经继承过来

}
