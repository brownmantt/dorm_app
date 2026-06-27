package com.dom.project.service.impl;

import com.dom.project.entity.DormFee;
import com.dom.project.entity.view.ResidenceHistoryView;
import com.dom.project.mapper.DormFeeMapper;
import com.dom.project.mapper.ResidenceHistoryMapper;
import com.dom.project.service.CsvExportService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CSV エクスポートサービス実装。
 */
@Service
public class CsvExportServiceImpl implements CsvExportService {

    private static final int EXPORT_LIMIT = 1000000;
    private static final byte[] UTF8_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ResidenceHistoryMapper residenceHistoryMapper;
    private final DormFeeMapper dormFeeMapper;

    public CsvExportServiceImpl(ResidenceHistoryMapper residenceHistoryMapper, DormFeeMapper dormFeeMapper) {
        this.residenceHistoryMapper = residenceHistoryMapper;
        this.dormFeeMapper = dormFeeMapper;
    }

    @Override
    public byte[] exportResidencesCsv() {
        List<ResidenceHistoryView> rows = residenceHistoryMapper.searchList(null, null, null, null, null, 0, EXPORT_LIMIT);
        StringBuilder sb = new StringBuilder();
        sb.append("residenceHistoryId,employeeId,employeeName,dormitoryId,dormitoryName,roomId,roomName,moveInDate,moveOutDate,moveOutReason\n");
        if (rows != null) {
            for (ResidenceHistoryView row : rows) {
                sb.append(csv(row.getResidenceHistoryId())).append(",")
                        .append(csv(row.getEmployeeId())).append(",")
                        .append(csv(row.getEmployeeName())).append(",")
                        .append(csv(row.getDormitoryId())).append(",")
                        .append(csv(row.getDormitoryName())).append(",")
                        .append(csv(row.getRoomId())).append(",")
                        .append(csv(row.getRoomName())).append(",")
                        .append(csv(row.getMoveInDate() == null ? null : row.getMoveInDate().toString())).append(",")
                        .append(csv(row.getMoveOutDate() == null ? null : row.getMoveOutDate().toString())).append(",")
                        .append(csv(row.getMoveOutReason()))
                        .append("\n");
            }
        }
        return withBom(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] exportDormFeesCsv() {
        List<DormFee> rows = dormFeeMapper.searchList(null, null, null, 0, EXPORT_LIMIT);
        StringBuilder sb = new StringBuilder();
        sb.append("dormFeeId,employeeId,roomId,targetYearMonth,amount,basisAreaSqm,basisDays,basisDetail,status,residenceHistoryId,createdAt,updatedAt\n");
        if (rows != null) {
            for (DormFee row : rows) {
                sb.append(csv(row.getDormFeeId())).append(",")
                        .append(csv(row.getEmployeeId())).append(",")
                        .append(csv(row.getRoomId())).append(",")
                        .append(csv(row.getTargetYearMonth())).append(",")
                        .append(csv(row.getAmount() == null ? null : row.getAmount().toPlainString())).append(",")
                        .append(csv(row.getBasisAreaSqm() == null ? null : row.getBasisAreaSqm().toPlainString())).append(",")
                        .append(csv(row.getBasisDays() == null ? null : row.getBasisDays().toString())).append(",")
                        .append(csv(row.getBasisDetail())).append(",")
                        .append(csv(row.getStatus())).append(",")
                        .append(csv(row.getResidenceHistoryId())).append(",")
                        .append(csv(row.getCreatedAt() == null ? null : DATE_TIME_FORMATTER.format(row.getCreatedAt()))).append(",")
                        .append(csv(row.getUpdatedAt() == null ? null : DATE_TIME_FORMATTER.format(row.getUpdatedAt())))
                        .append("\n");
            }
        }
        return withBom(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String csv(String value) {
        if (value == null) {
            return "\"\"";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private byte[] withBom(byte[] body) {
        byte[] result = new byte[UTF8_BOM.length + body.length];
        System.arraycopy(UTF8_BOM, 0, result, 0, UTF8_BOM.length);
        System.arraycopy(body, 0, result, UTF8_BOM.length, body.length);
        return result;
    }
}
