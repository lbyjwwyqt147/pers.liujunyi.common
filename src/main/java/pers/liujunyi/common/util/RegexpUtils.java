package pers.liujunyi.common.util;

/***
 * 正则表达式
 *
 * @author Administrator
 */
public class RegexpUtils {

    public static  final String ALNUM_CODE_REGEXP = "^(?!_)(?!.*?_$)[a-zA-Z0-9-_]+$";
    public static  final String ALNUM_CODE_MSG = "只能输入数字、字母、下划线,不能以下划线开头和结尾";



    public static  final String ALNUM_NAME_REGEXP = "^(?!_)(?!.*?_$)[a-zA-Z0-9-._#\\u4e00-\\u9fa5]+$";
    public static  final String ALNUM_NAME_MSG = "只能输入汉字、数字、字母、下划线,不能以下划线开头和结尾";

    public static  final String ILLEGITMACY_REGEXP = "^(?!_)(?!.*?_$)[a-zA-Z0-9-_.@#%$*()={}\"\"?<>, \\u4e00-\\u9fa5]+$";
    public static  final String ILLEGITMACY_MSG = "存在非法输入字符,请检查输入法是否正确";


    public static  final String HTML_LABEL_REGEXP = "/<(.*)>.*<\\/\\1>|<(.*) \\/>/";
    public static  final String HTML_LABEL_MSG = "存在html标签,这是不符合规定的";

    public static  final String FIGURE_REGEXP = "^[0-9]*$";
    public static  final String FIGURE_MSG = "只能输入数字";

    public static  final String TWO_DECIMAL_PLACES_REGEXP = "^[0-9]+(.[0-9]{2})?$";
    public static  final String TWO_DECIMAL_PLACES_MSG = "只能输入有两位小数的正实数";


    public static  final String POSITIVE_INTEGER_REGEXP = "^\\+?[1-9][0-9]*$";
    public static  final String POSITIVE_INTEGER_MSG = "只能输入非零的正整数";

    public static  final String NEGATIVE_INTEGER_REGEXP = "^\\-?[1-9][0-9]*$";
    public static  final String NEGATIVE_INTEGER_MSG = "只能输入非零的负整数";

    public static  final String ENGLISH_LETTER_REGEXP = "^[A-Za-z]+$";
    public static  final String ENGLISH_LETTER_MSG = "只能输入英文字母";

    public static  final String DIGITAL_ALPHABET_REGEXP = "^[A-Za-z0-9]+$";
    public static  final String DIGITAL_ALPHABET_MSG = "只能输入由数字和英文字母";

    public static  final String DIGITALALPHABETLINE_REGEXP = "^[A-Za-z0-9]+$";
    public static  final String DIGITALALPHABETLINE_MSG = "只能输入由数字和英文字母下划线";

    public static  final String HANZI_REGEXP = "^[\\u4e00-\\u9fa5]{0,}";
    public static  final String HANZI_MSG = "只能输入汉字";

    public static  final String URL_REGEXP = "^http://%28[/\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
    public static  final String URL_MSG = "url 格式错误";

    public static  final String TEL_REGEXP = "^(\\(\\d{3,4}-)|\\d{3.4}-)?\\d{7,8}";
    public static  final String TEL_MSG = "电话号码格式错误";

    public static  final String MOBILE_PHONE_REGEXP = "^1[0-9]{10}$";
    public static  final String MOBILE_PHONE_MSG = "手机号码格式错误";

    public static  final String IDENTIFICATIONCARD_REGEXP = "^\\d{15}|\\d{18}$";
    public static  final String IDENTIFICATIONCARD_MSG = "身份证格式错误";
}
