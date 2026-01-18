package com.openlms.api.commons.apis;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalItems;
    private final int totalPages;
    private final boolean last;

    protected PageResponse(List<T> items, int page, int size, long totalItems, int totalPages, boolean last) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.last = last;
    } 

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public List<T> getItems() { 
        return items; 
    }

    public int getPage() { 
        return page; 
    }

    public int getSize() { 
        return size; 
    }

    public long getTotalItems() { 
        return totalItems; 
    }

    public int getTotalPages() { 
        return totalPages; 
    }

    public boolean isLast() { 
        return last; 
    }
}