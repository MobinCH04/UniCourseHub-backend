package com.mch.unicoursehub.utils.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * A generic class for handling paginated data.
 *
 * @param <T> the type of the elements in the paginated data
 */
@AllArgsConstructor
@Data
public class Pagination<T> {
    /**
     * The list of data items for the current page.
     */
    private List<T> data;
    /**
     * The total size of the data available for pagination.
     */
    private int size;
    /**
     * The number of data items displayed per page.
     */
    @JsonProperty("dataPerPage")
    private int dataPerPage;

    /**
     * The current page number in the pagination.
     */
    @JsonProperty("pageNumber")
    private int pageNumber;

}
