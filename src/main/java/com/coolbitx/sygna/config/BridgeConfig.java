package com.coolbitx.sygna.config;

public class BridgeConfig {

    public static final String SYGNA_BRIDGE_CENTRAL_PUBKEY
            = "047b04ca933c0fccb7094af06bafb77e0fdd9264b45243cba0b72cd8f1bc8fc4e7454902d4bb6bad8ed4bc4dfae102858b6a7649e4febca0c5b266566aa4e59f12";
        public static final String SYGNA_BRIDGE_CENTRAL_PUBKEY_TEST
            = "04a6936f2bc43773cb4874980518b3f681c004464d167aebdc9e305e10d6fb6cdacb27a22812453e6c51ceabff5b1e2d2196d81a8d3e8e71e907948b01a7ea9ac8";
    
    public static final int HTTP_TIMEOUT = 60000;
    public static final int EXPIRE_DATE_MIN_OFFSET = 60 * 3;
    public static final String SYGNA_BRIDGE_API_URL = "https://api.sygna.io/";
    public static final String SYGNA_BRIDGE_API_TEST_URL = "https://test-api.sygna.io/";
}
