package com.lx.barragereptile.util;

import java.util.List;

/**
 * 针对bootstrap table
 * rows:内容
 * total:总条数
 */
public class PageBean<T> {
    private static final long serialVersionUID = 1L;
    private int total;
    private List<T> rows;

    public PageBean() {
    }
    public PageBean(List<T> list, int total) {
        this.rows = list;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
