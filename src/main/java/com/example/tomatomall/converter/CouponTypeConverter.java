package com.example.tomatomall.converter;

import com.example.tomatomall.enums.CouponType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

/**
 * CouponType 枚举转换器
 * 用于处理数据库中可能存在的大小写不一致的枚举值
 * 支持大小写兼容的转换，当遇到无效值时自动转换为默认值 REPEAT
 */
@Converter(autoApply = true)
public class CouponTypeConverter implements AttributeConverter<CouponType, String> {

    @Override
    public String convertToDatabaseColumn(CouponType attribute) {
        return Objects.requireNonNullElse(attribute, CouponType.REPEAT).name();
    }

    @Override
    public CouponType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return CouponType.REPEAT;
        }
        // 使用 CouponType.fromValue 方法，它会处理大小写转换和无效值
        return CouponType.fromValue(dbData);
    }
}
