package net.draycia.cooldowns;

public class MutableLong {

    private long value;

    public MutableLong() {
        this.value = 0;
    }

    public MutableLong(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long incrementAndGet() {
        return ++value;
    }

    public long decrementAndGet() {
        return --value;
    }

    public void increment() {
        value++;
    }

    public void decrement() {
        value--;
    }
}
