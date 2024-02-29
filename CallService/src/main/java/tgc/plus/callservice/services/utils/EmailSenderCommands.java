package tgc.plus.callservice.services.utils;

import lombok.Getter;

@Getter
public enum EmailSenderCommands {

    SEND_NEW_USER("send_user_cr", "Аккаунт успешно создан!", "tiger_create_user_template.ftl"),

    SEND_NEW_VIRTUAL_MACHINE("send_vm_cr", "Данные новой виртуальной машины", "tiger_create_vm_template.ftl"),

    SEND_RECOVERY_CODE("send_rest", "Изменение пароля", "tiger_restore_password_template.ftl"),

    SEND_DATA_EXPIRE_VIRTUAL_MACHINE("send_vm_ex", "Срок предоставляемой услуги истек!", "tiger_error_vm_template.ftl"),

    SEND_DATA_WARNING_VIRTUAL_MACHINE("send_vm_wn", "Срок предоставляемой услуги скоро истечет!", "tiger_warning_vm_template.ftl");

    private final String name;
    private final String subject;
    private final String template;

    EmailSenderCommands(String name, String subject, String template) {
        this.name = name;
        this.subject = subject;
        this.template = template;
    }

}
