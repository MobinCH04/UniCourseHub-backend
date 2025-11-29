package com.mch.unicoursehub.utils.pagination;

import java.util.List;

/**
 * Utility class for handling pagination of data.
 */
public final class PaginationUtil {

    /**
     * Creates a paginated view of a list based on the provided page number and size.
     *
     * @param <T>  the type of the elements in the list
     * @param list the full list of items to paginate
     * @param page the page number to retrieve (1-based index)
     * @param size the number of items per page
     * @return a {@link Pagination} object containing the paginated data
     * @throws IndexOutOfBoundsException if the requested page exceeds the list size
     */
    public static <T> Pagination<T> pagination(List<T> list, int page, int size) {
        int start = (page - 1) * size;
        int end = page * size;

        if (size <= -1 && page <= -1) {
            return pagination(list, page, size, list.size());
        }

        if (list.size() < start)
            throw new IndexOutOfBoundsException("size over of length");

        if (list.size() <= end)
            end = list.size();

        return new Pagination<T>(list.subList(start, end), list.size(), size, page);
    }

    /**
     * Creates a paginated view of a list with a specified total size.
     *
     * @param <T>   the type of the elements in the list
     * @param list  the full list of items to paginate
     * @param page  the page number to retrieve (1-based index)
     * @param size  the number of items per page
     * @param total the total size of all data (useful for cases where the list is pre-filtered)
     * @return a {@link Pagination} object containing the paginated data
     */
    public static <T> Pagination<T> pagination(List<T> list, int page, int size, int total) {
        return new Pagination<T>(list, total, size, page);
    }

}
