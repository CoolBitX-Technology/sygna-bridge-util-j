
# Sygna Bridge Util Java (sygna-bridge-util-j)

This sygna-bridge-util-j library is a Java version of Sygna Bridge SDK to help you build servers/services within Sygna Bridge Ecosystem. For more detail information, please see [Sygna Bridge](https://www.sygna.io/).

## Build Jar

You can build Jar from source code

```shell
mvn clean package
```

## Import Bridge Util

You also can import bridge util from [maven](https://mvnrepository.com/artifact/io.sygna/sygna-bridge-util-j)

## Package

```java
import com.coolbitx.sygna.bridge.*;
```

## Crypto

Dealing with encrypting, decrypting, signing and verifying in Sygna Bridge.

### ECIES Encrypting an Decrypting

During the communication of VASPs, there are some private information that must be encrypted. We use ECIES(Elliptic Curve Integrated Encryption Scheme) to securely encrypt these private data so that they can only be accessed by the recipient.

We're using [IVMS101 (interVASP Messaging Standard)](https://intervasp.org/) as our private information format.

We also provide [IVMS101 Java Utility](https://github.com/CoolBitX-Technology/sygna-bridge-ivms-utils/tree/master/java) to construct data payload.

```java
String  sensitiveData = "{" +
"  \"originator\": {" +
"    \"originator_persons\": [" +
"      {" +
"        \"natural_person\": {" +
"          \"name\": {" +
"            \"name_identifiers\": [" +
"              {" +
"                \"primary_identifier\": \"Wu Xinli\"," +
"                \"name_identifier_type\": \"LEGL\"" +
"              }" +
"            ]" +
"          }," +
"          \"national_identification\": {" +
"            \"national_identifier\": \"446005\"," +
"            \"national_identifier_type\": \"RAID\"," +
"            \"registration_authority\": \"RA000553\"" +
"          }," +
"          \"country_of_residence\": \"TZ\"" +
"        }" +
"      }" +
"    ]," +
"    \"account_numbers\": [" +
"      \"3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D\"" +
"    ]" +
"  }," +
"  \"beneficiary\": {" +
"    \"beneficiary_persons\": [" +
"      {" +
"        \"legal_person\": {" +
"          \"name\": {" +
"            \"name_identifiers\": [" +
"              {" +
"                \"legal_person_name\": \"ABC Limited\"," +
"                \"legal_person_name_identifier_type\": \"LEGL\"" +
"              }" +
"            ]" +
"          }" +
"        }" +
"      }" +
"    ]," +
"    \"account_numbers\": [" +
"      \"3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf\"" +
"    ]" +
"  }" +
"}";

JsonObject  sensitiveDataObj = new  Gson().fromJson(sensitiveData, JsonObject.class);
String  privateInfo = Crypto.encryptPrivateObj(sensitiveDataObj, PUBLIC_KEY);
JsonObject  decryptedPrivateInfo = Crypto.decryptPrivateObj(privateInfo, PRIVATE_KEY);

```

### Sign and Verify

In Sygna Bridge, we use secp256k1 ECDSA over sha256 of utf-8 json string to create signature on every API call. Since you need to provide the identical utf-8 string during verification, the order of key-value pair you put into the object is important.

The following example is the snippet of originator's signing process of `permissionRequest` API call. If you put the key `transaction` before `private_info` in the object, the verification will fail in the central server.

```java
JsonObject originatorAddr = new JsonObject();
originatorAddr.addProperty(Field.ADDRESS, "3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");

JsonArray originatorAddrs = new JsonArray();
originatorAddrs.add(originatorAddr);

JsonObject originatorVASP = new JsonObject();
originatorVASP.addProperty(Field.VASP_CODE, "VASPUSNY1");
originatorVASP.add(Field.ADDRS, originatorAddrs);

JsonObject beneficiaryAddr = new JsonObject();
beneficiaryAddr.addProperty(Field.ADDRESS, "3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");

JsonArray beneficiaryAddrs = new JsonArray();
beneficiaryAddrs.add(beneficiaryAddr);

JsonObject beneficiaryVASP = new JsonObject();
beneficiaryVASP.addProperty(Field.VASP_CODE, "VASPUSNY2");
beneficiaryVASP.add(Field.ADDRS, beneficiaryAddrs);

JsonObject  transaction = new  JsonObject();
transaction.add(Field.ORIGINATOR_VASP, originatorVASP);
transaction.add(Field.BENEFICIARY_VASP, beneficiaryVASP);
transaction.addProperty(Field.CURRENCY_ID, "sygna:0x80000090");
transaction.addProperty(Field.AMOUNT, "0.973");
  
String  dataDate = "2019-07-29T06:28:00Z";

// using signPermissionRequest to get a valid signed object (with signature attached)
JsonObject obj = new JsonObject();
obj.addProperty(Field.PRIVATE_INFO, privateInfo);
obj.add(Field.TRANSACTION, transaction);
obj.addProperty(Field.DATA_DT, dataDt);

JsonObject  signedObj = Crypto.signPermissionRequest(privateInfo, transaction, dataDate, originatorPrivateKey);
valid = Crypto.verifyObject(signedObj, originatorPublicKey);
```

We provide different methods like `signPermissionRequest`, `signCallback()` to sign different objects(or parameters) we specified in our api doc. You can also find more examples in the following section.

## API

API calls to communicate with Sygna Bridge server.

We use **basic auth** with all the API calls. To simplify the process, we provide a API class to deal with authentication and post/ get request format.

```java

String  sbServer = "https://api.sygna.io/";
API  API_UTIL = new  API("api-key", sbServer);

```

After you create the `API` instance, you can use it to make any API call to communicate with Sygna Bridge central server.

### Get VASP Information

```java
// Get List of VASPs associated with public keys.
boolean  verify = true  // set verify to true to verify the signature attached with api response automatically.
JsonArray vasps = API_UTIL.getVASPList(verify);

// Or call use getVASPPublicKey() to directly get public key for a specific VASP.
String  publicKey = API_UTIL.getVASPPublicKey("10298", verify);
```

### For Originator

There are two API calls from **transaction originator** to Sygna Bridge Server defined in the protocol, which are `postPermissionRequest` and `postTransactionId`.

The full logic of originator would be like the following:

```java
JsonParser parser = new JsonParser();
// from example above
JsonObject privateSenderInfo = parser.parse(sensitiveData).getAsJsonObject();

String  recipientPublicKey = API_UTIL.getVASPPublicKey("10298");
String  privateInfo = Crypto.encryptPrivateObj(privateSenderInfo, recipientPublicKey);

JsonObject originatorAddr = new JsonObject();
originatorAddr.addProperty(Field.ADDRESS, "3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");

JsonArray originatorAddrs = new JsonArray();
originatorAddrs.add(originatorAddr);

JsonObject originatorVASP = new JsonObject();
originatorVASP.addProperty(Field.VASP_CODE, "VASPUSNY1");
originatorVASP.add(Field.ADDRS, originatorAddrs);

JsonObject beneficiaryAddr = new JsonObject();
beneficiaryAddr.addProperty(Field.ADDRESS, "3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");

JsonArray beneficiaryAddrs = new JsonArray();
beneficiaryAddrs.add(beneficiaryAddr);

JsonObject beneficiaryVASP = new JsonObject();
beneficiaryVASP.addProperty(Field.VASP_CODE, "VASPUSNY2");
beneficiaryVASP.add(Field.ADDRS, beneficiaryAddrs);

JsonObject  transaction = new  JsonObject();
transaction.add(Field.ORIGINATOR_VASP, originatorVASP);
transaction.add(Field.BENEFICIARY_VASP, beneficiaryVASP);
transaction.addProperty(Field.CURRENCY_ID, "sygna:0x80000090");
transaction.addProperty(Field.AMOUNT, "0.973");

String  dataDt = "2019-07-29T07:29:80Z"
JsonObject permissionRequestData = new JsonObject();
permissionRequestData.addProperty(Field.PRIVATE_INFO, privateInfo);
permissionRequestData.add(Field.TRANSACTION, transaction);
permissionRequestData.addProperty(Field.DATA_DT, dataDt);
  
JsonObject permissionRequestObj = Crypto.signPermissionRequest(permissionRequestData, sender_privKey);

String  callbackUrl = "a url to receive data from beneficiary";
JsonObject callback = new JsonObject();
callback.addProperty(Field.CALLBACK_URL, callbackUrl);

JsonObject  callbackObj = Crypto.signCallBack(callback, sender_privKey);

JsonObject transferObj = new JsonObject();
transferObj.add(Field.DATA, permissionRequestObj);
transferObj.add(Field.CALLBACK, callbackObj);

JsonObject  obj = API_UTIL.postPermissionRequest(transferObj);
String  transfer_id = obj.get("transfer_id").getAsString();

// Broadcast your transaction to blockchain after got and api response at your api server.
String  txid = "1a0c9bef489a136f7e05671f7f7fada2b9d96ac9f44598e1bcaa4779ac564dcd";

// Inform Sygna Bridge that a specific transfer is successfully broadcasted to the blockchain.
JsonObject txIdObj = new JsonObject();
transferObj.add(Field.TRANSFER_ID, transfer_id);
transferObj.add(Field.TX_ID, txid);

JsonObject  sendTxIdObj = Crypto.signTxId(transfer_id, txid, sender_privKey);
JsonObject  result = API_UTIL.postTransactionId(sendTxIdObj);

```

### For Beneficiary

There is only one api for Beneficiary VASP to call, which is `postPermission`. After the beneficiary server confirm their legitimacy of a transfer request, they will sign `{ transfer_id, permission_status }` using `signPermission()` function, and send the result with signature to Sygna Bridge Central Server.

```java
String  permission_status = PermissionStatus.ACCEPTED.getStatus();//or REJECTED

JsonObject permissionObj = new JsonObject();
transferObj.add(Field.TRANSFER_ID, transfer_id);
transferObj.add(Field.PERMISSION_STATUS, permission_status);

JsonObject  signedPermissionObj = Crypto.signPermission(permissionObj, beneficiary_privKey);
String  finalResult = API_UTIL.postPermission(signedPermissionObj);
```

## Build and run example
```
mvn -f example clean package
java -cp  ./example/target/bridge-sample-2.0.0-jar-with-dependencies.jar io.sygna.bridge.sample.main
```