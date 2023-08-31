package com.garcheng.gulimall.common.constant;

public class PurchaseConstant {

    public enum PurchaseStatusEnum {

        NEW_CREATE(0,"新创建"),

        BE_ASSIGNED(1,"已分配"),

        BE_HANDLE(2,"处理中"),

        BE_FINISH(3,"已完成"),

        HAVE_EXCEPTION(4,"有异常");


        private int code;

        private String message;

        PurchaseStatusEnum(int code,String message){
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum PurchaseDetailStatusEnum {

        NEW_CREATE(0,"新建"),

        BE_ASSIGNED(1,"已分配"),

        BE_HANDLE(2,"正在采购"),

        BE_FINISH(3,"已完成"),

        HAVE_EXCEPTION(4,"采购失败");


        private int code;

        private String message;

        PurchaseDetailStatusEnum(int code,String message){
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
