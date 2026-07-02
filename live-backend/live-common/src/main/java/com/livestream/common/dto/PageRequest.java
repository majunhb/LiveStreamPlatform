package com.livestream.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分页请求参数
 */
@Data
@NoArgsConstructor
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 当前页码（从1开始） */
    private Integer pageNum = 1;
    
    /** 每页大小 */
    private Integer pageSize = 10;
    
    /** 排序字段 */
    private String orderBy;
    
    /** 排序方向（asc/desc） */
    private String orderDirection = "desc";

    public PageRequest(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum != null ? pageNum : 1;
        this.pageSize = pageSize != null ? pageSize : 10;
    }
}
