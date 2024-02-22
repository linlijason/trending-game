package com.bistro.module.task;

import com.alibaba.fastjson.JSON;
import com.bistro.constants.Constants;
import com.bistro.constants.RedisConstants;
import com.bistro.module.api.vo.GameBetDetail;
import com.bistro.module.api.vo.GameInfo;
import com.bistro.module.api.vo.GamePlayInfo;
import com.bistro.module.game.domain.GameBaseInfo;
import com.bistro.module.game.mapper.GameBaseInfoMapper;
import com.bistro.module.game.service.IGameRankService;
import com.bistro.utils.BetidHashUtils;
import com.bistro.utils.MultiplierUtils;
import com.bistro.utils.RandomNumbersUtils;
import com.bistro.utils.WeightRandomUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component("fakeBetTask")
public class FakeBetTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(FakeBetTask.class);

    private static char NAME_CHARS[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n'
            , 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    //名字加星号显示 就没必要这么长了
    private static final int NAME_LENGTH_MIN = 6;
    private static final int NAME_LENGTH_MAX = 10;
    private static final Long BET_ID_START = 500000000L;//亿
    @Value("${currency.default}")
    private String CURRENCY;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GameBaseInfoMapper baseInfoMapper;

    @Autowired
    private IGameRankService gameRankService;


    public void fakeMinesBet(Double multiplierMax, Integer minesCountMax, Integer betAmountMax, Integer winProbability, Integer jobInterval) {

        GameBaseInfo gameBaseInfo = baseInfoMapper.selectGameBaseInfoByCode(Constants.MINES_GAME_CODE);

        //随机雷
        int minesCount = new Random().nextInt(minesCountMax - Constants.SELECT_MINES_BLOCKS_MIN) + Constants.SELECT_MINES_BLOCKS_MIN;
        int minesIndex[] = RandomNumbersUtils.randomArray(0, Constants.MINES_BLOCKS - 1, minesCount);

        //随机输赢
        winProbability = winProbability % 101;//0-100 的数,如果超过了就取模
        List<Pair<String, Integer>> list = Lists.newArrayList();
        list.add(Pair.of("1", winProbability));
        list.add(Pair.of("0", 100 - winProbability));

        boolean win = "1".equals(new WeightRandomUtils(list).random());

        List<GameInfo.MultiplierHits> multiplierHits = MultiplierUtils.getMultiplierList(minesCount, gameBaseInfo.getMimesConfigs());

        BigDecimal multiplier = BigDecimal.ZERO;

        //随机钻石
        List<Integer> stoneIndex = new ArrayList<>();
        //砖石最大数量 根据赔率计算
        int maxStoneCount = 0;
        for (GameInfo.MultiplierHits item : multiplierHits) {
            if (item.getMultiplier().compareTo(new BigDecimal(multiplierMax)) <= 0) {
                maxStoneCount = item.getHits();
            }
        }

        List<Integer> minesList = Arrays.stream(minesIndex).boxed().collect(Collectors.toList());
        for (int i = 0; i < Constants.MINES_BLOCKS; i++) {
            if (minesList.contains(i)) {
//                        LOGGER.info("minesIndex contains{},{}",i,minesIndex);
                continue;
            } else {
//                        LOGGER.info("minesIndex not contains{},{}",i,minesIndex);
                stoneIndex.add(i);
            }
        }
        Collections.shuffle(stoneIndex);
        if (win && maxStoneCount > 0) {
            int stoneCount = new Random().nextInt(maxStoneCount) + 1;
            stoneIndex = stoneIndex.stream().limit(stoneCount).collect(Collectors.toList());
            multiplier = MultiplierUtils.calculate(Constants.MINES_BLOCKS, minesCount, stoneCount, gameBaseInfo.getCommissionRate());
        } else {//输 或者赔率大于了job配置的赔率也未输
            int stoneCount = new Random().nextInt(stoneIndex.size());
            Collections.shuffle(minesList);
            if (stoneCount > 0) {
                stoneIndex = stoneIndex.stream().limit(stoneCount).collect(Collectors.toList());
                stoneIndex.addAll(minesList.stream().limit(1).collect(Collectors.toList()));//加上雷
            } else {
                stoneIndex = minesList.stream().limit(1).collect(Collectors.toList());//没有钻石直接雷
            }

        }
        //随机下注金额
        BigDecimal betAmount = new BigDecimal(betAmountMax.intValue() * new Random().nextDouble()).setScale(new Random().nextInt(3), BigDecimal.ROUND_DOWN);

        //betId
        Long betId = redisTemplate.opsForValue().increment(RedisConstants.FAKE_BRT_ID_INCR);
        if (betId < BET_ID_START) {
            betId = BET_ID_START;
            redisTemplate.opsForValue().set(RedisConstants.FAKE_BRT_ID_INCR, String.valueOf(BET_ID_START));
        }
        String hashBetId = BetidHashUtils.hashId(betId);

        GamePlayInfo gamePlayInfo = new GamePlayInfo();
        gamePlayInfo.setBetId(hashBetId);
        gamePlayInfo.setBetAmount(betAmount);
        gamePlayInfo.setCurrency(CURRENCY);
        gamePlayInfo.setPlayer(randomName());
        gamePlayInfo.setPlayTime(String.valueOf(System.currentTimeMillis() / 1000 - new Random().nextInt(jobInterval)));
        gamePlayInfo.setMultiplier(multiplier);
        boolean isBigthanMaxPayout = betAmount.multiply(multiplier).setScale(Constants.PAYOUT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_DOWN).compareTo(gameBaseInfo.getMaxPayoutAmount()) >= 0 ? true : false;
        //如果payout 大于maxPayout, 默认stoneCount =1,不然全是maxpayout
        if (win && maxStoneCount > 0 && isBigthanMaxPayout) {
            multiplier = MultiplierUtils.calculate(Constants.MINES_BLOCKS, minesCount, 1, gameBaseInfo.getCommissionRate());
            stoneIndex = stoneIndex.subList(0, 1);
            gamePlayInfo.setMultiplier(multiplier);
        }

        gamePlayInfo.setPayoutAmount(MultiplierUtils.calPayout(multiplier, betAmount, gameBaseInfo.getMaxPayoutAmount()));

        GameBetDetail gameBetDetail = new GameBetDetail();
        gameBetDetail.setBetId(hashBetId);
        gameBetDetail.setClickedIndex(stoneIndex);
        gameBetDetail.setMinesIndex(JSON.parseArray(JSON.toJSONString(minesIndex)));
        gamePlayInfo.setDetail(gameBetDetail);
        gameRankService.addRankRecord(gamePlayInfo, Constants.MINES_GAME_CODE);
    }

    private static String randomName() {

        int length = new Random().nextInt(NAME_LENGTH_MAX - NAME_LENGTH_MIN) + NAME_LENGTH_MIN;
        List<Integer> charIndexes = new Random().ints(0, NAME_CHARS.length)
                .limit(length).boxed().collect(Collectors.toList());

        StringBuilder name = new StringBuilder();
        for (Integer index : charIndexes) {
            name.append(NAME_CHARS[index]);
        }

        return name.toString();


    }

    public static void main(String[] args) {
        System.out.println(randomName());
        System.out.println(randomName());
    }

}
