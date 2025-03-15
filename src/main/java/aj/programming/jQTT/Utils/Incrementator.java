package aj.programming.jQTT.Utils;

public class Incrementator {
    private int value;

    public Incrementator() {
        this.value = 0;
    }

    public void increment() {
        this.value++;
    }

    public void decrement() {
        this.value--;
    }

    public int incrementAndGet() {
        this.value++;
        return this.value;
    }

    public int incrementOf(int value) {
        this.value += value;
        return this.value;
    }

    public int getAndIncrement() {
        int toReturn = this.value;
        this.value++;
        return toReturn;
    }


    public void clear() {
        this.value = 0;
    }


    public int getValue() {
        return this.value;
    }
}
