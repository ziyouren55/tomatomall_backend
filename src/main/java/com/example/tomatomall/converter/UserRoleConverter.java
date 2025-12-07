package com.example.tomatomall.converter;

import com.example.tomatomall.enums.UserRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * UserRole 枚举转换器
 * 用于处理数据库中可能存在的无效枚举值
 * 当遇到无效值时，自动转换为默认值 USER
 */
@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        if (attribute == null) {
            return UserRole.USER.name();
        }
        return attribute.name();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return UserRole.USER;
        }
        // 使用 UserRole.fromString 方法，它会处理无效值并返回默认值
        return UserRole.fromString(dbData);
    }
}

