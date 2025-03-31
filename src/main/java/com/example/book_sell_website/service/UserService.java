package com.example.book_sell_website.service;


import com.example.book_sell_website.dto.Login_logout_register.MoreRegisterDTO;
import com.example.book_sell_website.dto.Login_logout_register.ResponseLogInDTO;
import com.example.book_sell_website.dto.Login_logout_register.logInDTO;
import com.example.book_sell_website.dto.Login_logout_register.registerDTO;
import com.example.book_sell_website.dto.Login_logout_register.registerResponseDTO;
import com.example.book_sell_website.entity.User;
import com.example.book_sell_website.entity.pendingUser;
import com.example.book_sell_website.exception.AppException;
import com.example.book_sell_website.exception.ErrorCode;
import com.example.book_sell_website.repository.UserRepo.UserPendingRepository;
import com.example.book_sell_website.repository.UserRepo.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserPendingRepository userPendingRepository;

    @Autowired
    private EntityManager entityManager;

    public String generateRandomNumber() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // Tạo số ngẫu nhiên từ 0-9
        }
        return code.toString();
    }

    // request body
    // kiểm tra nếu user đăng ký có tồn tại trong bảng user phụ không
    // tồn tại thì lưu vào bảng user phụ, và trả về true
    public boolean isExistUser(registerDTO user)
    {
        // nếu không tồn tại thì lưu vào bảng phụ và trả về false
        if (userPendingRepository.findUserByMail(user.getMail()) == null && userPendingRepository.findUserByPhone(user.getPhone()) == null)
        {
            pendingUser newUser = new pendingUser();
            newUser.setMail(user.getMail());
            newUser.setName(user.getName());
            newUser.setPhone(user.getPhone());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            String code = generateRandomNumber();
            newUser.setCode(code);
            userPendingRepository.save(newUser);
            mailService.sendMail(user.getMail(), "Verify your email", code + " is your code to register, please don't share with anyone else");
            return false;
        }
        // nếu tồn tại và chưa kích hoạt thì trả về true
//        if((userPendingRepository.findUserByMail(user.getMail()) != null || userPendingRepository.findUserByPhone(user.getPhone()) != null) && !user.isStatus())
//        {
//            entityManager.merge(user.getID());
//            return true;
//        }
        return true;
    }

    // verify
    public registerResponseDTO verify(String mail, String code)
    {
        pendingUser user = userPendingRepository.findUserByMail(mail);  
//        System.out.println(user);
//        pendingUser user = findByMail(mail);
        registerResponseDTO result = new registerResponseDTO();
        if (user == null) {
            result.setStatus(false);
            result.setMessage("Email not found");
            return result;
        }
        if (!user.isStatus())
        {
            System.out.println(code);
            System.out.println(user.getCode());
            if (code.equals(user.getCode()))
            {
                User user1 = new User();
                user1.setPassword(user.getPassword());
                user1.setMembershipLevel(User.MembershipLevel.Silver);
                user1.setName(user.getName());
                user1.setMail(user.getMail());
                user1.setPhone(user.getPhone());
                userRepository.save(user1);
                result.setMessage("Successfully register!");
                result.setStatus(true);
                user.setStatus(true);
                user.setCode(null);
                userPendingRepository.save(user);
                return result;
            }else{
                result.setMessage("Wrong code!");
                result.setStatus(false);
                return result;
            }
        }else {
            result.setMessage("Your account is enable! Log in now!");
            result.setStatus(false);
            return result;
        }

    }

    // log in
    public ResponseLogInDTO logIn(logInDTO infor) {
        ResponseLogInDTO result = new ResponseLogInDTO();

        // Kiểm tra đầu vào
        if (infor == null || (infor.getPhone() == null && infor.getMail() == null)) {
            result.setMessage("Phone or email is required!");
            result.setStatus(false);
            return result;
        }

        // Tìm user theo phone hoặc mail
        User user = null;
        if (infor.getPhone() != null) {
            user = userRepository.findUserByPhone(infor.getPhone()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
        if (user == null && infor.getMail() != null) {
            user = userRepository.findUserByMail(infor.getMail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }

        // Kiểm tra tài khoản tồn tại
        if (user == null) {
            result.setMessage("This account doesn't exist!");
            result.setStatus(false);
            return result;
        }

        // Kiểm tra mật khẩu
        if (passwordEncoder.matches(infor.getPassword(), user.getPassword())) {
            result.setUser_id(user.getID());
            result.setMessage("Successfully log in!");
            result.setStatus(true);
            user.setIs_login(true);
        } else {
            result.setMessage("Wrong password!");
            result.setStatus(false);
        }

        return result;
    }
    // update infor
    public boolean updateUserDetails(int userId, MoreRegisterDTO moreRegisterDTO) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFull_name(moreRegisterDTO.getFull_name());
            user.setAddress(moreRegisterDTO.getAddress());
            userRepository.save(user);
            return true;
        }
        return false;
        }
        public User updateUserInfo(Integer userId, MoreRegisterDTO moreRegisterDTO) {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setFull_name(moreRegisterDTO.getFull_name());
                user.setAddress(moreRegisterDTO.getAddress());
                return userRepository.save(user);
            } else {
                throw new RuntimeException("User not found!");
            }
        }

    // update balance
        public User updateBalance(int userId, double money) {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setBalance(user.getBalance() + money/10000);
                return userRepository.save(user);
            } else {
                throw new RuntimeException("User not found!");
            }
        }

    // log out
    public boolean logOut(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIs_login(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    
}