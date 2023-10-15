package com.diy.computer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diy.computer.common.CustomException;
import com.diy.computer.entity.Part;
import com.diy.computer.entity.Setmeal;
import com.diy.computer.service.CategoryService;
import com.diy.computer.service.PartService;
import com.diy.computer.service.SetmealService;
import com.diy.computer.entity.Category;
import com.diy.computer.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private PartService partService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，但要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Part> partLambdaQueryWrapper=new LambdaQueryWrapper<>();
//        构建条件
        partLambdaQueryWrapper.eq(Part::getCategoryId, id);
        int count1 = partService.count(partLambdaQueryWrapper);
//        查询分类是否关联零件或套餐
        if (count1 > 0) {
//            说明有关联,异常退出
            throw new CustomException("该分类与零件关联，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
//            说明有关联，异常退出
            throw new CustomException("该分类与套餐关联，不能删除");
        }

//        正常删除
        super.removeById(id);
    }
}
