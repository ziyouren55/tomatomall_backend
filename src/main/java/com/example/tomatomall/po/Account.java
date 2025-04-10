package com.example.tomatomall.po;

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

    @Basic
    @Column(name = "role")
    private String role;

    @Basic
    @Column(name = "telephone")
    private String telephone;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "location")
    private String location;

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
        accountVO.setRole(role);
        accountVO.setTelephone(telephone);
        accountVO.setEmail(email);
        accountVO.setLocation(location);

        return accountVO;
    }

}
