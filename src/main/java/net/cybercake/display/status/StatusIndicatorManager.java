package net.cybercake.display.status;

import net.cybercake.display.args.ArgumentReader;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.cybercake.display.Application.WINDOW_WIDTH;

@SuppressWarnings({"LoopConditionNotUpdatedInsideLoop", "InfiniteLoopStatement", "BusyWait"})
public class StatusIndicatorManager {

    final ArgumentReader args;
    private final JPanel panel;

    private final List<Indicator> indicators;

    public StatusIndicatorManager(ArgumentReader args) {
        this.args = args;

        this.panel = new JPanel();
        this.panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.panel.setBackground(Color.BLACK);
        this.panel.setPreferredSize(new Dimension(WINDOW_WIDTH, 32));
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.X_AXIS));
        this.panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.indicators = new ArrayList<>();

        SwingUtilities.invokeLater(() -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    while (true) {
                        for (Indicator indicator : StatusIndicatorManager.this.indicators) {
                            indicator.update();
                            Thread.sleep(500);
                        }
                        Thread.sleep(5000);
                    }
                }
            };
            worker.execute();
        });
    }

    public CommandIndicator addFromCmd(String text, String cmd) {
        return (CommandIndicator) add(new CommandIndicator(this, text, cmd).executeAndReturn());
    }

    public SupplierIndicator addFromSupp(Supplier<String> supplier) {
        return (SupplierIndicator) add(new SupplierIndicator(this, supplier).executeAndReturn());
    }

    private Indicator add(Indicator indicator) {
        this.indicators.add(indicator);
        indicator.implement(this.panel);
        this.panel.add(Box.createRigidArea(new Dimension(20, 0))); // space in between
        return indicator;
    }

    public void implement(JFrame frame) {
        frame.add(this.panel, BorderLayout.SOUTH);
    }

}
