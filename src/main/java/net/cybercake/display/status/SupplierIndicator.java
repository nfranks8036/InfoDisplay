package net.cybercake.display.status;

import java.util.function.Supplier;

public class SupplierIndicator extends Indicator {

    private final Supplier<String> supplier;

    public SupplierIndicator(StatusIndicatorManager manager, Supplier<String> supplier) {
        super(manager, "-");

        this.supplier = supplier;
    }

    @Override
    void update() {
        try {
            this.newResult(this.supplier.get());
        } catch (Exception exception) {
            IllegalStateException e = new IllegalStateException("Unable to execute supplier: " + exception, exception);
            e.printStackTrace();

            this.errorResult();
        }
    }
}
