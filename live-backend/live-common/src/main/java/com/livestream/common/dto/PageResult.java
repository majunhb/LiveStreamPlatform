package com.livestream.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 数据列表 */
    private List<T> records;
    
    /** 总记录数 */
    private Long total;
    
    /** 当前页码 */
    private Integer pageNum;
    
    /** 每页大小 */
    private Integer pageSize;
    
    /** 总页数 */
    private Integer totalPages;
    
    /** 是否有上一页 */
    private Boolean hasPrevious;
    
    /** 是否有下一页 */
    private Boolean hasNext;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        
        int totalPages = (int) Math.ceil((double) total / pageSize);
        result.setTotalPages(totalPages);
        result.setHasPrevious(pageNum > 1);
        result.setHasNext(pageNum < totalPages);
        
        return result;
    }
}
