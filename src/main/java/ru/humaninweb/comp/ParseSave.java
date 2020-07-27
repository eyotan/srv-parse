package ru.humaninweb.comp;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.humaninweb.dao.ParseDao;
import ru.humaninweb.dto.ParseDto;
import ru.humaninweb.prop.Prop;

/**
 * Сохранение запроса
 *
 * @author vit
 */
@Log4j2
@Component
public class ParseSave {

    @Autowired
    private Prop prop;

    @Autowired
    private ParseDao dao;

    @Autowired
    private ConcurrentLinkedQueue<ParseDto> parseQueue;

    /**
     * Сохранение файла запроса на диск, добавление ссылки в базу
     *
     * @param reqFile
     * @return При успехе идентификатор запроса иначе -1
     */
    public BigDecimal save(MultipartFile reqFile) {
        LocalDateTime reqTime = LocalDateTime.now();
        Path reqPath = saveToDisk(reqTime, reqFile);
        if (reqPath != null) {
            BigDecimal reqId = saveToBase(reqTime, reqPath);
            if (reqId.compareTo(BigDecimal.ZERO) > 0) {
                ParseDto dto = new ParseDto();
                dto.setReqId(reqId);
                dto.setReqLink(reqPath.toString());
                parseQueue.add(dto);
                return reqId;
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Сохранение ответа в базу
     *
     * @param dto
     */
    public void save(ParseDto dto) {
        dao.insertResp(dto);
    }

    /**
     * Сохранение на диск
     *
     * @param reqTime Время запроса
     * @param file Файл запроса
     * @return При успехе путь к сохранённому файлу иначе null
     */
    private Path saveToDisk(LocalDateTime reqTime, MultipartFile reqFile) {
        Path reqPath;
        try {
            reqPath = createPath(reqTime, prop.getReqDir(), reqFile.getOriginalFilename());
            if (reqPath.toFile().exists()) {
                log.error("FILE ALREADY EXIST {}", reqPath);
                return null;
            }
            reqFile.transferTo(reqPath);
            log.info("REQ PATH {}", reqPath);
        } catch (IOException | IllegalStateException ex) {
            log.error(ex);
            return null;
        }
        return reqPath;
    }

    /**
     * Создание полного пути к файлу
     *
     * @param root Корневая директория
     * @param filename Имя файла
     * @return Путь для сохранения файла
     */
    private Path createPath(LocalDateTime reqTime, Path root, String filename) throws IOException {
        String ld = reqTime.format(DateTimeFormatter.ISO_DATE);
        Path subRoot = Files.createDirectories(Paths.get(root.toString(), ld));
        return Paths.get(subRoot.toString(), filename);
    }

    /**
     * Сохранение в базу
     *
     * @param reqTime Время запроса
     * @param reqPath Путь к файлу
     * @return Идентификатор записи
     */
    private BigDecimal saveToBase(LocalDateTime reqTime, Path reqPath) {
        return dao.insertReq(reqTime, reqPath.toString());
    }

    /**
     * Создание пути файла ответа
     *
     * @param respTime Время ответа
     * @param reqPath Путь к файлу запроса
     * @param format Формат файла
     * @return Путь файла ответа
     * @throws IOException
     */
    public Path getRespPath(LocalDateTime respTime, String reqPath, String format) throws IOException {
        Path req = Paths.get(reqPath);
        String filename = new StringBuilder(req.getFileName().toString()).append(".").append(format).toString();
        return createPath(respTime, prop.getRespDir(), filename);
    }
}
