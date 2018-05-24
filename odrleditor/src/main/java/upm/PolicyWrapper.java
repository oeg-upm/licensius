package upm;

import com.fasterxml.jackson.annotation.JsonInclude;
import upm.model.Constraint;
import upm.model.License;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyWrapper {
    private License license;
    private List<Constraint> constraints;
    private String format;

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
