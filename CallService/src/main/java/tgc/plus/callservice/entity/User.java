package tgc.plus.callservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

@Table(name = "user_communicate")
@Component
@NoArgsConstructor
@Getter
@ToString
public class User {

    @Id
    Long id;
    String userCode;
    String email;
    String phone;

    public User(String userCode, String email) {
        this.userCode = userCode;
        this.email = email;
    }
}
