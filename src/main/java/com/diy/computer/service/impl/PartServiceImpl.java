package com.diy.computer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diy.computer.dto.PartDto;
import com.diy.computer.entity.PartAfter;
import com.diy.computer.service.PartAfterService;
import com.diy.computer.entity.Part;
import com.diy.computer.mapper.PartMapper;
import com.diy.computer.service.PartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartServiceImpl extends ServiceImpl<PartMapper, Part> implements PartService {
    @Autowired
    private PartAfterService partafterService;
    /**
     * 新增零件以及相关服务
     * @param partDto
     */
    @Override
    @Transactional
    public void saveWithAfter(PartDto partDto) {
//        保存零件,插入零件表
        this.save(partDto);
//        保存服务，插入服务表
        Long partId=partDto.getId();
        List<PartAfter> afters=partDto.getAfters();
        for (PartAfter after :
                afters) {
            after.setPartId(partId);
        }
        partafterService.saveBatch(afters);
    }

    /**
     * 获取零件及其服务信息
     * @param id
     * @return
     */
    @Override
    public PartDto getByIdWithAfter(Long id) {
//        查零件基本信息
        Part part = this.getById(id);

//        查询服务
        LambdaQueryWrapper<PartAfter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartAfter::getPartId, part.getId());
//        获取服务信息
        List<PartAfter> afters = partafterService.list(queryWrapper);
//        存入对象
        PartDto partDto=new PartDto();
        BeanUtils.copyProperties(part, partDto);
        partDto.setAfters(afters);
        return partDto;
    }

    /**
     * 更新零件及售后
     * @param partDto
     */
    @Override
    @Transactional
    public void updateWithAfter(PartDto partDto) {
//        更新零件表
        this.updateById(partDto);

        LambdaQueryWrapper<PartAfter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartAfter::getPartId, partDto.getId());
//        先删除
        partafterService.remove(queryWrapper);
//        再更新
        Long partId=partDto.getId();
        List<PartAfter> afters=partDto.getAfters();
        for (PartAfter after :
                afters) {
            after.setPartId(partId);
        }
        partafterService.saveBatch(afters);
    }
}
