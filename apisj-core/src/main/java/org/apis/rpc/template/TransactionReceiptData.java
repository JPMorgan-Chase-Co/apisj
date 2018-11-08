package org.apis.rpc.template;

import com.google.gson.GsonBuilder;
import org.apis.core.Block;
import org.apis.core.Transaction;
import org.apis.core.TransactionInfo;
import org.apis.core.TransactionReceipt;
import org.apis.util.ByteUtil;
import org.apis.util.blockchain.ApisUtil;
import org.apis.vm.LogInfo;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.apis.util.ByteUtil.toHexString0x;
import static org.apis.util.blockchain.ApisUtil.readableApis;


public class TransactionReceiptData {
    /**
     * either 1 (success) or 0 (failure)
     */
    private String status;

    /**
     * 32 Bytes - hash of the transaction.
     */
    private String transactionHash;

    /**
     * integer of the transactions index position in the block.
     */
    private int transactionIndex;

    /**
     * 32 Bytes - hash of the block where this transaction was in.
     */
    private String blockHash;

    /**
     * block number where this transaction was in.
     */
    private long blockNumber;

    /**
     * 20 Bytes - address of the sender.
     */
    private String from;

    /**
     * 20 Bytes - address of the receiver. null when its a contract creation transaction.
     */
    private String to;

    private String toMask;

    /**
     * 20 Bytes - The contract address created, if the transaction was a contract creation, otherwise null
     */
    private String contractAddress;

    private long gas;

    private String gasPrice;
    private String gasPriceAPIS;

    /**
     * The amount of gas used by this specific transaction alone.
     */
    private long gasUsed;

    /**
     * The amount of mineral used by this specific transaction alone.
     */
    private String fee;
    private String feeAPIS;

    private String mineralUsed;
    private String mineralUsedMNR;

    private String feePaid;
    private String feePaidAPIS;


    /**
     * The total amount of gas used when this transaction was executed in the block.
     */
    private long cumulativeGasUsed;

    /**
     * The total amount of mineral used when this transaction was executed in the block.
     */
    private String cumulativeMineralUsed;
    private String cumulativeMineralUsedMNR;

    /**
     * Array of log objects, which this transaction generated.
     */
    private List<LogInfoData> logs;

    /**
     * 256 Bytes - Bloom filter for light clients toAddress quickly retrieve related logs.
     */
    private String logsBloom;

    public TransactionReceiptData(TransactionInfo info, Block block) {
        TransactionReceipt receipt = info.getReceipt();
        Transaction tx = receipt.getTransaction();

        this.transactionHash = toHexString0x(tx.getHash());

        this.transactionIndex = info.getIndex();

        this.blockNumber = block.getNumber();

        this.blockHash = toHexString0x(info.getBlockHash());

        this.from = toHexString0x(tx.getSender());

        if(!ByteUtil.isNullOrZeroArray(tx.getReceiveAddress())) {
            this.to = toHexString0x(tx.getReceiveAddress());
        }

        if(!ByteUtil.isNullOrZeroArray(tx.getReceiveMask())) {
            this.toMask = new String(tx.getReceiveMask(), Charset.forName("UTF-8"));
        }

        if(!ByteUtil.isNullOrZeroArray(tx.getContractAddress())) {
            this.contractAddress = toHexString0x(tx.getContractAddress());
        }

        BigInteger gasPrice = ByteUtil.bytesToBigInteger(tx.getGasPrice());
        this.gasPrice = gasPrice.toString();
        this.gasPriceAPIS = readableApis(gasPrice.toString(), ',', ApisUtil.Unit.nAPIS, true) + " nAPIS";

        BigInteger gasUsed = ByteUtil.bytesToBigInteger(receipt.getGasUsed());
        this.gasUsed = gasUsed.longValue();

        BigInteger mineralUsed = ByteUtil.bytesToBigInteger(receipt.getMineralUsed());
        this.mineralUsed = mineralUsed.toString();
        this.mineralUsedMNR = readableApis(mineralUsed, ',', true) + " MNR";

        BigInteger gasLimit = ByteUtil.bytesToBigInteger(tx.getGasLimit());
        this.gas = gasLimit.longValue();

        this.cumulativeGasUsed = receipt.getCumulativeGasLong();

        this.cumulativeMineralUsed = receipt.getCumulativeMineralBI().toString();
        this.cumulativeMineralUsedMNR = readableApis(receipt.getCumulativeMineralBI(), ',', true) + " MNR";

        BigInteger fee = gasUsed.multiply(gasPrice);
        this.fee = gasUsed.multiply(gasPrice).toString();
        this.feeAPIS = readableApis(fee, ',', true) + " APIS";

        BigInteger feePaid = gasUsed.multiply(gasPrice).subtract(mineralUsed);
        this.feePaid = feePaid.toString();
        this.feePaidAPIS = readableApis(feePaid, ',', true);

        if (receipt.getLogInfoList().size() > 0) {
            this.logs = new ArrayList<>();
            this.logsBloom = toHexString0x(receipt.getBloomFilter().getData());
        }
        int logIndex = 0;
        for(LogInfo logInfo : receipt.getLogInfoList()) {
            this.logs.add(new LogInfoData(logInfo, blockHash, transactionHash, logIndex, blockNumber, transactionIndex));
            logIndex += 1;
        }

        this.status = toHexString0x(receipt.getPostTxState());
    }

    public String getJson() {
        return new GsonBuilder().create().toJson(this);
    }
    /*public TransactionReceiptData(String transactionHash, int transactionIndex, long blockNumber, String blockHash,
                                  BigInteger cumulativeGasUsed, String log, String logsBloom, int status) {
        this.transactionHash = transactionHash;
        this.transactionIndex = transactionIndex;
        this.blockNumber = blockNumber;
        this.blockHash = blockHash;
        this.cumulativeGasUsed = cumulativeGasUsed;
        this.log = log;
        this.logsBloom = logsBloom;
        this.status = status;
    }*/
}
