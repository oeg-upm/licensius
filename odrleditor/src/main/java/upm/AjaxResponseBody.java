package upm;

import com.fasterxml.jackson.annotation.JsonView;

public class AjaxResponseBody {

    @JsonView(Views.Public.class)
    boolean valid;

    @JsonView(Views.Public.class)
    String txt;

    @JsonView(Views.Public.class)
    String msg;
}
