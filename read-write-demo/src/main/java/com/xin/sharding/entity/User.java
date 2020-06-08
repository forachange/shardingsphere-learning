package com.xin.sharding.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable {
    
    private static final long serialVersionUID = 263434701950670170L;
    
    private int userId;
    
    private String userName;
    
    private String userNamePlain;
    
    private String pwd;
    
    private String assistedQueryPwd;
}