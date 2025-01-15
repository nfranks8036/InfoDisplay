package net.cybercake.display.utils;

import java.io.Serializable;

@SuppressWarnings({"unused"})
public class Pair<A, B> implements Serializable {

    private A item1;
    private B item2;

    public Pair() {
        this.item1 = null;
        this.item2 = null;
    }

    public void setPair(A item1, B item2) { setFirstItem(item1); setSecondItem(item2); }
    public void setFirstItem(A item1) { this.item1 = item1; }
    public void setSecondItem(B item2) { this.item2 = item2; }

    public A getFirstItem() { return this.item1; }
    public B getSecondItem() { return this.item2; }

    public boolean isFirstItemSet() { return this.item1 != null; }
    public boolean isSecondItemSet() { return this.item2 != null; }

    public boolean areSameType() {
        if(this.item1 == null) throw new IllegalArgumentException("item1 cannot be null");
        if(this.item2 == null) throw new IllegalArgumentException("item2 cannot be null");
        return this.item1.getClass() == this.item2.getClass();
    }

    @Override
    public String toString() {
        if(this.item1 == null) throw new IllegalArgumentException("item1 cannot be null");
        if(this.item2 == null) throw new IllegalArgumentException("item2 cannot be null");
        return this.getClass().getSimpleName() + "{" + item1.getClass().getCanonicalName() + " " + item1 + ", " + item2.getClass().getCanonicalName() + " " + item2 + "}";
    }

}