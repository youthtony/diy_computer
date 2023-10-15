package com.diy.computer.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diy.computer.dto.PartDto;
import com.diy.computer.common.R;
import com.diy.computer.entity.Category;
import com.diy.computer.entity.Part;
import com.diy.computer.entity.PartAfter;
import com.diy.computer.service.CategoryService;
import com.diy.computer.service.PartService;
import com.diy.computer.service.PartAfterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 零件管理
 */
@RestController
@RequestMapping("/part")
@Slf4j
public class PartController {
    @Autowired
    private PartService partService;
    @Autowired
    private PartAfterService partafterService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增零件
     * @param partDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody PartDto partDto) {
        log.info(partDto.toString());
        partService.saveWithAfter(partDto);
        return R.success("新增零件成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        构造分页构造器
        Page<PartDto> partDtoPage = new Page<>();
        Page<Part> pageInfo = new Page<>(page, pageSize);

//        条件构造器
        LambdaQueryWrapper<Part> queryWrapper = new LambdaQueryWrapper<>();
//        构造条件
        queryWrapper.like(name != null, Part::getName, name);
        queryWrapper.orderByDesc(Part::getUpdateTime);

//        执行
        partService.page(pageInfo, queryWrapper);

//        拷贝到partDtoPage
        BeanUtils.copyProperties(pageInfo, partDtoPage, "records");
        List<Part> records=pageInfo.getRecords();
//        进入循环
        List<PartDto> list = records.stream().map((item) -> {
//            存入对象
            PartDto partDto = new PartDto();
            BeanUtils.copyProperties(item, partDto);
//            获取分类id
            Long catId = item.getCategoryId();
//            获取分类
            Category category = categoryService.getById(catId);
            if (category != null) {
                String catName=category.getName();
//            获取分类名称,存入列表中
                partDto.setCategoryName(catName);
            }
            return partDto;
        }).collect(Collectors.toList());
//        存入Page中
        partDtoPage.setRecords(list);
        return R.success(partDtoPage);
    }

    /**
     * 查询零件及其服务
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<PartDto> get(@PathVariable Long id){
        PartDto partDto = partService.getByIdWithAfter(id);
        return R.success(partDto);
    }

    /**
     * 更新零件信息
     * @param partDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody PartDto partDto) {
        partService.updateWithAfter(partDto);
        return R.success("修改零件完成");
    }

    /**
     * 获取零件列表
     * @param part
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Part>> list(Part part) {
        List<Part> parts=new ArrayList<>();
//        构造条件
        LambdaQueryWrapper<Part> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(part.getCategoryId() != null, Part::getCategoryId, part.getCategoryId());
        queryWrapper.eq(Part::getStatus, 1);
        queryWrapper.orderByAsc(Part::getSort).orderByDesc(Part::getUpdateTime);

        parts = partService.list(queryWrapper);
        return R.success(parts);
    }*/
    @GetMapping("/list")
    public R<List<PartDto>> list(Part part) {

//        构造条件
        LambdaQueryWrapper<Part> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(part.getCategoryId() != null, Part::getCategoryId, part.getCategoryId());
        queryWrapper.eq(Part::getStatus, 1);
        queryWrapper.orderByAsc(Part::getSort).orderByDesc(Part::getUpdateTime);

        List<Part> list = partService.list(queryWrapper);
        List<PartDto> partDtoList = list.stream().map((item) -> {
//            存入对象
            PartDto partDto = new PartDto();
            BeanUtils.copyProperties(item, partDto);
//            获取分类id
            Long categoryId = item.getCategoryId();
//            获取分类
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String catName=category.getName();
//            获取分类名称,存入列表中
                partDto.setCategoryName(catName);
            }
            //当前零件的id
            Long partId = item.getId();
            LambdaQueryWrapper<PartAfter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(PartAfter::getPartId,partId);
            List<PartAfter> partAfterList = partafterService.list(lambdaQueryWrapper);
            partDto.setAfters(partAfterList);
            return partDto;
        }).collect(Collectors.toList());

        //parts = partService.list(queryWrapper);
        return R.success(partDtoList);
    }

    /**
     * 启售停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status,
                          String[] ids){
        for(String id: ids){
            Part part = partService.getById(id);
            part.setStatus(status);
            partService.updateById(part);
        }
        return R.success("修改成功");
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String[] ids){
        for (String id:ids) {
            partService.removeById(id);
        }
        return R.success("删除成功");
    }

}
