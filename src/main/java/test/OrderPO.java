package test;

import java.io.Serializable;

public class OrderPO implements Serializable {

    private Integer itemCount;

    @Override
    public String toString() {
        return "OrderPO{" +
                "itemCount=" + itemCount +
                ", valid=" + valid +
                '}';
    }

    private boolean valid;

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
