package pro.sky.telegrambot.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Scheduled {

    @Autowired
    NotificationTaskRepository notificationTaskRepository;

    public List<NotificationTask> runTasks() {
        return notificationTaskRepository.findAll().stream()
                .filter(f-> f.getLocalDateTime().equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                .collect(Collectors.toList());
    }


}
