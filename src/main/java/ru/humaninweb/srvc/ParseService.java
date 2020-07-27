package ru.humaninweb.srvc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLStreamException;
import lombok.extern.log4j.Log4j2;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.humaninweb.comp.ParseSave;
import ru.humaninweb.dao.ParseDao;
import ru.humaninweb.dto.ParseDto;

/**
 * Разбор файла, запись результата в базу.
 *
 * @author vit
 */
@Log4j2
@Service
public class ParseService {

    @Autowired
    private ParseDao dao;

    @Autowired
    private ParseSave parseSave;

    @Autowired
    private ConcurrentLinkedQueue<ParseDto> parseQueue;

    /**
     * Разделитель разделов
     */
    private final Pattern pattern = Pattern.compile("^(#+).");

    /**
     * Проверка файлов в очереди
     */
    @Scheduled(fixedRate = 5000)
    public void checkQueue() {
        parse(parseQueue.poll(), "QUEUE");
    }

    /**
     * Проверка файлов в базе
     */
    @Scheduled(fixedRate = 10000)
    public void checkBase() {
        dao.selectReq().forEach(dto -> {
            if (!parseQueue.contains(dto)) {
                parse(dto, "BASE");
            }
        });
    }

    /**
     * Разбор файла, формирование и запись xml, запись ссылки в базу
     *
     * @param dto
     * @param src
     */
    private void parse(ParseDto dto, String src) {
        if (dto != null) {
            log.info("START PARSE ID {} PATH {} SRC {}", dto.getReqId(), dto.getReqLink(), src);
            try {
                LocalDateTime respTime = LocalDateTime.now();
                Path respPath = parseSave.getRespPath(respTime, dto.getReqLink(), "xml");

                BufferedWriter writer = Files.newBufferedWriter(respPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
                XMLOutputFactory2 of = (XMLOutputFactory2) XMLOutputFactory2.newInstance();
                XMLStreamWriter2 wr = of.createXMLStreamWriter(writer, StandardCharsets.UTF_8.name());

                wr.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");

                Deque<Integer> stack = new ArrayDeque<>();
                StringBuilder sb = new StringBuilder();

                Files.readAllLines(Paths.get(dto.getReqLink())).forEach(line -> {
                    try {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            if (sb.length() > 0) {
                                wr.writeCharacters(sb.toString());
                                wr.writeEndElement();
                                sb.setLength(0);
                                stack.pop();
                            }
                            Integer partLength = matcher.group(1).length();
                            if (!stack.isEmpty() && partLength.compareTo(stack.peek()) <= 0) {
                                while (!stack.isEmpty()) {
                                    Integer pop = stack.pop();
                                    wr.writeEndElement();
                                    if (partLength.compareTo(pop) == 0) {
                                        break;
                                    }
                                }
                            }
                            stack.push(matcher.group(1).length());
                            wr.writeStartElement(line.replaceAll("#", "").replaceAll(" ", ""));
                        } else {
                            sb.append(line);
                        }

                    } catch (XMLStreamException ex) {
                        log.error(ex);
                    }
                });

                if (sb.length() > 0) {
                    wr.writeCharacters(sb.toString());
                    wr.writeEndElement();
                    sb.setLength(0);
                }

                wr.flush();
                wr.close();

                if (respPath.toFile().canRead()) {
                    dto.setRespTime(respTime);
                    dto.setRespLink(respPath.toString());
                    parseSave.save(dto);
                }

                log.info("END PARSE ID {} PATH {} SRC {}", dto.getReqId(), respPath, src);
            } catch (IOException | XMLStreamException ex) {
                log.error(ex);
            }
        }
    }
}