package com.wb.service;

import com.wb.dto.UserDTO;
import com.wb.dto.UserLoginDTO;
import com.wb.entity.User;

public interface UserService {

    /**
     * 网页登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    void save(UserDTO userDTO);
}
