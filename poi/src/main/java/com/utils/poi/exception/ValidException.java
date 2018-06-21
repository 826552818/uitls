package com.utils.poi.exception;


/**
 * <p>
 * <li>Description: 校验异常信息</li>
 * <li>@author: LiuDun </li>
 * <li>@date 2018-06-21 14:24:22</li>
 */
public class ValidException extends Exception {

    /**
     * <li> ValidException 的构造函数. </li>
     *
     * @param msg the msg
     */
    public ValidException ( String msg ) {

        super( msg );
    }
}
