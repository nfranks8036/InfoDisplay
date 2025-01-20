package net.cybercake.display.args;

import net.cybercake.display.utils.Log;
import net.cybercake.display.utils.Pair;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings({"unused"})
public class ArgumentReader implements Serializable {

    private final String[] args;
    private Map<String, Argument> deserializedArgs = new HashMap<>();

    public ArgumentReader(String[] rawArguments) {
        this.args = rawArguments;
        matchToKeyValuePairs();
    }

    public String[] getArgs() { return this.args; }
    public Map<String, Argument> getDeserializedArgs() { return this.deserializedArgs; }

    public boolean isPresent(String key) { return this.deserializedArgs.get(key) != null; }

    public Argument getArg(String key) {
        return getArg(key, false);
    }

    public Argument getArg(String key, boolean required, String... aliases) {
        List<String> argAliases = new ArrayList<>(Collections.singleton(key));
        if(aliases != null && aliases.length > 0) argAliases.addAll(Arrays.stream(aliases).toList());
        for(String arg : argAliases)
            if(isPresent(arg))
                return this.deserializedArgs.get(arg);
        if(required) throw new RuntimeException("No argument supplied for '" + key + "', try adding it to your java args. It should look like 'java -jar <jar file> " + key + "=<your value>");
        return Argument.DEFAULT_ARGUMENT;
    }

    @Override
    public String toString() {
        return String.join(" ", this.args);
    }

    private void matchToKeyValuePairs() {
        Map<String, Argument> deserializedArgs = new HashMap<>();
        Pair<String, StringBuilder> combinedSingleString = new Pair<>();
        for(String arg : args) {
            if(combinedSingleString.isFirstItemSet()) {
                boolean terminate = false;
                combinedSingleString.getSecondItem().append(" ");
                if(arg.endsWith("\"")) {
                    arg = arg.substring(0, arg.length()-1);
                    terminate = true;
                }
                combinedSingleString.getSecondItem().append(arg);
                if(terminate) {
                    deserializedArgs.put(combinedSingleString.getFirstItem(), Argument.of(combinedSingleString.getSecondItem().toString()));
                    combinedSingleString.setPair(null, null);
                }
                continue;
            }

            if(arg.split("=").length == 1) throw new IllegalArgumentException("One or more argument(s) do not have a proper key-value pair (key=value)");
            String key = arg.split("=")[0];
            if(key.startsWith("--")) key = key.substring(2);
            if(key.startsWith("-")) key = key.substring(1);
            Object value = arg.split("=")[1];
            if(value instanceof String stringValue && stringValue.startsWith("\"")) {
                StringBuilder newStringBuilder = new StringBuilder();
                newStringBuilder.append(stringValue.substring(1));
                combinedSingleString.setPair(key, newStringBuilder);
                continue;
            }
            deserializedArgs.put(key, Argument.of(value));
        }

        Log.line("Found specified arguments: " + deserializedArgs.toString());
        this.deserializedArgs = deserializedArgs;
    }

}
