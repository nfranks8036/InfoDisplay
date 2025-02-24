package net.cybercake.display.status;

import net.cybercake.display.utils.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CommandIndicator extends Indicator {

    private final String text;
    private final String cmd;

    private Function<String, String> peek;

    public CommandIndicator(StatusIndicatorManager manager, String text, String cmd) {
        super(manager, text + ": -");
        this.text = text;
        this.cmd = cmd;
        this.peek = null;
    }

    @Override
    public void update() {
        try {
            Process process = Runtime.getRuntime().exec(this.getCommand());
            process.waitFor(5, TimeUnit.SECONDS);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            Log.debug("[CMD RESULT] (" + String.join(" ", getCommand()) + ") -> " + result);
            if (result == null) {
                throw new NullPointerException("No result given from command '" + String.join(" ", this.getCommand()) + "'");
            }

            if (result.contains("=")) {
                result = result.split("=")[1];
            }

            if (this.peek != null) {
                result = this.peek.apply(result);
            }

            this.newResult(result);

            process.destroy();
            reader.close();
        } catch (Exception exception) {
            if (exception.getMessage().contains("error=2") || exception.getClass() == NullPointerException.class) {
                this.newResult("???");
                return;
            }

            IllegalStateException e = new IllegalStateException("Unable to execute command '" + String.join(" ", this.getCommand()) + "'", exception);
            e.printStackTrace();

            this.errorResult();
        }
    }

    @Override
    protected void newResult(String result) {
        result = this.text + ": " + result;
        super.newResult(result);
    }

    public String[] getCommand() {
        return this.cmd.split(" ");
    }

    public void peek(Function<String, String> peek) {
        this.peek = peek;
    }

}
