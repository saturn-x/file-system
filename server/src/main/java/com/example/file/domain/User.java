package com.example.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
@JsonIgnoreProperties({"password"})
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    /**
     * 创建时间
     */
    public String nickname;
    private String createTime;


}
