package com.diy.computer.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diy.computer.common.R;
import com.diy.computer.dto.PartDto;
import com.diy.computer.entity.Category;
import com.diy.computer.entity.Part;
import com.diy.computer.entity.Setmeal;
import com.diy.computer.entity.SetmealPart;
import com.diy.computer.service.CategoryService;
import com.diy.computer.service.PartService;
import com.diy.computer.service.SetmealService;
import com.diy.computer.dto.SetmealDto;
import com.diy.computer.service.SetmealPartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealPartService setmealPartService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private PartService partService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithPart(setmealDto);
        return R.success("新增套餐完成");
    }

    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        构造分页构造器
        Page<SetmealDto> setmealDtoPage = new Page<>();
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
//        关于条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
//        执行
        setmealService.page(pageInfo, queryWrapper);

        //        拷贝到partDtoPage
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
//        进入循环
        List<SetmealDto> list = records.stream().map((item) -> {
//            存入对象
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
//            获取分类id
            Long catId = item.getCategoryId();
//            获取分类
            Category category = categoryService.getById(catId);
            if (category != null) {
                String catName = category.getName();
//            获取分类名称,存入列表中
                setmealDto.setCategoryName(catName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
//        存入Page中
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐及关联数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithPart(ids);
        return R.success("选中套餐删除成功！");
    }

    /**
     * 设置套餐状态为0
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> updateSetmealToDown(@RequestParam List<Long> ids) {
        setmealService.updateWithPartToStatus(ids, 0);
        return R.success("套餐信息修改成功");
    }

    /**
     * 设置套餐状态为1
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> updateSetmealToUp(@RequestParam List<Long> ids) {
        setmealService.updateWithPartToStatus(ids, 1);
        return R.success("套餐信息修改成功");
    }

    /**
     * 根据套餐id获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithPart(id);
        //       获取分类名
        String catName = categoryService.getById(setmealDto.getCategoryId()).getName();
        setmealDto.setCategoryName(catName);
        return R.success(setmealDto);
    }

    //修改套餐
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 点击查看套餐中的零件
     */
    /*@GetMapping("/dish/{id}")
    public R<List<PartDto>> dish(@PathVariable("id") Long SetmealId) {
        LambdaQueryWrapper<SetmealPart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealPart::getSetmealId, SetmealId);
        //获取套餐里面的所有零件  这个就是SetmealDish表里面的数据
        List<SetmealPart> list = setmealPartService.list(queryWrapper);

        List<PartDto> dishDtos = list.stream().map((setmealDish) -> {
            PartDto dishDto = new PartDto();
            //将套餐零件关系表中的数据拷贝到dishDto中
            BeanUtils.copyProperties(setmealDish, dishDto);
            //这里是为了把套餐中的零件的基本信息填充到dto中，比如零件描述，零件图片等零件的基本信息
            Long dishId = setmealDish.getPartId();
            Part dish = partService.getById(dishId);
            //将零件信息拷贝到dishDto中
            BeanUtils.copyProperties(dish, dishDto);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }*/
    /**
     * 点击查看套餐中的零件
     */
    @GetMapping("/dish/{id}")
    public R<List<PartDto>> dish(@PathVariable("id") Long SetmealId) {
        LambdaQueryWrapper<SetmealPart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealPart::getSetmealId, SetmealId);
        //获取套餐里面的所有零件  这个就是SetmealDish表里面的数据
        List<SetmealPart> list = setmealPartService.list(queryWrapper);

        List<PartDto> dishDtos = list.stream().map((setmealPart) -> {
            PartDto dishDto = new PartDto();
            //将套餐零件关系表中的数据拷贝到dishDto中
            BeanUtils.copyProperties(setmealPart, dishDto);
            //这里是为了把套餐中的零件的基本信息填充到dto中，比如零件描述，零件图片等零件的基本信息
            Long dishId = setmealPart.getPartId();
            Part dish = partService.getById(dishId);
            //将零件信息拷贝到dishDto中
            BeanUtils.copyProperties(dish, dishDto);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }



}
