package info.pevy.poi.excel.page;

import java.util.Collections;
import java.util.List;

public class Pagination<T> {
    /**
     * 原集合
     */
    private List<T> data;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总数据条数
     */
    private int totalCount;

    public Pagination(List<T> data, int pageSize) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("data must be not empty!");
        }
        this.data = data;
        this.pageSize = pageSize;
        this.totalCount = data.size();
        this.totalPage = (totalCount + pageSize - 1) / pageSize;
    }

    /**
     * 得到分页后的数据
     */
    public List<T> getPagedList(int currentPage) {
        int fromIndex = (currentPage - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();//空数组
        }
        if (fromIndex < 0) {
            return Collections.emptyList();//空数组
        }
        int toIndex = currentPage * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }
}