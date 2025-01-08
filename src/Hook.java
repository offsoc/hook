import java.lang.instrument.Instrumentation;

public class Hook {
    public static void premain(String str, Instrumentation instrumentation) {
        System.out.println("Hook starting...");
        instrumentation.addTransformer(new Transformer());
    }
}
