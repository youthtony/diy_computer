package com.diy.computer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diy.computer.common.R;
import com.diy.computer.entity.Employee;
import com.diy.computer.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录方法
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
//        根据用户名比对数据库

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //没有用户名
        if (emp == null) {
            return R.error("用户名不存在");
        }
//        比对密码
        //密码错误
        if (!emp.getPassword().equals(password)) {
            return R.error("账户或密码错误");
        }
//        查看员工账号状态
        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用");
        }
        //全部匹配，登录成功
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
//        构造分页构造器
        Page pageInfo = new Page(page, pageSize);
//        条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
//        name为空时不执行
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        排序方式
        queryWrapper.orderByDesc(Employee::getUpdateTime);

//        执行
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updateEmployee(@RequestBody Employee employee){
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 修改信息时获取数据，id来自方法中
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getByID(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("未查询到对应员工信息");
    }

    /**
     * 新增员工
     * @param session
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpSession session, @RequestBody Employee employee) {
//        设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        存入数据库,如果出错，则会被异常处理类捕获
        employeeService.save(employee);
        return R.success("成功添加成员：" + employee.getName());
    }

    /**
     * 退出
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("employee");
        return R.success("退出成功");
    }
}
