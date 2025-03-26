package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    TokenUtil tokenUtil;



    @Override
    public Boolean register(AccountVO accountVO)
    {
        Account account = accountRepository.findByUsername(accountVO.getUsername());
        if (account != null) {
            throw TomatoMallException.usernameAlreadyExists();
        }

        String rawPassword = accountVO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Account newAccount = accountVO.toPO();
        newAccount.setCreateTime(new Date());
        newAccount.setPassword(encodedPassword);
        accountRepository.save(newAccount);
        return true;
    }

    @Override
    public String login(String username, String password)
    {
        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            // 使用 passwordEncoder 比较原始密码和数据库中加密后的密码
           if(passwordEncoder.matches(password, account.getPassword()))
               return tokenUtil.getToken(account);
           else
               throw  TomatoMallException.passwordError();
        }
        else
            throw TomatoMallException.usernameNotFind();
    }

    @Override
    public AccountVO getUser(String username)
    {
        Account account = accountRepository.findByUsername(username);
        if(account != null)
            return account.toVO();
        else
            throw  TomatoMallException.usernameNotFind();
    }

    @Override
    public Boolean updateUser(AccountVO accountVO)
    {
        Account account = accountRepository.findByUsername(accountVO.getUsername());
        if(account == null)
            throw TomatoMallException.usernameNotFind();
        if(accountVO.getUsername() == null)
        {
            throw TomatoMallException.lackOfUsername();
        }
        account.setUsername(accountVO.getUsername());

        BeanUtils.copyProperties(accountVO,account, MyBeanUtil.getNullPropertyNames(accountVO));

        return true;
    }
}
