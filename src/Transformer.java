import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.ProtectionDomain;
import java.security.MessageDigest;
import java.util.Base64;

public class Transformer implements ClassFileTransformer {
    
    private Object f1C;
    private String f2B;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.equals("sun/management/VMManagementImpl")) {
            System.out.println("Applying Patch #1");
            classfileBuffer = Base64.getDecoder().decode("yv66vgAA...");
        } else if (className.equals("common/AuthCrypto")) {
            System.out.println("Applying Patch #2");
            modify();
            // The Base64 encoded class bytes containing the modified byte code for `AuthCrypto`
            // This is just a placeholder. Actual byte code modification is needed here.
            classfileBuffer = Base64.getDecoder().decode("yv66vgAAADQAp...");
        }
        return classfileBuffer;
    }

    private void modify() {
        try {
            byte[] md5 = MD5(readAll(getClass().getClassLoader().getResourceAsStream("resources/authkey.pub")));
            byte[] readAll = readAll(getClass().getClassLoader().getResourceAsStream("resources/我们自己的公钥key文件.pub"));
            if (!"8bb4df00c120881a1945a43e2bb2379e".equals(toHex(md5))) {
                printError("Invalid authorization file");
                System.exit(0);
            }
            this.f1C = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(readAll));
        } catch (Exception e) {
            this.f2B = "Could not deserialize authpub.key";
            logException("authpub.key deserialization", e);
        }
    }

    private byte[] readAll(java.io.InputStream is) throws java.io.IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private byte[] MD5(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(data);
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void printError(String message) {
        System.err.println(message);
    }

    private void logException(String context, Exception e) {
        System.err.println(context);
        e.printStackTrace(System.err);
    }
}
