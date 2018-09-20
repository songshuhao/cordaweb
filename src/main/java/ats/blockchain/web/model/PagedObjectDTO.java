package ats.blockchain.web.model;


import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Administrator on 2016/1/26.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class PagedObjectDTO implements Serializable {
    List<?> rows;
    Long total;

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
