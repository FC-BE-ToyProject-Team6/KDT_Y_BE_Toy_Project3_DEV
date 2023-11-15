package com.fastcampus.toyproject.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.fastcampus.toyproject.domain.user.entity.Authority;
import com.fastcampus.toyproject.domain.user.entity.User;
import com.fastcampus.toyproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
//내장 DB에 테스트를 진행 하기 위에 아래와 같은 어노테이션을 작성해 줍니다.
//application.yml의 외장 mysql이 아닌 내장 h2 db를 사용하게 됩니다.
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {

        user = User.builder()
            .password("testpassword")
            .name("테스트")
            .authority(Authority.ROLE_USER)
            .email("test@test.com")
            .build();

    }

    @Test
    @DisplayName("회원 저장")
    public void save() {

        //given
        //System.out.println(user);
        //User(userId=null, email=test@test.com, password=testpassword, name=테스트, authority=ROLE_USER,
        //저장전의 user는 UserId가 null입니다.

        //when
        User savedUser = userRepository.save(user);

        //System.out.println(savedUser);
        //User(userId=6, email=test@test.com, password=testpassword, name=테스트, authority=ROLE_USER
        //저장 후의 userId 6이 생성 되었습니다!
        //JPA가 user객체에 userId를 넣어준것으로 정상 작동함을 알 수 있습니다.
        //then
        assertThat(user).isEqualTo(savedUser)
            .extracting("userId").isNotNull();


    }

}
