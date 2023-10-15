package com.diy.computer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diy.computer.common.CustomException;
import com.diy.computer.dto.SetmealDto;
import com.diy.computer.entity.Setmeal;
import com.diy.computer.entity.SetmealPart;
import com.diy.computer.mapper.SetmealMapper;
import com.diy.computer.service.SetmealPartService;
import com.diy.computer.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealPartService setmealPartService;

    /**
     * 保存
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithPart(SetmealDto setmealDto) {
//        保存套餐
        this.save(setmealDto);
//        保存套餐详情
        Long setmealId=setmealDto.getId();
        List<SetmealPart> setmealParts=setmealDto.getSetmealParts();
        for (SetmealPart item :
                setmealParts) {
            item.setSetmealId(setmealId);
        }
        setmealPartService.saveBatch(setmealParts);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithPart(List<Long> ids) {
//        确认是否可删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
//            有售卖中的套餐，不能删除
            throw new CustomException("套餐正在售卖中，不能删除");
        }
//         删除套餐表信息，批量删除
        this.removeByIds(ids);
//        删除套餐明细信息 delete from setmeal_part where id in (ids)
        LambdaQueryWrapper<SetmealPart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealPart::getSetmealId, ids);
        setmealPartService.remove(lambdaQueryWrapper);
    }

    /**
     * 更新指定套餐的售卖状态
     * @param ids
     * @param status
     */
    @Override
    public void updateWithPartToStatus(List<Long> ids,int status) {
//        修改状态
        List<Setmeal> setmeals=new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Setmeal setmeal=this.getById(ids.get(i));
            setmeal.setStatus(status);
            setmeals.add(setmeal);
        }
        this.updateBatchById(setmeals);
    }

    /**
     * 根据id获取套餐信息
     * @param id
     * @return
     */
    @Transactional
    @Override
    public SetmealDto getByIdWithPart(Long id) {
//        1、获取套餐信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
//        构建套餐详细信息条件
        LambdaQueryWrapper<SetmealPart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealPart::getSetmealId, setmeal.getId());
//       2、获取套餐详情信息
        List<SetmealPart> parts = setmealPartService.list(queryWrapper);
        setmealDto.setSetmealParts(parts);
        return setmealDto;
    }

    @Override
    public void updateWithPart(SetmealDto setmealDto) {
//        跟新套餐表
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealPart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealPart::getSetmealId, setmealDto.getId());
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新setmeal表基本信息
        this.updateById(setmealDto);

        //更新setmeal_dish表信息delete操作
        LambdaQueryWrapper<SetmealPart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealPart::getSetmealId, setmealDto.getId());
        setmealPartService.remove(queryWrapper);

        //更新setmeal_dish表信息insert操作
        List<SetmealPart> SetmealDishes = setmealDto.getSetmealParts();

        SetmealDishes = SetmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealPartService.saveBatch(SetmealDishes);
    }
}
