package com.example.tomatomall.vo.accounts;

import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.po.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AccountVO
{

    private Integer id;
    private String username;
    private String password;
    private String name;
    private String avatar;
    private String role;
    private String telephone;
    private String email;
    private Integer memberLevelId;
    private Boolean isMember;
    private String location;

    /**
     * 获取角色枚举值
     */
    public UserRole getRoleEnum() {
        return UserRole.fromString(role);
    }

    public Account toPO()
    {
        Account account = new Account();
        account.setId(id);
        account.setUsername(username);
        account.setPassword(password);
        account.setName(name);
        account.setAvatar(avatar);
        // 将字符串角色转换为枚举，如果为空则使用默认值
        account.setRole(role != null ? UserRole.fromString(role) : UserRole.USER);
        account.setTelephone(telephone);
        account.setEmail(email);
        account.setLocation(location);
        account.setMemberLevelId(memberLevelId);
        account.setIsMember(isMember);

        return account;
    }

}
