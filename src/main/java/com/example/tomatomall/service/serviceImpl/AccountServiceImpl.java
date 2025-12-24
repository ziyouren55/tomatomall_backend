package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.MemberPoints;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.MemberPointsRepository;
import com.example.tomatomall.service.AccountService;
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

    @Autowired
    MemberPointsRepository memberPointsRepository;
    @Autowired
    private com.example.tomatomall.repository.SchoolVerificationRepository schoolVerificationRepository;
    @Autowired
    private com.example.tomatomall.repository.SchoolRepository schoolRepository;

    @Override
    public String register(AccountVO accountVO)
    {
        Optional<Account> account = accountRepository.findByUsername(accountVO.getUsername());
        if (account.isPresent()) {
            throw TomatoMallException.usernameAlreadyExists();
        }

        // 验证并规范化角色
        if (accountVO.getRole() == null || accountVO.getRole().trim().isEmpty()) {
            accountVO.setRole(UserRole.CUSTOMER.name()); // 默认角色
        } else {
            // 验证角色是否有效
            if (!UserRole.isValid(accountVO.getRole())) {
                throw TomatoMallException.invalidRole();
            }
        }

        String rawPassword = accountVO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Account newAccount = accountVO.toPO();
        newAccount.setCreateTime(new Date());
        newAccount.setPassword(encodedPassword);

        // 保证注册用户默认成为会员且绑定最低等级
        if (newAccount.getMemberLevelId() == null) {
            newAccount.setMemberLevelId(1);
        }
        newAccount.setIsMember(true);
        accountRepository.save(newAccount);

        // 初始化会员积分记录（确保当前等级为最低等级）
        MemberPoints points = new MemberPoints();
        points.setUserId(newAccount.getId());
        points.setCurrentLevelId(newAccount.getMemberLevelId());
        points.setCreateTime(new Date());
        points.setUpdateTime(new Date());
        memberPointsRepository.save(points);

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
    public AccountVO getUserById(Integer id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            return account.get().toVO();
        } else {
            throw TomatoMallException.usernameNotFind();
        }
    }
    @Override
    public com.example.tomatomall.vo.accounts.UserSchoolVO getUserSchool(String username) {
        AccountVO base = getUser(username);
        Integer userId = base.getId();
        return buildUserSchoolVOByUserId(userId);
    }

    @Override
    public com.example.tomatomall.vo.accounts.UserSchoolVO getUserSchoolById(Integer id) {
        return buildUserSchoolVOByUserId(id);
    }

    // helper to build UserSchoolVO from userId
    private com.example.tomatomall.vo.accounts.UserSchoolVO buildUserSchoolVOByUserId(Integer userId) {
        com.example.tomatomall.vo.accounts.UserSchoolVO vo = new com.example.tomatomall.vo.accounts.UserSchoolVO();
        java.util.Optional<com.example.tomatomall.po.SchoolVerification> svOpt = schoolVerificationRepository.findByUserId(userId);
        if (svOpt.isPresent()) {
            com.example.tomatomall.po.SchoolVerification sv = svOpt.get();
            boolean certified = sv.getStatus() != null && "VERIFIED".equalsIgnoreCase(sv.getStatus());
            vo.setSchoolCertified(certified);
            vo.setSchoolName(sv.getSchoolName());
            if (certified && sv.getSchoolName() != null) {
                try {
                    org.springframework.data.domain.Page<com.example.tomatomall.po.School> sp = schoolRepository.findByNameContainingIgnoreCase(sv.getSchoolName(), org.springframework.data.domain.PageRequest.of(0,1));
                    if (sp != null && sp.hasContent()) {
                        com.example.tomatomall.po.School s = sp.getContent().get(0);
                        vo.setSchoolCode(s.getCode());
                        vo.setCityCode(s.getCityCode());
                        vo.setCityName(s.getCityName());
                    }
                } catch (Exception ignored) {}
            }
        } else {
            vo.setSchoolCertified(false);
        }
        return vo;
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

        // 单独处理 role 字段，确保使用枚举转换
        if(accountVO.getRole() != null) {
            account.setRole(UserRole.fromString(accountVO.getRole()));
        }

        if(accountVO.getPassword() != null)
            account.setPassword(passwordEncoder.encode(account.getPassword()));

        accountRepository.save(account);

        return account.toVO();
    }
}
