package com.example.tomatomall.po;

import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.vo.accounts.AccountVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "username")
    private String username;

    @Basic
    @Column(name = "password")
    private String password;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "avatar")
    private String avatar;

    @Convert(converter = com.example.tomatomall.converter.UserRoleConverter.class)
    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'USER'")
    private UserRole role = UserRole.USER;

    @Basic
    @Column(name = "telephone")
    private String telephone;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "location")
    private String location;

    // 在Account类中添加会员等级关联
    @Column(name = "member_level_id")
    private Integer memberLevelId = 1; // 默认为最低会员等级

    @Column(name = "is_member", nullable = false)
    private Boolean isMember = false;

    @Basic
    @Column(name = "create_time")
    private Date createTime;

    public AccountVO toVO()
    {
        AccountVO accountVO = new AccountVO();
        accountVO.setId(id);
        accountVO.setUsername(username);
        accountVO.setPassword(password);
        accountVO.setName(name);
        accountVO.setAvatar(avatar);
        accountVO.setRole(role != null ? role.name() : UserRole.USER.name());
        accountVO.setTelephone(telephone);
        accountVO.setEmail(email);
        accountVO.setLocation(location);

        return accountVO;
    }

}
