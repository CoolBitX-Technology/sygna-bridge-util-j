# Sygna Bridge Util Java (sygna-bridge-util-j)

This sygna-bridge-util-j library is a Java version of Sygna Bridge SDK to help you build servers/servies within Sygna Bridge Ecosystem. For more detail information, please see [Sygna Bridge](https://www.sygna.io/).

## Build Jar

You can build Jar from source code or download jar files in artifacts directly from the [latest pipeline](https://gitlab.com/coolbitx/sygna/bridge/sygna-bridge-util-j/pipelines)

```shell
mvn clean package
```

## Package

```java
import com.coolbitx.sygna.bridge.*;
```

## Crypto

Dealing with encrypting, decrypting, signing and verifying in Sygna Bridge.

### ECIES Encrypting an Decrypting

During the communication of VASPs, there are some private information that must be encrypted. We use ECIES(Elliptic Curve Integrated Encryption Scheme) to securely encrypt these private data so that they can only be accessed by the recipient.

```java
String sensitiveData =
"{" +
"    \"originator\": {" +
"        \"name\": \"Antoine Griezmann\"," +
"        \"date_of_birth\":\"1991-03-21\"" +
"    }," +
"    \"beneficiary\":{" +
"        \"name\": \"Leo Messi\"" +
"    }" +
"}";
JsonObject sensitiveDataObj = new Gson().fromJson(sensitiveData, JsonObject.class);
String privateInfo = Crypto.encryptPrivateObj(sensitiveDataObj, PUBLIC_KEY);
JsonObject decryptedPrivateInfo = Crypto.decryptPrivateObj(privateInfo, PRIVATE_KEY);

```

### Sign and Verify

In Sygna Bridge, we use secp256k1 ECDSA over sha256 of utf-8 json string to create signature on every API call. Since you need to provide the identical utf-8 string during verfication, the order of key-value pair you put into the object is important.

The following example is the snippet of originator's signing process of `premissionRequest` API call. If you put the key `transaction` before `private_info` in the object, the verification will fail in the central server.

```java
String transaction =
"{" +
"    originator_vasp_code:\"10000\"," +
"    originator_addrs:[\"3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D\"]," +
"    beneficiary_vasp_code:\"10001\"," +
"    beneficiary_addrs:[\"3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf\"]," +
"    transaction_currency:\"0x80000000\"," +
"    amount: 0.973" +
"}";

String dataDate = "2019-07-29T06:28:00Z";

// using signObject to get a valid signed object (with signature attached)

JsonObject obj = new JsonObject();
obj.addProperty("private_info", privateInfo);
obj.addProperty("transaction", transaction);
obj.addProperty("data_dt", dataDate);

Crypto.signObject(obj, originatorPrivateKey);

boolean valid = Crypto.verifyObject(obj, originatorPublicKey);

// or you can use the method that's built for `transfer` request:
JsonObject signedObj = Crypto.signPermissionRequest(privateInfo, transaction, dataDate, originatorPrivateKey)
valid = Crypto.verifyObject(signedObj, originatorPublicKey);

```

We provide different methods like `signPermissionRequest`, `signCallback()` to sign different objects(or parameters) we specified in our api doc. You can also find more examples in the following section.

## API

API calls to communicate with Sygna Bridge server.

We use **baisc auth** with all the API calls. To simplify the process, we provide a API class to deal with authentication and post/ get request format.

```java
String sbServer = "https://api.sygna.io/sb/";
API API_UTIL = new API("api-key", sbServer);
```

After you create the `API` instance, you can use it to make any API call to communicate with Sygna Bridge central server.

### Get VASP Information

```java
// Get List of VASPs associated with public keys.
boolean verify = true // set verify to true to verify the signature attached with api response automatically.
ArrayList<VaspDetail> vasps = API_UTIL.getVASPList(verify);

// Or call use getVASPPublicKey() to directly get public key for a specific VASP.
String publicKey = API_UTIL.getVASPPublicKey("10298", verify);
```

### For Originator

There are two API calls from **transaction originator** to Sygna Bridge Server defined in the protocol, which are `postPermissionRequest` and `postTransactionId`.

The full logic of originator would be like the following:

```java
// originator.js

JsonObject privateSenderInfo = new JsonObject();
JsonObject originator = new JsonObject();
originator.addProperty("name", "Antoine Griezmann");
originator.addProperty("date_of_birth", "1991-03-21");

JsonObject beneficiary = new JsonObject();
beneficiary.addProperty("name", "Leo Messi");

privateSenderInfo.add("originator", originator);
privateSenderInfo.add("beneficiary", beneficiary);

String recipientPublicKey = API_UTIL.getVASPPublicKey("10298");
String private_info = Crypto.encryptPrivateObj(privateSenderInfo, recipientPublicKey);

JsonObject transaction = new JsonObject();
JsonArray originator_addrs = new JsonArray();
originator_addrs.add("3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");

JsonArray beneficiary_addrs = new JsonArray();
beneficiary_addrs.add("3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");

transaction.addProperty("originator_vasp_code", "VASPUSNY1");
transaction.add("originator_addrs", originator_addrs);
transaction.addProperty("beneficiary_vasp_code", "VASPUSNY2");
transaction.add("beneficiary_addrs", beneficiary_addrs);
transaction.addProperty("transaction_currency", "0x80000000");
transaction.addProperty("amount", 0.973);

String data_dt = "2019-07-29T07:29:80Z"

JsonObject transferObj = Crypto.signPermissionRequest(private_info, transaction, data_dt, sender_privKey);

String callback_url = "https://81f7d956.ngrok.io/api/v1/originator/transaction/premission";
JsonObject callbackObj = Crypto.signCallBack(callback_url, sender_privKey);

JsonObject obj = API_UTIL.postPermissionRequest(tansferObj, callbackObj)
String transfer_id = obj.get("transfer_id").getAsString();

// Boradcast your transaction to blockchain after got and api reponse at your api server.
String txid = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";

// Inform Sygna Bridge that a specific transfer is successfully broadcasted to the blockchain.

JsonObject sendTxIdObj = Crypto.signTxId(transfer_id, txid, sender_privKey);
JsonObject result = API_UTIL.postTransactionId(sendTxIdObj);

```

### For Beneficiary

There is only one api for Beneficiary VASP to call, which is `postPermission`. After the beneficiary server confirm thet legitemacy of a transfer request, they will sign `{ transfer_id, permission_status }` using `signPermission()` function, and send the result with signature to Sygna Bridge Central Server.

```java
String permission_status = "ACCEPT"; // or "REJECT"
JsonObject permissionObj = Crypto.signPermission(transfer_id, permission_status, beneficiary_privKey);
String finalresult = API_UTIL.postPermission(permissionObj);

```

If you're trying to implement the beneficiary server on your own, we strongly recommand you to take a look at our [Nodejs sample](https://github.com/CoolBitX-Technology/sygna-bridge-sample) to get a big picture of how it should behave in the ecosystem.
