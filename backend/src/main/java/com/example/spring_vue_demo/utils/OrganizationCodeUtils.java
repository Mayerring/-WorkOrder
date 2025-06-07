package com.example.spring_vue_demo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrganizationCodeUtils {
    // 字符集，用于生成随机主体标识码（数字和大写字母，去除I,O等易混淆字符）
    private static final String CHARSET = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    // 权重数组，标准GB 32100-2015定义的权重
    private static final int[] WEIGHTS = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};

    // 字符到数值映射
    private static final int[] CHAR_MAP = new int[128];
    static {
        for (int i = 0; i < CHAR_MAP.length; i++) CHAR_MAP[i] = -1;
        for (int i = 0; i < 10; i++) CHAR_MAP['0' + i] = i;
        for (int i = 0; i < 26; i++) CHAR_MAP['A' + i] = 10 + i;
    }

    private static final Random RANDOM = new Random();

    /**
     * 生成一个公司统一社会信用代码风格的编号 deptCode+orgTypeCode+regionCode+identifyCode
     * @return 18位统一社会信用代码风格的公司编号
     */
    public static String generateCompanyCode() {

        StringBuilder code = new StringBuilder();
        code.append("91440000");//表示广东的企业
        // 生成9位随机主体标识码（数字+大写字母）
        for (int i = 0; i < 9; i++) {
            code.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        // 计算校验码
        char checkCode = calculateCheckCode(code.toString());
        code.append(checkCode);

        return code.toString();
    }

    /**
     * 计算校验码
     * @param code 17位字符串，组成部分不包括校验码
     * @return 校验码字符
     */
    public static char calculateCheckCode(String code) {
        if (code == null || code.length() != 17) {
            throw new IllegalArgumentException("传入字符串必须为17位");
        }

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = code.charAt(i);
            int codeValue = CHAR_MAP[c];
            if (codeValue == -1) throw new IllegalArgumentException("非法字符: " + c);
            sum += codeValue * WEIGHTS[i];
        }

        int logicCheckCode = 31 - (sum % 31);
        if (logicCheckCode == 31) logicCheckCode = 0;

        for (int i = 0; i < CHARSET.length(); i++) {
            if (CHAR_MAP[CHARSET.charAt(i)] == logicCheckCode) {
                return CHARSET.charAt(i);
            }
        }

        throw new RuntimeException("无法计算校验码");
    }

    /**
     * 生成部门编号
     * 规则：D + 公司编号 + 四位流水号，例如 D4401-0001
     *
     * @param companyCode 公司编号，例如 "4401"
     * @param departmentCount 当前公司已有的部门数量，用于生成流水号
     * @return 部门编号
     */
    public static String generateDepartmentCode(String companyCode, int departmentCount) {
        int nextNumber = departmentCount + 1;
        return String.format("D%s-%04d", companyCode, nextNumber);
    }

    /**
     * 生成员工编号
     * 格式：E + 日期时间
     * 示例：E20250606T153045789-C0003-D0005
     *
     * @return 员工编号
     */
    public static String generateStaffCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
        String timestamp = sdf.format(new Date());
        return String.format("E%s", timestamp);
    }
}