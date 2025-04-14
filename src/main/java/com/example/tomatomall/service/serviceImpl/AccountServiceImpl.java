package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.accounts.AccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    CartRepository cartRepository;

    @Override
    public String register(AccountVO accountVO)
    {
        Optional<Account> account = accountRepository.findByUsername(accountVO.getUsername());
        if (account.isPresent()) {
            throw TomatoMallException.usernameAlreadyExists();
        }

        String rawPassword = accountVO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Account newAccount = accountVO.toPO();
        newAccount.setCreateTime(new Date());
        newAccount.setPassword(encodedPassword);
        accountRepository.save(newAccount);

        return "注册成功";
    }

    @Override
    public String login(AccountVO accountVO)
    {
        Optional<Account> account = accountRepository.findByUsername(accountVO.getUsername());
        if (account.isPresent()) {
            // 使用 passwordEncoder 比较原始密码和数据库中加密后的密码
           if(passwordEncoder.matches(accountVO.getPassword(), account.get().getPassword()))
               return tokenUtil.getToken(account.get());
           else
               throw  TomatoMallException.passwordError();
        }
        else
            throw TomatoMallException.usernameNotFind();
    }

    @Override
    public AccountVO getUser(String username)
    {
        Optional<Account> account = accountRepository.findByUsername(username);
        if(account.isPresent())
            return account.get().toVO();
        else
            throw  TomatoMallException.usernameNotFind();
    }

    @Override
    public AccountVO updateUser(AccountVO accountVO)
    {
        Optional<Account> opAccount = accountRepository.findByUsername(accountVO.getUsername());
        if(!opAccount.isPresent())
            throw TomatoMallException.usernameNotFind();
        if(accountVO.getUsername() == null)
        {
            throw TomatoMallException.lackOfUsername();
        }
        Account account = opAccount.get();
        account.setUsername(accountVO.getUsername());

        BeanUtils.copyProperties(accountVO,account, MyBeanUtil.getNullPropertyNames(accountVO));

        if(accountVO.getPassword() != null)
            account.setPassword(passwordEncoder.encode(account.getPassword()));

        accountRepository.save(account);

        return account.toVO();
    }
}
