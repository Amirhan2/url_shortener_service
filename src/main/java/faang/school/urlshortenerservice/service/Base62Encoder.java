package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Stack;

@Component
public class Base62Encoder {
    public final static String SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::numberAsEncodedString)
                .toList();
    }

    public String numberAsEncodedString(Long number) {
        int length = SYMBOLS.length();
        int ostatok;
        Stack<String> stack = new Stack<>();
        StringBuilder result = new StringBuilder();
        do {
            ostatok = (int) (number % length);
            stack.push(String.valueOf(SYMBOLS.charAt(ostatok)));
            number = number / length;
        } while (number != 0);

        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }
        return result.toString();
    }
}
