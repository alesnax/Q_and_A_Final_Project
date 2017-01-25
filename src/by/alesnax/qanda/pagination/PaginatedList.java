package by.alesnax.qanda.pagination;

import java.util.List;

/**
 * Created by alesnax on 22.01.2017.
 */
public class PaginatedList<E> {

    private List<E> items;
    private int totalCount;
    private int itemStart;
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

    public boolean isFirstPage() {
        return itemStart == 0;
    }

    public boolean isLastPage() {
        return itemStart + items.size() >= totalCount;
    }

   public int getCurrentPage(){
       return (itemStart / itemsPerPage + 1);
   }


    public int getTotalPagesCount(){
        return (totalCount - 1)/itemsPerPage + 1;
    }


}
