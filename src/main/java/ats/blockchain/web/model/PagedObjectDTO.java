package ats.blockchain.web.model;


import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by Administrator on 2016/1/26.
 */
@JsonInclude(Include.NON_NULL)
public class PagedObjectDTO implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4688033208212982551L;
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
