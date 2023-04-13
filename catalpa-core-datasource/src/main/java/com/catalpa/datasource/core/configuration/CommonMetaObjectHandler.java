package com.catalpa.datasource.core.configuration;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author zjwu
 * MyBatis-Plus自动填充
 */
@Slf4j
@Component
public class CommonMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 主键ID
        this.strictInsertFill(metaObject, "guid", String.class, IdUtil.simpleUUID());
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 创建用户
        this.strictInsertFill(metaObject, "creator", String.class, "管理员");
        // 更新时间
        this.strictInsertFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now());
        // 更新用户
        this.strictInsertFill(metaObject, "modifier", String.class, "管理员");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间
        this.strictInsertFill(metaObject, "modifyTime", LocalDateTime.class, LocalDateTime.now());
        // 更新用户
        this.strictInsertFill(metaObject, "modifier", String.class, "管理员");
    }
}
