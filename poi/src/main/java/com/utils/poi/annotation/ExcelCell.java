package com.utils.poi.annotation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * <li>Description: excel解析注解</li>
 * <li>@author: LiuDun </li>
 * <li>@date 2018-06-21 14:47:43</li>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelCell {
    
    /**
     * 顺序 default 100
     *
     * @return
     */
    public int index() default 100;
    
    /**
     * 当值为null时要显示的值 default StringUtils.EMPTY
     *
     * @return
     */
    public String defaultValue() default StringUtils.EMPTY;
    
    /**
     * 用于验证
     *
     * @return
     */
    public Valid valid() default @Valid();
    
    /**
     * <p>
     * <li>Description: TODO</li>
     * <li>@author: LiuDun </li>
     * <li>@date 2018-06-21 14:47:43</li>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Valid {
        
        /**
         * 必须与in中String相符,目前仅支持String类型
         *
         * @return e.g. {"abc","123"}
         */
        public String[] in() default {};
        
        /**
         * 是否为时间格式
         *
         * @return
         */
        public boolean isDate() default false;
        
        /**
         * 是否允许为空,用于验证数据 default true
         *
         * @return
         */
        public boolean allowNull() default false;
        
        /**
         * Apply a "greater than" constraint to the named property , equivalent ">"
         *
         * @return
         */
        public double gt() default Double.NaN;
        
        /**
         * Apply a "less than" constraint to the named property , equivalent "<"
         *
         * @return
         */
        public double lt() default Double.NaN;
        
        /**
         * Apply a "greater than or equal" constraint to the named property , equivalent ">="
         *
         * @return
         */
        public double ge() default Double.NaN;
        
        /**
         * Apply a "less than or equal" constraint to the named property , equivalent "<="
         *
         * @return
         */
        public double le() default Double.NaN;
        
        /**
         * Apply regular expression
         *
         * @return
         */
        public String regex() default StringUtils.EMPTY;
        
    }
}
