package ru.humaninweb.ctrl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.humaninweb.comp.ParseResponse;
import ru.humaninweb.comp.ParseSave;
import ru.humaninweb.dao.ParseDao;

/**
 *
 * @author vit
 */
@Log4j2
@Controller
public class ParseController {

    @Autowired
    private ParseSave parseSave;

    @Autowired
    private ParseDao dao;

    @Autowired
    private ParseResponse parseResponse;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * GET /echo : Проверка доступности сервера
     *
     * @return
     */
    @GetMapping(value = "/echo")
    public ResponseEntity info() {
        return ResponseEntity.ok().body("СЕРВЕР РАЗБОРА ФАЙЛОВ");
    }

    /**
     * POST /upload : Загрузка файла для разбора
     *
     * @param file Файл (required)
     * @return Успешный ответ (status code 200) иначе 503
     *
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity echo(@RequestParam("file") MultipartFile file) {
        BigDecimal reqId = parseSave.save(file);
        if (reqId.compareTo(BigDecimal.ZERO) == 0) {
            String resp = parseResponse.create(BigDecimal.ZERO, "ОШИБКА СОХРАНЕНИЯ ФАЙЛА");
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("");
            return ResponseEntity.ok().contentType(MediaType.TEXT_XML).body(resp);
        } else {
            String resp = parseResponse.create(reqId);
            return ResponseEntity.ok().contentType(MediaType.TEXT_XML).body(resp);
        }
    }

    /**
     * GET /download : Получение результата разбора файла по идентификатору
     * загрузки
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/download", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity download(@RequestParam BigDecimal id) {
        List<String> respLink = dao.selectResp(id);
        if (!respLink.isEmpty()) {
            try {
                String resp = Files.newBufferedReader(Paths.get(respLink.stream().findFirst().get())).lines().collect(Collectors.joining(System.lineSeparator()));
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(resp);
            } catch (IOException ex) {
                log.error(ex);
            }
        }
        String resp = parseResponse.create(id, "ФАЙЛ С ТАКИМ ID НЕ НАЙДЕН");
        return ResponseEntity.ok().contentType(MediaType.TEXT_XML).body(resp);
    }
}
