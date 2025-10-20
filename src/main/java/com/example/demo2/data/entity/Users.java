package com.example.demo2.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id; // ✨ この行を追加

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = "password") // 自動生成されるtoStringにpasswordを出力しない
public class Users {
	@Id // これでエラーが解消されます
	public String userName;
	public String password;
	public String name;
	public String roleName;
}