package com.diy.computer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diy.computer.entity.Category;


public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
