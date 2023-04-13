package com.catalpa.datasource.core.page;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catalpa.common.core.constants.IntegerConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjwu
 * 分页信息传输类
 */
@Getter
@Setter
@Schema(name = "DataTables", description = "分页信息 传输类")
public class DataTables implements Serializable {

    private final static long serialVersionUID = 1L;

    @Schema(description = "页码")
    private int pageNum = 1;

    @Schema(description = "每页条数")
    private int pageSize = 20;

    @Schema(description = "排序字段")
    private List<String> orderColumn = new ArrayList<>();

    /**
     * 创建起始分页（默认按修改时间降序排序）
     *
     * @return 分页数据
     */
    public Page createStartPage() {
        this.pageNum = this.pageNum <= 0 ? 1 : this.pageNum;
        if (this.pageSize == IntegerConstant.ZERO) {
            this.pageSize = IntegerConstant.ONE;
        }
        if (this.pageSize > IntegerConstant.SIX_HUNDRED) {
            this.pageSize = IntegerConstant.SIX_HUNDRED;
        }
        Page page = new Page<>(this.pageNum, this.pageSize);
        this.setPageOrder(page, true);
        return page;
    }

    /**
     * 创建起始分页（默认按修改时间降序排列）
     *
     * @param isModifyTimeTimeOrder 是否默认按修改时间降序排序
     * @return 分页数据
     */
    public Page createStartPage(boolean isModifyTimeTimeOrder) {
        this.pageNum = this.pageNum <= 0 ? 1 : this.pageNum;
        if (this.pageSize == IntegerConstant.ZERO) {
            this.pageSize = IntegerConstant.ONE;
        }
        if (this.pageSize > IntegerConstant.SIX_HUNDRED) {
            this.pageSize = IntegerConstant.SIX_HUNDRED;
        }
        Page page = new Page<>(this.pageNum, this.pageSize);
        this.setPageOrder(page, isModifyTimeTimeOrder);
        return page;
    }

    /**
     * 设置分页排序信息
     *
     * @param page 分页数据
     * @param isModifyTimeTimeOrder 是否默认按修改时间降序排序
     */
    private void setPageOrder(Page page, boolean isModifyTimeTimeOrder) {
        List<OrderItem> orderItemList = new ArrayList<>();
        if (this.orderColumn != null && this.orderColumn.size() > IntegerConstant.ZERO) {
            for(String orderStr : this.orderColumn) {
                OrderItem orderItem = new OrderItem();
                String[] split = orderStr.trim().split(" ");
                orderItem.setColumn(StrUtil.toUnderlineCase(split[IntegerConstant.ZERO]));
                if (split.length > IntegerConstant.ONE) {
                    orderItem.setAsc("asc".equalsIgnoreCase(split[IntegerConstant.ONE].replace("end", "")));
                } else {
                    orderItem.setAsc(true);
                }
                orderItemList.add(orderItem);
            }
        }
        if (isModifyTimeTimeOrder) {
            orderItemList.add(new OrderItem("modify_time", false));
        }
        page.setOrders(orderItemList);
    }
}
