package tgc.plus.authservice.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.admin_dto.ChangeAccountDTO;
import tgc.plus.authservice.dto.admin_dto.GetUsersByDateDTO;
import tgc.plus.authservice.dto.admin_dto.UserResponseDTO;
import tgc.plus.authservice.facades.AdminFacade;

import java.util.List;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminController {

    //удаление аккаунта
    //изменение параметров аккаунта
    //ручной разлогин всех
    //получение всех пользователей
    //получение токенов для пользователя

    //написать аннотации для тел


    //проверить баны!

    @Autowired
    private AdminFacade adminFacade;

    @GetMapping("/roles")
    public Mono<List<String>> promotionToAdmin(){
        return adminFacade.getSystemRoles();
    }

    @PatchMapping("/account/change")
    public Mono<Void> changeAccount(@RequestBody ChangeAccountDTO changeAccountDTO){
        return adminFacade.changeAccount(changeAccountDTO);
    }

    @DeleteMapping("/account/{userCode}/tokens")
    public Mono<Void> closeAllActiveSessions(@PathVariable("userCode") String userCode){
        return adminFacade.closeAccountSessions(userCode);
    }

    @GetMapping("/account/all")
    public Mono<List<UserResponseDTO>> getAllUsers(@Valid @ModelAttribute GetUsersByDateDTO getUsersByDateDTO){
        return adminFacade.getUsersByDateAndLimit(getUsersByDateDTO);
    }

    @GetMapping("/account/{email}")
    public Mono<UserResponseDTO> getAllUsers(@ModelAttribute @PathVariable("email") String email){
        return adminFacade.getUserByEmail(email);
    }

}
