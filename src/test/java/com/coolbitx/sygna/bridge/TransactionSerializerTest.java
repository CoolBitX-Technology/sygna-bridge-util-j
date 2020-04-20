/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coolbitx.sygna.bridge;

import com.coolbitx.sygna.bridge.model.Transaction;
import com.coolbitx.sygna.json.TransactionSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author kunming.liu
 */
public class TransactionSerializerTest {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Transaction.class, new TransactionSerializer())
            .create();

    @Test
    public void testTransactionSerializer() {
        String signature = "";
        String transferId = "123";
        String txId = "456";

        //should ignore signature if signature is empty
        Transaction transaction = new Transaction(transferId, txId,signature);
        JsonElement jsonElement = gson.toJsonTree(transaction, Transaction.class);
        String expectedMessage
                = String.format("{\"transfer_id\":\"%s\",\"txid\":\"%s\"}",
                        transferId, txId);
        assertEquals(jsonElement.toString(), expectedMessage);

        signature = "123456789";
        Transaction transaction1 = new Transaction(transferId, txId,signature);
        JsonElement jsonElement1 = gson.toJsonTree(transaction1, Transaction.class);
        String expectedMessage1
                = String.format("{\"transfer_id\":\"%s\",\"txid\":\"%s\",\"signature\":\"%s\"}",
                        transferId, txId, signature);
        assertEquals(jsonElement1.toString(), expectedMessage1);
    }
}
