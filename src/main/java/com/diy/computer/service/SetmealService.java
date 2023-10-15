package com.diy.computer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diy.computer.dto.SetmealDto;
import com.diy.computer.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //    新增套餐
    public void saveWithPart(SetmealDto setmealDto);

    //    删除套餐
    public void removeWithPart(List<Long> ids);

    //    更新套餐是否起售信息
    public void updateWithPartToStatus(List<Long> ids,int status);

    //    修改套餐信息
    public SetmealDto getByIdWithPart(Long id);

    //     更新套餐信息
    public void updateWithPart(SetmealDto setmealDto);

    void updateWithDish(SetmealDto setmealDto);
}
