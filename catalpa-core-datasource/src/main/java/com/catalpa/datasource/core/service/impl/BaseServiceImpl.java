package com.catalpa.datasource.core.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.catalpa.common.core.constants.IntegerConstant;
import com.catalpa.common.core.exception.MapperFieldTransformException;
import com.catalpa.datasource.core.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zjwu
 * MyBatis-Plus服务实现类
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    @Autowired
    protected M baseMapper;

    @Override
    public IPage<T> searchFuzzy(Map<String, Object> map, Page page) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        Object selectProperties = null;
        Object excludeProperties = null;
        // 获取目标类
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class itemClass = (Class) pt.getActualTypeArguments()[IntegerConstant.ONE];
        // 剔除集合中的分页信息
        if (!map.isEmpty() && map.size() > IntegerConstant.ZERO) {
            selectProperties = map.get("selectProperties");
            excludeProperties = map.get("excludeProperties");
            map.remove("pageNum");
            map.remove("pageSize");
            map.remove("orderColumn");
            map.remove("total");
            map.remove("selectProperties");
            map.remove("excludeProperties");
        }
        if (selectProperties != null) {
            // 指定查询的列
            queryWrapper.select(selectProperties.toString().split(","));
        }
        if (excludeProperties != null) {
            // 指定排除的列
            List<String> split = Arrays.asList(excludeProperties.toString().split(","));
            queryWrapper.select(itemClass, item -> !split.contains(item.getProperty()));
        }
        if (map.isEmpty() || map.size() < IntegerConstant.ONE) {
            return this.baseMapper.selectPage(page, queryWrapper);
        }
        String commaSplit = ",";
        String underLineSplit = "_";
        // 将原集合分组
        Map<String, Map<String, Object>> groupMap = this.getGroupMap(map, underLineSplit, commaSplit);
        // 组织查询信息
        groupMap.forEach((groupName, itemMap) -> {
            try {
                queryWrapper.and(item -> {
                    this.organizeQuery(itemMap, commaSplit, underLineSplit, item, itemClass);
                });
            } catch (Exception error) {
                throw new MapperFieldTransformException(error.getMessage());
            }
        });
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 组织查询信息
     *
     * @param itemMap        集合组
     * @param commaSplit     分隔符
     * @param underLineSplit 分隔线
     * @param queryWrapper   条件构造器
     * @param itemClass      目标类
     */
    private void organizeQuery(Map<String, Object> itemMap, String commaSplit, String underLineSplit, QueryWrapper<T> queryWrapper, Class<T> itemClass) {
        Iterator iterator = itemMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = StrUtil.toUnderlineCase((String) entry.getKey());
            String value = (String) entry.getValue();
            String pre = "eq";
            // 若存在分隔符，则将查询条件与查询值拆分
            if (value.contains(commaSplit)) {
                pre = value.substring(0, value.indexOf(commaSplit)).toLowerCase();
                value = value.substring(value.indexOf(commaSplit) + IntegerConstant.ONE);
            }
            // 根据查询值是否为空选择查询条件
            if (!StrUtil.isEmptyOrUndefined(value) && !"undefined".equalsIgnoreCase(value)) {
                if (pre.startsWith("groupor_")) {
                    // 嵌套类型为or的分组
                    String[] groupPre = pre.split(underLineSplit);
                    String preName = groupPre[IntegerConstant.TWO];
                    if (groupPre.length < IntegerConstant.THREE) {
                        preName = groupPre.length == IntegerConstant.ONE ? "eq" : groupPre[IntegerConstant.ONE];
                    }
                    this.setWrapper(queryWrapper, key, value, preName, commaSplit, itemClass);
                    if (iterator.hasNext()) {
                        queryWrapper.or();
                    }
                } else {
                    // 嵌套类型为and
                    this.setWrapper(queryWrapper, key, value, pre, commaSplit, itemClass);
                }
            } else {
                // 字段是否允许为空
                if ("isnotnull".equalsIgnoreCase(pre)) {
                    queryWrapper.isNotNull(key);
                } else if ("isnull".equalsIgnoreCase(pre)) {
                    queryWrapper.isNull(key);
                }
            }
        }
    }

    /**
     * 分组集合
     *
     * @param map            原集合
     * @param underLineSplit 分隔线
     * @param commaSplit     分隔符
     * @return 分组后集合
     */
    private Map<String, Map<String, Object>> getGroupMap(Map<String, Object> map, String underLineSplit, String commaSplit) {
        Map<String, Map<String, Object>> groupMap = new HashMap<>(IntegerConstant.ONE);
        map.forEach((key, val) -> {
            String value = ((String) val).split(commaSplit)[IntegerConstant.ZERO];
            String groupName = "none";
            if (value.startsWith("groupor_")) {
                String[] groupPre = value.split(underLineSplit);
                if (groupPre.length < IntegerConstant.THREE) {
                    groupName = "default";
                } else {
                    groupName = groupPre[IntegerConstant.ONE].toUpperCase();
                }
            }
            Map<String, Object> itemMap;
            if (groupMap.containsKey(groupName)) {
                itemMap = groupMap.get(groupName);
            } else {
                itemMap = new HashMap<>(IntegerConstant.ONE);
            }
            itemMap.put(key, val);
            groupMap.put(groupName, itemMap);
        });
        return groupMap;
    }

    /**
     * 设置查询信息
     *
     * @param queryWrapper 条件构造器
     * @param name         字段名
     * @param value        查询值
     * @param pre          查询条件
     * @param commaSplit   分隔符
     * @param itemClass    目标类
     */
    private void setWrapper(QueryWrapper<T> queryWrapper, String name, String value, String pre, String commaSplit, Class<T> itemClass) {
        if (!StrUtil.isEmptyOrUndefined(value) && !"eq".equalsIgnoreCase(pre)) {
            if ("like".equalsIgnoreCase(pre)) {
                // 全匹配模糊查询
                queryWrapper.like(name, value);
            } else if ("prelike".equalsIgnoreCase(pre)) {
                // 左匹配模糊查询
                queryWrapper.likeLeft(name, value);
            } else if ("endlike".equalsIgnoreCase(pre)) {
                // 右匹配模糊查询
                queryWrapper.likeRight(name, value);
            } else if ("egt".equalsIgnoreCase(pre)) {
                // 大于等于
                queryWrapper.ge(name, value);
            } else if ("elt".equalsIgnoreCase(pre)) {
                // 小于等于
                queryWrapper.le(name, value);
            } else if ("gt".equalsIgnoreCase(pre)) {
                // 大于
                queryWrapper.gt(name, value);
            } else if ("lt".equalsIgnoreCase(pre)) {
                // 小于
                queryWrapper.lt(name, value);
            } else if ("neq".equalsIgnoreCase(pre)) {
                // 不等于
                queryWrapper.ne(name, value);
            } else if ("in".equalsIgnoreCase(pre)) {
                // 范围查询
                queryWrapper.in(name, Arrays.asList(value.split(commaSplit)));
            } else if ("notin".equalsIgnoreCase(pre)) {
                // 不在范围查询
                queryWrapper.notIn(name, Arrays.asList(value.split(commaSplit)));
            } else {
                String[] split;
                if ("betweenand".equalsIgnoreCase(pre)) {
                    // 开区间查询
                    split = value.split(commaSplit);
                    queryWrapper.gt(name, this.transDataType(name, split[IntegerConstant.ZERO], itemClass));
                    queryWrapper.lt(name, this.transDataType(name, split[IntegerConstant.ONE], itemClass));
                } else if ("betweenandq".equalsIgnoreCase(pre)) {
                    // 闭区间查询
                    split = value.split(commaSplit);
                    queryWrapper.between(name, this.transDataType(name, split[IntegerConstant.ZERO], itemClass), this.transDataType(name, split[IntegerConstant.ONE], itemClass));
                } else {
                    if ("endLikeOr".equalsIgnoreCase(pre)) {
                        // 批量右匹配
                        String[] strings = value.split(commaSplit);
                        boolean isFirst = true;
                        for (String key : strings) {
                            if (isFirst) {
                                isFirst = false;
                                queryWrapper.likeRight(name, key);
                                continue;
                            }
                            queryWrapper.or(item -> {
                                item.likeRight(name, key);
                            });
                        }
                    } else if ("preLikeOr".equalsIgnoreCase(pre)) {
                        // 批量左匹配
                        String[] strings = value.split(commaSplit);
                        boolean isFirst = true;
                        for (String key : strings) {
                            if (isFirst) {
                                isFirst = false;
                                queryWrapper.likeLeft(name, key);
                                continue;
                            }
                            queryWrapper.or(item -> {
                                item.likeLeft(name, key);
                            });
                        }
                    }
                }
            }
        } else {
            // 嵌套类型为or或and的equal方法
            queryWrapper.eq(name, this.transDataType(name, value, itemClass));
        }
    }

    /**
     * 基于实体类的字段值类型转换
     *
     * @param name      字段名
     * @param value     字段值
     * @param itemClass 目标类
     * @return 对应实体类字段类型的字段值
     */
    private Object transDataType(String name, String value, Class<T> itemClass) {
        try {
            String dataType = itemClass.getDeclaredField(name).getType().getName();
            if ("java.lang.Integer".equalsIgnoreCase(dataType)) {
                return Integer.parseInt(value);
            }
            if ("java.math.BigDecimal".equalsIgnoreCase(dataType)) {
                return new BigDecimal(value);
            }
            if ("java.lang.Long".equalsIgnoreCase(dataType)) {
                return Long.parseLong(value);
            }
            if ("java.util.Date".equalsIgnoreCase(dataType)) {
                return DateUtil.parse(value, "yyyy-MM-dd HH:mm:ss");
            }
        } catch (NoSuchFieldException error) {
            error.printStackTrace();
        }
        return value;
    }
}
