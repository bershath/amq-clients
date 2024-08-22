package org.jboss.labs.amq.jms;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

public class SSLCheck {
    public static void main(String[] args) {
        try {
            String[] sslActiveProtocols = SSLContext.getDefault().createSSLEngine().getEnabledProtocols();
            for(int i = 0; i < sslActiveProtocols.length; i++){
                System.out.println(sslActiveProtocols[i]);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
