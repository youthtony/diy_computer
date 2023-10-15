package com.diy.computer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diy.computer.dto.PartDto;
import com.diy.computer.entity.Part;

public interface PartService extends IService<Part> {

//    新增零件，以及相关服务
    public void saveWithAfter(PartDto partDto);

    //    获取零件信息及其服务
    public PartDto getByIdWithAfter(Long id);

//    更新零件及其服务
    public void updateWithAfter(PartDto partDto);
}
