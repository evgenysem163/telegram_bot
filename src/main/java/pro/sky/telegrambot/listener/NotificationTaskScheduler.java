package pro.sky.telegrambot.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class NotificationTaskScheduler {

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBotService telegramBotService;

    public NotificationTaskScheduler(NotificationTaskRepository notificationTaskRepository, TelegramBotService telegramBotService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBotService = telegramBotService;
    }


    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1)
    @Transactional
    public void task() {
        notificationTaskRepository.findAllByLocalDateTime(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        ).forEach(notificationTask -> {
            telegramBotService.sendMessage(
                    notificationTask.getChatId(),
                    "Вы просили напомнить о сообщении: " + notificationTask.getMessageText()
            );
            notificationTaskRepository.delete(notificationTask);
        });
    }


}
