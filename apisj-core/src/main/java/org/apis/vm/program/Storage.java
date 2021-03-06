/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apis.vm.program;

import org.apis.core.*;
import org.apis.db.ByteArrayWrapper;
import org.apis.db.ContractDetails;
import org.apis.util.MasternodeSize;
import org.apis.vm.DataWord;
import org.apis.vm.program.invoke.ProgramInvoke;
import org.apis.vm.program.listener.ProgramListener;
import org.apis.vm.program.listener.ProgramListenerAware;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;

public class Storage implements Repository, ProgramListenerAware {

    private final Repository repository;
    private final DataWord address;
    private ProgramListener programListener;

    public Storage(ProgramInvoke programInvoke) {
        this.address = programInvoke.getOwnerAddress();
        this.repository = programInvoke.getRepository();
    }

    @Override
    public void setProgramListener(ProgramListener listener) {
        this.programListener = listener;
    }

    @Override
    public AccountState createAccount(byte[] addr) {
        return repository.createAccount(addr);
    }

    @Override
    public AccountState createAccount(byte[] addr, long blockNumber) {
        return repository.createAccount(addr, blockNumber);
    }

    @Override
    public boolean isExist(byte[] addr) {
        return repository.isExist(addr);
    }

    @Override
    public AccountState getAccountState(byte[] addr) {
        return repository.getAccountState(addr);
    }

    @Override
    public void delete(byte[] addr) {
        if (canListenTrace(addr)) programListener.onStorageClear();
        repository.delete(addr);
    }

    @Override
    public BigInteger increaseNonce(byte[] addr) {
        return repository.increaseNonce(addr);
    }

    @Override
    public BigInteger setNonce(byte[] addr, BigInteger nonce) {
        return repository.setNonce(addr, nonce);
    }

    @Override
    public BigInteger getNonce(byte[] addr) {
        return repository.getNonce(addr);
    }

    @Override
    public ContractDetails getContractDetails(byte[] addr) {
        return repository.getContractDetails(addr);
    }

    @Override
    public boolean hasContractDetails(byte[] addr) {
        return repository.hasContractDetails(addr);
    }

    @Override
    public void saveCode(byte[] addr, byte[] code) {
        repository.saveCode(addr, code);
    }

    @Override
    public byte[] getCode(byte[] addr) {
        return repository.getCode(addr);
    }

    @Override
    public byte[] getCodeHash(byte[] addr) {
        return repository.getCodeHash(addr);
    }

    @Override
    public void addStorageRow(byte[] addr, DataWord key, DataWord value) {
        if (canListenTrace(addr)) programListener.onStoragePut(key, value);
        repository.addStorageRow(addr, key, value);
    }

    private boolean canListenTrace(byte[] address) {
        return (programListener != null) && this.address.equals(new DataWord(address));
    }

    @Override
    public DataWord getStorageValue(byte[] addr, DataWord key) {
        return repository.getStorageValue(addr, key);
    }

    @Override
    public BigInteger getBalance(byte[] addr) {
        return repository.getBalance(addr);
    }

    @Override
    public BigInteger getMineral(byte[] addr, long blockNumber) {
        return repository.getMineral(addr, blockNumber);
    }

    @Override
    public BigInteger getTotalReward(byte[] addr) {
        return repository.getTotalReward(addr);
    }

    @Override
    public BigInteger addBalance(byte[] addr, BigInteger value) {
        return repository.addBalance(addr, value);
    }

    @Override
    public BigInteger addBalance(byte[] addr, BigInteger value, long blockNumber) {
        return repository.addBalance(addr, value, blockNumber);
    }

    @Override
    public BigInteger addReward(byte[] addr, BigInteger reward) {
        return repository.addReward(addr, reward);
    }

    @Override
    public BigInteger setMineral(byte[] addr, BigInteger value, long blockNumber) {
        return repository.setMineral(addr, value, blockNumber);
    }

    @Override
    public String setAddressMask(byte[] addr, String mask) {
        return repository.setAddressMask(addr, mask);
    }

    @Override
    public String getMaskByAddress(byte[] addr) {
        return repository.getMaskByAddress(addr);
    }

    @Override
    public byte[] getAddressByMask(String mask) {
        return repository.getAddressByMask(mask);
    }

    @Override
    public byte[] getProofKey(byte[] addr) {
        return repository.getProofKey(addr);
    }

    @Override
    public long getMnStartBlock(byte[] addr) {
        return repository.getMnStartBlock(addr);
    }

    @Override
    public long setMnStartBlock(byte[] addr, long blockNumber) {
        return repository.setMnStartBlock(addr, blockNumber);
    }

    @Override
    public long getMnLastBlock(byte[] addr) {
        return repository.getMnLastBlock(addr);
    }

    @Override
    public long setMnLastBlock(byte[] addr, long blockNumber) {
        return repository.setMnLastBlock(addr, blockNumber);
    }

    @Override
    public List<byte[]> getUpdatingMnList(long blockNumber) {
        return repository.getUpdatingMnList(blockNumber);
    }

    @Override
    public List<byte[]> getNodeListToCheckExpiration(long blockNumber) {
        return repository.getNodeListToCheckExpiration(blockNumber);
    }

    @Override
    public byte[] getMnRecipient(byte[] addr) {
        return repository.getMnRecipient(addr);
    }

    @Override
    public byte[] setMnRecipient(byte[] addr, byte[] recipient) {
        return repository.setMnRecipient(addr, recipient);
    }

    @Override
    public BigInteger getMnStartBalance(byte[] addr) {
        return repository.getMnStartBalance(addr);
    }

    @Override
    public BigInteger setMnStartBalance(byte[] addr, BigInteger balance) {
        return repository.setMnStartBalance(addr, balance);
    }

    @Override
    public void checkMasternodeCollateral(byte[] sender, long blockNumber) {
        repository.checkMasternodeCollateral(sender, blockNumber);
    }

    @Override
    public void cleaningMasterNodes(long blockNumber) {
        repository.cleaningMasterNodes(blockNumber);
    }

    @Override
    public void updateMasterNode(Transaction tx, long blockNumber) {
        repository.updateMasterNode(tx, blockNumber);
    }

    @Override
    public void updateMasterNodeEarlyBird(TransactionReceipt receipt, long blockNumber) {
        repository.updateMasterNodeEarlyBird(receipt, blockNumber);
    }

    @Override
    public MasternodeSize sizeofMasterNode(byte[] baseNode) {
        return repository.sizeofMasterNode(baseNode);
    }

    @Override
    public boolean isIncludedInMasternodes(byte[] address) {
        return repository.isIncludedInMasternodes(address);
    }

    @Override
    public List<byte[]> getMasterNodeList(byte[] baseNode) {
        return repository.getMasterNodeList(baseNode);
    }

    @Override
    public List<byte[]> getMasterNodeList(byte[] baseNode, long blockNumber) {
        return repository.getMasterNodeList(baseNode, blockNumber);
    }

    @Override
    public long getMasternodeSize(BigInteger collateral) {
        return repository.getMasternodeSize(collateral);
    }

    @Override
    public boolean isRecipientOfMasternode(byte[] address) {
        return repository.isRecipientOfMasternode(address);
    }

    @Override
    public void updateAddressMask(TransactionReceipt receipt) {
        repository.updateAddressMask(receipt);
    }

    @Override
    public void updateProofOfKnowledge(TransactionReceipt receipt) {
        repository.updateProofOfKnowledge(receipt);
    }

    @Override
    public void updatePurchasedMineral(TransactionReceipt receipt, long blockNumber) {
        repository.updatePurchasedMineral(receipt, blockNumber);
    }

    @Override
    public void insertMnState(byte[] prevMn, byte[] addr, long blockNumber, BigInteger startBalance, byte[] recipient) {
        repository.insertMnState(prevMn, addr, blockNumber, startBalance, recipient);
    }


    @Override
    public byte[] setProofKey(byte[] addr, byte[] proofKey) {
        return repository.setProofKey(addr, proofKey);
    }

    @Override
    public BigInteger addMineral(byte[] addr, BigInteger value, long blockNumber) {
        return repository.addMineral(addr, value, blockNumber);
    }

    @Override
    public Set<byte[]> getAccountsKeys() {
        return repository.getAccountsKeys();
    }

    @Override
    public void dumpState(Block block, long gasUsed, int txNumber, byte[] txHash) {
        repository.dumpState(block, gasUsed, txNumber, txHash);
    }

    @Override
    public Repository startTracking() {
        return repository.startTracking();
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public void flushNoReconnect() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void commit() {
        repository.commit();
    }

    @Override
    public void rollback() {
        repository.rollback();
    }

    @Override
    public void syncToRoot(byte[] root) {
        repository.syncToRoot(root);
    }

    @Override
    public boolean isClosed() {
        return repository.isClosed();
    }

    @Override
    public void close() {
        repository.close();
    }

    @Override
    public void reset() {
        repository.reset();
    }

    @Override
    public void updateBatch(HashMap<ByteArrayWrapper, AccountState> accountStates, HashMap<ByteArrayWrapper, ContractDetails> contractDetails) {
        for (ByteArrayWrapper address : contractDetails.keySet()) {
            if (!canListenTrace(address.getData())) return;

            ContractDetails details = contractDetails.get(address);
            if (details.isDeleted()) {
                programListener.onStorageClear();
            } else if (details.isDirty()) {
                for (Map.Entry<DataWord, DataWord> entry : details.getStorage().entrySet()) {
                    programListener.onStoragePut(entry.getKey(), entry.getValue());
                }
            }
        }
        repository.updateBatch(accountStates, contractDetails);
    }

    @Override
    public byte[] getRoot() {
        return repository.getRoot();
    }

    @Override
    public void loadAccount(byte[] addr, HashMap<ByteArrayWrapper, AccountState> cacheAccounts, HashMap<ByteArrayWrapper, ContractDetails> cacheDetails) {
        repository.loadAccount(addr, cacheAccounts, cacheDetails);
    }

    @Override
    public Repository getSnapshotTo(byte[] root) {
        throw new UnsupportedOperationException();
    }

    /*@Override
    public Repository getSnapshotTo(long blockNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Block getBlock(long blockNumber) {
        throw new UnsupportedOperationException();
    }*/

    @Override
    public int getStorageSize(byte[] addr) {
        return repository.getStorageSize(addr);
    }

    @Override
    public Set<DataWord> getStorageKeys(byte[] addr) {
        return repository.getStorageKeys(addr);
    }

    @Override
    public Map<DataWord, DataWord> getStorage(byte[] addr, @Nullable Collection<DataWord> keys) {
        return repository.getStorage(addr, keys);
    }
}
