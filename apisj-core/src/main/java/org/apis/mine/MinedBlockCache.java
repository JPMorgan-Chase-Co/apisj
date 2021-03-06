package org.apis.mine;

import com.google.common.collect.Lists;
import org.apis.core.Block;
import org.apis.db.ByteArrayWrapper;
import org.apis.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class MinedBlockCache {

    private static MinedBlockCache sMinedBlockCache = null;
    public static MinedBlockCache getInstance() {
        if(sMinedBlockCache == null) {
            sMinedBlockCache = new MinedBlockCache();
        }

        return sMinedBlockCache;
    }


    private final Logger logger = LoggerFactory.getLogger(MinedBlockCache.class.getSimpleName());

    private final List<Block> bestMinedBlocks = new ArrayList<>();

    private final HashMap<Long, HashMap<ByteArrayWrapper, Block>> allKnownBlocks = new HashMap<>();

    private final LinkedHashMap<ByteArrayWrapper, Long> invalidBlocks = new LinkedHashMap<ByteArrayWrapper, Long>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            int maxInvalidBlockSize = 100;
            return size() > maxInvalidBlockSize;
        }
    };

    /**
     * 잘못된 블럭을 전송한 채굴자를 블랙리스트에 등록한다.
     * 최대 갯수 1000개
     */
    private final List<ByteArrayWrapper> invalidMiners = new ArrayList<>();
    private static final int MAX_INVALID_MINER_COUNT = 1_000;

    private MinedBlockCache() {}


    public List<Block> getBestMinedBlocks(int count) {
        List<Block> bestBlocks = new ArrayList<>(bestMinedBlocks);

        for(int i = 0; i < 500 && i < count && bestBlocks.size() < count && bestBlocks.size() > 0; i++) {
            Block firstBlock = bestBlocks.get(0);
            HashMap<ByteArrayWrapper, Block> blocks = allKnownBlocks.get(firstBlock.getNumber() - 1);
            if(blocks == null || blocks.isEmpty()) {
                break;
            }
            Block parentBlock = blocks.get(new ByteArrayWrapper(firstBlock.getParentHash()));
            if(parentBlock == null) {
                break;
            } else {
                bestBlocks.add(0, parentBlock);
            }
        }

        return bestBlocks;
    }

    /**
     * 다른 노드에서 전달받은 채굴 블럭들을 현재 저장된 블럭들과 비교한다.
     * 더 높은 RP 값을 보유한 블록일 경우에만 bestMinedBlocks로 대체한다.
     *
     * @param receivedBlocks bestMinedBlock과 비교하려는 블록들
     * @return true : 교체되었음 false : 기존 유지
     */
    public synchronized boolean compareMinedBlocks(List<Block> receivedBlocks) {

        for(int i = 0; i < receivedBlocks.size(); i++) {
            Block receivedBlock = receivedBlocks.get(i);

            // 전달받은 블록들 중에 검증되지 않은 불록이 있으면 빠져나간다.
            if(invalidBlocks.get(new ByteArrayWrapper(receivedBlock.getHash())) != null) {
                return false;
            }

            // 블랙리스트에 등록된 채굴자는 받아들이지 않는다
            /*if(invalidMiners.indexOf(new ByteArrayWrapper(receivedBlock.getCoinbase())) >= 0) {
                return false;
            }*/


            // 블록 번호가 연속되지 않으면, 단절된 이후의 블록은 제거한다
            List<Block> reverseBlocks = Lists.reverse(receivedBlocks);
            if(reverseBlocks.size() > 1) {
                Block child = reverseBlocks.get(0);
                for (int k = 1; k < reverseBlocks.size(); k++) {
                    Block parent = reverseBlocks.get(k);

                    if(child.getNumber() - parent.getNumber() > 1) {
                        Block finalChild = child;
                        receivedBlocks.removeIf(b -> b.getNumber() < finalChild.getNumber());
                        break;
                    }
                    child = parent;
                }
            }

            // 블록 번호가 연속되지 않았으면 빠져나간다.
            if(i > 0 && receivedBlock.getNumber() - receivedBlocks.get(i - 1).getNumber() != 1) {
                return false;
            }

            // 블록들이 서로 연결되어있지 않으면 빠져나간다.
            if(i > 0 && !FastByteComparisons.equal(receivedBlock.getParentHash(), receivedBlocks.get(i - 1).getHash())) {
                return false;
            }

            // 현재 시간보다 빠른 블록을 받았을 경우 검증하지 않는다
            if(receivedBlock.getTimestamp()*1_000L > TimeUtils.getRealTimestamp()) {
                return false;
            }
        }

        addAllBlocks(receivedBlocks);

        if(bestMinedBlocks.isEmpty()) {
            bestMinedBlocks.addAll(receivedBlocks);
            return true;
        }

        for(Block block : bestMinedBlocks) {
            if(block == null) {
                bestMinedBlocks.clear();
                bestMinedBlocks.addAll(receivedBlocks);
                return true;
            }
        }


        Block cachedLastBlock = bestMinedBlocks.get(bestMinedBlocks.size() - 1);
        Block receivedLastBlock = receivedBlocks.get(receivedBlocks.size() - 1);

        long cachedLastNumber = cachedLastBlock.getNumber();
        long receivedLastNumber = receivedLastBlock.getNumber();

        // 최신 블록이 아니면 추가할 필요 없음
        if(receivedLastNumber < cachedLastNumber) {
            //ConsoleUtil.printlnYellow("MinedBlockCache : The block received is not up to date and has not been added.");
            return false;
        }

        /*
         * 동일한 RP 값을 갖는 블록일 경우
         * 블록의 해시 값을 비교한다.
         * 해시 값이 더 클 경우 받아들이고, 이하일 경우 받아들이지 않는다
         */
        if(cachedLastNumber == receivedLastNumber) {
            BigInteger lastCachedCRP = cachedLastBlock.getCumulativeRewardPoint();
            BigInteger lastReceivedCRP = receivedLastBlock.getCumulativeRewardPoint();

            if(lastReceivedCRP.compareTo(lastCachedCRP) == 0) {
                BigInteger lastCachedHash = ByteUtil.bytesToBigInteger(cachedLastBlock.getHash());
                BigInteger lastReceivedHash = ByteUtil.bytesToBigInteger(receivedLastBlock.getHash());

                if (lastReceivedHash.compareTo(lastCachedHash) <= 0) {
                    return false;
                }
            }
            else if(lastReceivedCRP.compareTo(lastCachedCRP) < 0) {
                return false;
            }
        }


        // 비교 결과, 전달받은 블록들로 체인을 연결해도 되는것으로 판단된다
        bestMinedBlocks.clear();
        if(receivedBlocks.size() > 0) {
            bestMinedBlocks.add(receivedBlocks.get(receivedBlocks.size() - 1));
        }
        while(bestMinedBlocks.size() < 8 && bestMinedBlocks.size() > 0) {
            Block firstBlock = bestMinedBlocks.get(0);
            HashMap<ByteArrayWrapper, Block> blocks = allKnownBlocks.get(firstBlock.getNumber() - 1);
            if(blocks == null || blocks.isEmpty()) {
                break;
            }
            Block parentBlock = blocks.get(new ByteArrayWrapper(firstBlock.getParentHash()));
            if(parentBlock == null) {
                break;
            } else {
                bestMinedBlocks.add(0, parentBlock);
            }
        }

        /*if(!bestMinedBlocks.isEmpty()) {
            bestMinedBlocks.removeIf(block -> block.getNumber() >= receivedBestBlocks.get(0).getNumber());
        }
        if(!bestMinedBlocks.isEmpty()) {
            bestMinedBlocks.removeIf(block -> block.getNumber() <= receivedBestBlock.getNumber() - 5);
        }*/

        //bestMinedBlocks.addAll(receivedBestBlocks);

        logger.info("Cached blocks changed : Last block : {}, miner : {}", receivedLastBlock.getShortDescr(), AddressUtil.getShortAddress(receivedLastBlock.getCoinbase()));
        return true;
    }

    private void addAllBlocks(List<Block> receivedBlocks) {
        if(receivedBlocks == null || receivedBlocks.isEmpty()) {
            return;
        }
        synchronized (allKnownBlocks) {
            for (Block receivedBlock : receivedBlocks) {
                final long blockNumber = receivedBlock.getNumber();

                HashMap<ByteArrayWrapper, Block> blocks = allKnownBlocks.get(blockNumber);
                blocks = (blocks == null ? new HashMap<>() : blocks);

                ByteArrayWrapper blockHashW = new ByteArrayWrapper(receivedBlock.getHash());
                if (blocks.get(blockHashW) == null) {
                    blocks.put(blockHashW, receivedBlock);
                    allKnownBlocks.put(blockNumber, blocks);
                }
            }

            // 오래된 데이터는 삭제
            Block firstBlock = receivedBlocks.get(0);

            if (!allKnownBlocks.isEmpty() && firstBlock != null) {
                allKnownBlocks.keySet().removeIf(key -> key < firstBlock.getNumber() - 333);
            }
        }

        /*System.out.println("----");
        for(long key : allKnownBlocks.keySet()) {
            System.out.println(key + " : " + allKnownBlocks.get(key).size() + " : " + getBestBlock(key).getShortDescr());
        }*/
    }

    /**
     * 올바르지 못한 블럭을 받았을 수 있다.
     * 검증을 통과하지 못했을 경우, 리스트에서 삭제하고, 블랙리스트에 등록시킨다.
     * @param invalidBlock invalid block
     */
    public void removeBestBlock(Block invalidBlock) {
        ByteArrayWrapper blockHashW = new ByteArrayWrapper(invalidBlock.getHash());

        invalidBlocks.keySet().removeIf(key -> key.equals(blockHashW));
        invalidBlocks.put(blockHashW, invalidBlock.getNumber());

        // 채굴자도 블랙 리스트에 등록한다.
        ByteArrayWrapper minerW = new ByteArrayWrapper(invalidBlock.getCoinbase());
        if(invalidMiners.indexOf(minerW) < 0) {
            invalidMiners.add(minerW);
            if(invalidMiners.size() > MAX_INVALID_MINER_COUNT) {
                invalidMiners.remove(0);
            }
        }

        /*
         * bestMinedBlocks 리스트에서도 invalid block을 삭제한다.
         * 만약 invalid block이 존재하면 그 블록의 자식 블록들도 삭제해야한다.
         */
        boolean hasInvalidBlock = false;
        for(Iterator<Block> it = bestMinedBlocks.iterator(); it.hasNext();) {
            Block best = it.next();
            if(blockHashW.equals(new ByteArrayWrapper(best.getHash()))) {
                hasInvalidBlock = true;
                it.remove();
                continue;
            }

            if(hasInvalidBlock) {
                it.remove();
            }
        }

        HashMap<ByteArrayWrapper, Block> blocks = allKnownBlocks.get(invalidBlock.getNumber());
        if(blocks == null || blocks.isEmpty()) {
            return;
        }

        blocks.keySet().removeIf(key -> key.equals(blockHashW));
        allKnownBlocks.replace(invalidBlock.getNumber(), blocks);
    }


    long getBestBlockNumber() {
        if(bestMinedBlocks.isEmpty()) {
            return 0;
        }

        if(getBestBlock() == null) {
            return 0;
        } else {
            return getBestBlock().getNumber();
        }
    }

    public Block getBestBlock() {
        if(bestMinedBlocks.isEmpty()) {
            return null;
        }

        return bestMinedBlocks.get(bestMinedBlocks.size() - 1);
    }
}
