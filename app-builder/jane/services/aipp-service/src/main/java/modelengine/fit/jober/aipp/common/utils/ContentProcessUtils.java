package modelengine.fit.jober.aipp.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentProcessUtils {
    private static final String REASONING_REGEX = "<think>(.*?)</think>";

    public static String filterReasoningContent(String rawContent) {
        // todo 是否处理<think>标签不完整的情况
        Pattern pattern = Pattern.compile(REASONING_REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(rawContent);

        // 用空字符串替换掉所有匹配到的think内容
        String cleanedText = matcher.replaceAll("");

        return cleanedText.trim();
    }
}
