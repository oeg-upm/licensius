package upm;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import upm.w3cModelInfo.ActionInfo;
import upm.w3cModelInfo.Expression;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class AppController {

    List<ActionInfo> actions;
    List<ActionInfo> leftOperands;
    List<ActionInfo> operators;


    @RequestMapping(value ="/", method = RequestMethod.GET)
    public ModelAndView index() throws IOException, URISyntaxException {

        if(actions==null) {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();

            actions=new ArrayList<ActionInfo>();
            leftOperands=new ArrayList<ActionInfo>();
            operators=new ArrayList<ActionInfo>();
            try {
                List<Expression> vocabulary = new ObjectMapper().readValue(new URL("https://www.w3.org/ns/odrl/2/ODRL22.json"), typeFactory.constructCollectionType(List.class, Expression.class));

                for (Expression e:
                     vocabulary) {
                    if(e.getType().contains("http://www.w3.org/ns/odrl/2/Action")
                            && (e.getHttpWwwW3Org200207OwlDeprecated()!=null
                            && e.getHttpWwwW3Org200207OwlDeprecated().size()>0
                            && !e.getHttpWwwW3Org200207OwlDeprecated().get(0).getValue() || e.getHttpWwwW3Org200207OwlDeprecated()==null)
                            && e.getHttpWwwW3Org200001RdfSchemaLabel()!=null
                            && e.getHttpWwwW3Org200001RdfSchemaLabel().size()>0
                            && e.getHttpWwwW3Org200402SkosCoreDefinition()!=null
                            && e.getHttpWwwW3Org200402SkosCoreDefinition().size()>0){
                        actions.add(new ActionInfo(e.getHttpWwwW3Org200001RdfSchemaLabel().get(0).getValue(),e.getId(),e.getHttpWwwW3Org200402SkosCoreDefinition().get(0).getValue()));
                    }
                    else if(e.getType().contains("http://www.w3.org/ns/odrl/2/LeftOperand")
                            && (e.getHttpWwwW3Org200207OwlDeprecated()!=null
                            && e.getHttpWwwW3Org200207OwlDeprecated().size()>0
                            && !e.getHttpWwwW3Org200207OwlDeprecated().get(0).getValue() || e.getHttpWwwW3Org200207OwlDeprecated()==null)
                            && e.getHttpWwwW3Org200001RdfSchemaLabel()!=null
                            && e.getHttpWwwW3Org200001RdfSchemaLabel().size()>0
                            && e.getHttpWwwW3Org200402SkosCoreDefinition()!=null
                            && e.getHttpWwwW3Org200402SkosCoreDefinition().size()>0){
                        leftOperands.add(new ActionInfo(e.getHttpWwwW3Org200001RdfSchemaLabel().get(0).getValue(),e.getId(),e.getHttpWwwW3Org200402SkosCoreDefinition().get(0).getValue()));

                    }
                    else if(e.getType().contains("http://www.w3.org/ns/odrl/2/Operator")
                            && (e.getHttpWwwW3Org200207OwlDeprecated()!=null
                            && e.getHttpWwwW3Org200207OwlDeprecated().size()>0
                            && !e.getHttpWwwW3Org200207OwlDeprecated().get(0).getValue() || e.getHttpWwwW3Org200207OwlDeprecated()==null)
                            && e.getHttpWwwW3Org200001RdfSchemaLabel()!=null
                            && e.getHttpWwwW3Org200001RdfSchemaLabel().size()>0
                            && e.getHttpWwwW3Org200402SkosCoreDefinition()!=null
                            && e.getHttpWwwW3Org200402SkosCoreDefinition().size()>0){
                        operators.add(new ActionInfo(e.getHttpWwwW3Org200001RdfSchemaLabel().get(0).getValue(),e.getId(),e.getHttpWwwW3Org200402SkosCoreDefinition().get(0).getValue()));

                    }
                }

                Collections.sort(actions, new ActionInfoComparer());
                Collections.sort(leftOperands, new ActionInfoComparer());
                Collections.sort(operators, new ActionInfoComparer());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ModelAndView("login").addObject("actions",actions)
                                                .addObject("leftOperands",leftOperands)
                                                .addObject("operators",operators);
    }



}

class ActionInfoComparer implements Comparator<ActionInfo> {
    @Override
    public int compare(ActionInfo x, ActionInfo y) {
        int startComparison = x.name.compareTo(y.name);
        return startComparison;
    }

}



