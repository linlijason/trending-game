package com.bistro.module.game.service.impl;

import com.bistro.common.core.domain.model.LoginUser;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.common.utils.sign.Md5Utils;
import com.bistro.framework.manager.AsyncManager;
import com.bistro.module.game.domain.BetGameUserQuery;
import com.bistro.module.game.domain.BetGameUserVo;
import com.bistro.module.game.domain.ExportFile;
import com.bistro.module.game.domain.GameEnum;
import com.bistro.module.game.mapper.ExportFileMapper;
import com.bistro.module.game.mapper.GameBetMapper;
import com.bistro.module.game.service.BetGameUserService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class BetGameUserServiceImpl implements BetGameUserService {
    private final static Logger log = LoggerFactory.getLogger(BetGameUserServiceImpl.class);
    @Value("${export.path}")
    private String FILE_PATH;
    @Autowired
    private GameBetMapper betMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private ExportFileMapper exportFileMapper;

    @Override
    public List<BetGameUserVo> search(BetGameUserQuery query) {
        return betMapper.selectBetGameUser(query);
    }

    @Override
    public void exportReport(BetGameUserQuery query, LoginUser loginUser) {
        log.info("exportReport：{}", loginUser.getUsername());
        ExportFile exportFile = new ExportFile();
        String fileName = UUID.randomUUID().toString();
        String excelName = FILE_PATH + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(query.getBeginCreateTime()) + "_"
                +DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format( query.getEndCreateTime())  + "_" + System.currentTimeMillis() + ".xlsx";
        String zipName = FILE_PATH + fileName + ".zip";
        exportFile.setFileName(fileName + ".zip");
        exportFile.setUid(loginUser.getUserId());
        exportFile.setUserName(loginUser.getUsername());
        exportFile.setPassword(Md5Utils.hash(exportFile.getFileName() + "aspasswaword").substring(0, 10));
        exportFileMapper.insertExportFile(exportFile);

        AsyncManager.me().execute(() -> {
            log.info("exportReport：get data start {}", exportFile.getFileName());
            List<List<Object>> data = new ArrayList<>();
            SqlSession sqlSession = sqlSessionFactory.openSession();
            File file = new File(excelName);
            try {
                Cursor<BetGameUserVo> result = sqlSession.getMapper(GameBetMapper.class).selectBetForExport(query);
                result.forEach(row -> {
                    List rowList = new ArrayList();
//                System.out.println(JSONObject.toJSONString(row));
                    rowList.add((int)row.getBetId());
                    rowList.add(row.getUserName());
                    rowList.add(row.getBetAmount().doubleValue());
                    rowList.add(row.getMultiplier().doubleValue());
                    rowList.add(row.getPayoutAmount().doubleValue());
                    rowList.add(GameEnum.BetStatusEnum.getNameByStatus(row.getStatus()));
                    rowList.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(row.getBetTime()));
//                    rowList.add(row.getBetTime());
                    rowList.add(row.getGameCode());
                    rowList.add(row.getCurrency());
                    data.add(rowList);
                });

                List<Pair<String, Integer>> header = new ArrayList<>();
                header.add(new ImmutablePair<>("betId", 0));
                header.add(new ImmutablePair<>("用户名", 0));
                header.add(new ImmutablePair<>("下注金额", 0));
                header.add(new ImmutablePair<>("赔率", 0));
                header.add(new ImmutablePair<>("派奖金额", 0));
                header.add(new ImmutablePair<>("状态", 0));
                header.add(new ImmutablePair<>("时间", 0));
                header.add(new ImmutablePair<>("游戏代码", 0));
                header.add(new ImmutablePair<>("货币", 0));


                //生成excel
                log.info("exportReport：exportExcel start {}", exportFile.getFileName());

                if(!file.exists()){
                    Files.createFile(file.toPath());
                }
                ExcelUtil.exportExcel(header, data, new FileOutputStream(excelName));
                log.info("exportReport：exportExcel done {}", exportFile.getFileName());
                //压缩
                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(EncryptionMethod.AES);
                // Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
                zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);


                List<File> filesToAdd = Arrays.asList(
                        file
                );

                ZipFile zipFile = new ZipFile(zipName, exportFile.getPassword().toCharArray());
                zipFile.addFiles(filesToAdd, zipParameters);
                log.info("exportReport：zip end {}", exportFile.getFileName());
                exportFile.setStatus(1);
                exportFileMapper.updateExportFile(exportFile);
            } catch (Exception e) {
                log.error("exportReport：failed end {}", exportFile.getFileName(), e);
                exportFile.setStatus(2);
                exportFileMapper.updateExportFile(exportFile);
            } finally {
                sqlSession.close();
                file.delete();
            }
        });
    }
}
