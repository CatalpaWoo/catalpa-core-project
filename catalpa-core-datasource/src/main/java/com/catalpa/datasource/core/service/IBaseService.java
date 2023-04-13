package com.catalpa.datasource.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author zjwu
 * MyBatis-Plus服务类
 */
public interface IBaseService<T> extends IService<T> {

    /**
     * 模糊查询
     *
     * @param map  查询参数
     * @param page 分页信息
     * @return 查询结果列表
     */
    IPage<T> searchFuzzy(Map<String, Object> map, Page page);
}
