package tgc.plus.authservice.api;

import tgc.plus.authservice.dto.DeviceTokens;
import tgc.plus.authservice.entity.User;
//import auth.tgc.plus.authservice.producer.MailProducer;
import tgc.plus.authservice.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class MobileController {

    @Autowired
    UserRepository userRepository;


    Logger logger = Logger.getLogger(this.getClass().getName());

    @JsonView(DeviceTokens.AllView.class)
    @PostMapping("/registration")
    public DeviceTokens registration(){
        logger.info("Start");
        Mono<User> user = userRepository
                .save(new User(12342, false, "email", "123131",
                        true, "user", null, null));

        user.doOnNext(elem -> {
            logger.info(elem.getEmail());
            logger.warning("Element by id - " + elem.getId() + " will be delete!");
        }).subscribe();


        return new DeviceTokens("ads", "asd");

    }

    @GetMapping("/user")
    public Mono<User> getUser(){
        logger.info("get user");
        return userRepository.getUserById(1);
    }

//    @GetMapping("/sendMessage")
//    public void sendMssg(){
//        mailProducer.sendMessageToTopic();
//    }

}
