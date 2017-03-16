package by.alesnax.qanda.pagination;

import java.util.List;

/**
 * Class {@code PaginatedList<E>} is parametrised container of List of items, used for pagination and getting data from database.
 * Let's not show all list of items but limited list with known total number of elements. Help to count number of pages.
 * Class has fields of total number of pages, current first number of item and number of items per page.
 *
 * @author Aliaksandr Nakhankou
 */
public class PaginatedList<E> {

    /**
     * parametrised list of items stored in container
     */
    private List<E> items;

    /**
     * total number of items from known from SQL query
     */
    private int totalCount;

    /**
     * number of first item in list from SQL query
     */
    private int itemStart;

    /**
     * number of items per page, used as parameter while getting definite number of rows from SQL query
     * and showing it at page
     */
    private int itemsPerPage;

    public PaginatedList() {
    }

    public List<E> getItems() {
        return items;
    }

    public void setItems(List<E> items) {
        this.items = items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getItemStart() {
        return itemStart;
    }

    public void setItemStart(int itemStart) {
        this.itemStart = itemStart;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

   public int getCurrentPage(){
       return (itemStart / itemsPerPage + 1);
   }


    public int getTotalPagesCount(){
        return (totalCount - 1)/itemsPerPage + 1;
    }


}
