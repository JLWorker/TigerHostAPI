package tgc.plus.callservice.entity;

//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

@Table(name = "user_communicate")
@Component
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    Long id;

    @Column(value = "user_code")
//    @NotEmpty(message = "UserCode field must`t be null")
    String userCode;

    @Column(value = "email")
//    @Email
    String email;

    @Column(value = "phone")
//    @Pattern(regexp = "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$",
//    message = "Incorrect phone format")
    String phone;

    public User(String userCode, String email) {
        this.userCode = userCode;
        this.email = email;
    }
}
