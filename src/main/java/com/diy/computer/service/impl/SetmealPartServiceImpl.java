package com.diy.computer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diy.computer.entity.SetmealPart;
import com.diy.computer.mapper.SetmealPartMapper;
import com.diy.computer.service.SetmealPartService;
import org.springframework.stereotype.Service;

@Service
public class SetmealPartServiceImpl extends ServiceImpl<SetmealPartMapper, SetmealPart> implements SetmealPartService {
}
