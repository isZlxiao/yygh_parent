package com.atguigu.yygh.cmn.test;


import com.alibaba.excel.EasyExcel;

public class ReadTest {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\79188\\Desktop\\a.xlsx";
        EasyExcel.read(filePath,Stu.class,new ExcelListener()).sheet().doRead();
    }

}
