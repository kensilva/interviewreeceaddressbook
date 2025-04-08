package com.interview.reece.addressbook.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public abstract class PagedResult<T> {

    /**
     * The page number
     */
    private int page;

    /**
     * current size of this page
     */
    private int currentPageSize;

    /**
     * total count of the entire result without paging
     */
    private long totalSize;

    /**
     * The number of pages
     */
    private int totalPages;

    /**
     * the requested page size
     */
    private int requestedPageSize;

    /**
     * The result
     */
    private List<T> results;

    public PagedResult(final int page, final int pageSize){
        this.page = page;
        this.requestedPageSize = pageSize;
    }

    public PagedResult(final int page, final int pageSize, final Page pageResult){
        this.page = page;
        this.requestedPageSize = pageSize;
        init(pageResult);
    }

    public void init(final Page page){
        setTotalPages(page.getTotalPages());
        setTotalSize(page.getTotalElements());
        setCurrentPageSize(page.getNumberOfElements());
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getCurrentPageSize() {
        return currentPageSize;
    }

    public void setCurrentPageSize(int currentPageSize) {
        this.currentPageSize = currentPageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getRequestedPageSize() {
        return requestedPageSize;
    }

    public void setRequestedPageSize(int requestedPageSize) {
        this.requestedPageSize = requestedPageSize;
    }
}
