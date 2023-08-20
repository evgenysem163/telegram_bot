package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    @Autowired
    private TelegramBot telegramBot;


    @Autowired
    pro.sky.telegrambot.listener.Scheduled scheduled;


    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if(update.message().text().equals("/start")){
                SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                        "Привет " + update.message().from().firstName() + " Добро пожаловать!");
                SendResponse sendResponse = telegramBot.execute(sendMessage);
            }
            else {
                String text = update.message().text();
                Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");
                Matcher matcher = pattern.matcher(text);
                if(matcher.matches()){
                    LocalDateTime date = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    String answer = matcher.group(3);
                    NotificationTask notificationTask = new NotificationTask();
                    notificationTask.setMessageText(answer);
                    notificationTask.setChatId(update.message().chat().id());
                    notificationTask.setLocalDateTime(date);
                    notificationTaskRepository.save(notificationTask);
                }

            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    @Scheduled(fixedRate = 6000)
    public void checkTask() {
        scheduled.runTasks()
                .forEach(f -> {
                    SendMessage message = new SendMessage(f.getChatId(), f.getMessageText());
                    SendResponse response = telegramBot.execute(message);
                });
    }
}
