package com.wb.controller.user;

import com.wb.constant.JwtClaimsConstant;
import com.wb.dto.UserDTO;
import com.wb.dto.UserLoginDTO;
import com.wb.entity.User;
import com.wb.properties.JwtProperties;
import com.wb.result.Result;
import com.wb.service.UserService;
import com.wb.utils.JwtUtil;
import com.wb.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 顾客登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("顾客登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("顾客登录,用户名：{}",userLoginDTO.getUsername());
        //顾客登录
        User user = userService.login(userLoginDTO);

        //为网页端的顾客用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }

    /**
     * 新增员工
     * @param userDTO
     * @return Result
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody UserDTO userDTO){
        log.info("新增员工：{}",userDTO);
        userService.save(userDTO);
        return Result.success();
    }
}
