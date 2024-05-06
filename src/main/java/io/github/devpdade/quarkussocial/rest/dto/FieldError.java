package io.github.devpdade.quarkussocial.rest.dto;

//Classe para mapear as constraintsviolations
public class FieldError {
    //Campo que deu erro
    private String field;
    //mensagem do campo que deu erro
    private String message;

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
