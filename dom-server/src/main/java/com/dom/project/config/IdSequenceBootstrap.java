package com.dom.project.config;

import com.dom.project.mapper.IdSequenceMapper;
import com.dom.project.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 起動時に DB 上の当日最大連番を IdGenerator に同期する。
 */
@Component
public class IdSequenceBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IdSequenceBootstrap.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final IdSequenceMapper idSequenceMapper;

    public IdSequenceBootstrap(IdSequenceMapper idSequenceMapper) {
        this.idSequenceMapper = idSequenceMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        String today = LocalDate.now().format(DATE_FORMAT);
        Integer maxSequence = idSequenceMapper.selectMaxSequenceForDate(today);
        if (maxSequence != null && maxSequence > 0) {
            IdGenerator.ensureMinSequence(maxSequence);
            log.info("業務 ID 連番を DB と同期しました: date={}, maxSequence={}", today, maxSequence);
        }
    }
}
