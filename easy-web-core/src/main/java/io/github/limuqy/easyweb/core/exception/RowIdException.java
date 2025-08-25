package io.github.limuqy.easyweb.core.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RowIdException extends RuntimeException {

    private HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
    private String message;

    public RowIdException(HttpStatus code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public RowIdException(HttpStatus code) {
        super(code.getReasonPhrase());
        this.code = code;
    }

    public RowIdException(String message) {
        super(message);
        this.message = message;
    }

    public void setMessage(String message, Object... params) {
        this.message = String.format(message, params);
    }

    public void mappingErr(String patch, Class<?> clazz, String fieldName) {
        this.message = String.format("%s mapping to: %s[%s]，%s", patch, clazz.getSimpleName(), fieldName, this.message);
    }
}
