package ru.humaninweb.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.humaninweb.dto.ParseDto;

/**
 *
 * @author vit
 */
@Log4j2
@Repository
public class ParseDao extends JdbcDaoSupport {

    @Autowired
    public ParseDao(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    /**
     * Вставка файла для разбора
     *
     * @param reqtime Время запроса
     * @param reqLink Ссылка на файл запроса
     * @return Идентификатор записи
     */
    public BigDecimal insertReq(LocalDateTime reqtime, String reqLink) {
        String sql = "INSERT INTO PARSE(reqtime, reqlink) VALUES(?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int insertCount = getJdbcTemplate().update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(reqtime));
            ps.setString(2, reqLink);
            return ps;
        }, keyHolder);
        BigDecimal insertId = keyHolder.getKey() == null ? BigDecimal.ZERO : BigDecimal.valueOf(keyHolder.getKey().longValue());
        log.info("INSERT REQ ID {} COUNT {}", insertId, insertCount);
        return insertId;
    }

    /**
     * Получение данных запроса
     *
     * @return
     */
    public List<ParseDto> selectReq() {
        String sql = "SELECT id, reqlink FROM PARSE WHERE resplink IS NULL;";
        return getJdbcTemplate().query(sql, (ResultSet rs, int i) -> {
            ParseDto dto = new ParseDto();
            dto.setReqId(rs.getBigDecimal("id"));
            dto.setReqLink(rs.getString("reqlink"));
            return dto;
        });
    }

    /**
     * Вставка файла ответа
     *
     * @param dto
     * @return
     */
    public long insertResp(ParseDto dto) {
        String sql = "UPDATE PARSE SET resptime=?, resplink=?;";
        long insertCount = getJdbcTemplate().update(sql, (PreparedStatement ps) -> {
            ps.setTimestamp(1, Timestamp.valueOf(dto.getRespTime()));
            ps.setString(2, dto.getRespLink());
        });
        log.info("INSERT RESP ID {} COUNT {}", dto.getReqId(), insertCount);
        return insertCount;
    }

    public List<String> selectResp(BigDecimal id) {
        String sql = "SELECT resplink AS resplink FROM PARSE WHERE id=? AND resplink IS NOT NULL;";
        return getJdbcTemplate().queryForList(sql, new Object[]{id}, String.class);
    }
}
