 var x = 0;

 var rules = 0;

 $(document).ready(function() {
     onPageLoad();
 });

 function newAdd(e) {
     console.log(e);
     var nombre = "untexto[" + x + "].text";
     var nombreb = "b" + x;
     e.parent().append('<div><input type="text" name=' + nombre + ' /><a href="#" class="delete">Delete</a><button  name=' + nombreb + ' class="add_form_field">Add New Field &nbsp; <span style="font-size:16px; font-weight:bold;">+ </span></button></div>'); //add input box

     add_button = $(".add_form_field");
     console.log(x);
     console.log("uno");
     $("[name='" + nombreb + "']").click(function(e) {

         newAdd($(this));
     });
     console.log($("[name]"));
     x++;

 }

 function newProfile(e) {
     var nombre = "license.profile";

     // create profile field
     e.parent('div').parent('div').children('.fields').append($("#profileTemplate").html()); //add input box

     console.log("hola");
     console.log($("[name='license.profile']"));
     x++;

     // Add delete function for Profile
     $("[name='license.profile']").parent('div').on("click", ".delete", function(e) {
         console.log("si");
         console.log($(this));
         e.preventDefault();
         $(this).parent('div').parent('div').parent('div').children('.buttons').append($("#addProfileButtonTemplate").html());

         // Add "add profile" button function again
         $(".add_profile").click(function(e) {
             newProfile($(this));
             $(this).remove();
         });

         $(this).parent('div').remove();
         x--;

     })

 }

 function newInheritance(e) {
     var nombre = "license.inheritFrom";

     // create profile field
     e.parent('div').parent('div').children('.fields').append($("#inheritanceTemplate").html()); //add input box

     x++;

     // Add delete function for Profile
     $("[name='license.inheritFrom']").parent('div').on("click", ".delete", function(e) {
         console.log("si");
         console.log($(this));
         e.preventDefault();
         $(this).parent('div').parent('div').parent('div').children('.buttons').append($("#addInheritanceButtonTemplate").html());

         // Add "add profile" button function again
         $(".add_inheritance").click(function(e) {
             newInheritance($(this));
             $(this).remove();
         });

         $(this).parent('div').remove();
         x--;

     })

 }

 function newConflict(e) {
     console.log(e);
     var nombre = "license.conflict";

     // create profile field
     var template = $("#conflictTemplate").html();
     e.parent('div').parent('div').children('.fields').append(template.replace("{{name}}", nombre));

     console.log(x);
     console.log($("[name='" + nombre + "']"));
     x++;

     // Add delete function
     $("[name='" + nombre + "']").parent('div').on("click", ".delete", function(e) {
         console.log("si");
         console.log($(this));
         e.preventDefault();
         var templateButton = $("#addConflictButtonTemplate").html();
         $(this).parent('div').parent('div').parent('div').children('.buttons').append(templateButton);

         // Add "add" button function again
         $(".add_conflict").click(function(e) {
             newConflict($(this));
             $(this).remove();
         });

         $(this).parent('div').remove();
         x--;

     })

 }

 function newRule(e) {
     console.log(e);
     $("#errorMessage").text("");
     var nombre = "license.rules[" + rules + "]";
     //
     // create profile field
     var template = $("#ruleTemplate").html();
     $("#rule_wrapper").append(template.replace("{{typeName}}", nombre + ".type")
         .replace(/{{name}}/g, nombre)
         .replace("{{deleteName}}", nombre + "_delete")
         .replace("{{number}}", rules)
         //   .replace("{{assignerName}}",nombre+".assigners[0].uid")
         .replace("{{actionName}}", nombre + ".actions[0].uid")); //Add index as a html tag?

     console.log(x);
     console.log($("[name='" + nombre + "_delete']"));
     x++;

     addTarget(nombre);

     var deleteFunction=function(e) {
                                 console.log("si");

                                 e.preventDefault();

                                 if (rules > 1) {
                                     var number = parseInt($(this).parent('div').parent('div').attr('number'));
                                     console.log("[name^='license.rules[" + (number + 1) + "]']");

                                     //change all other rules number
                                     for (var i = number + 1; i < rules; i++) {
                                         var newNumber = i - 1;
                                         $("[name='license.rules[" + i + "]']").attr("number", newNumber);
                                         $("[name^='license.rules[" + i + "]']").each(function() {
                                             $(this).attr("name", $(this).attr("name").replace("license.rules[" + i + "]", "license.rules[" + newNumber + "]"))
                                         });
                                         $("[data-target^='" + "[pc=\\'license.rules[" + i + "]']").each(function() {
                                             $(this).attr("data-target", $(this).attr("data-target").replace("license.rules[" + i + "]", "license.rules[" + newNumber + "]"))
                                         });
                                         $("[pc^='license.rules[" + i + "]']").each(function() {
                                             $(this).attr("pc", $(this).attr("pc").replace("license.rules[" + i + "]", "license.rules[" + newNumber + "]"))
                                         });
                                     }

                                     $(this).parent('div').parent('div').remove();



                                     rules--;
                                     x--;
                                 } else {
                                     $("#errorMessage").text("A policy must have at least one rule.");
                                 }
                             };

     // Add delete function
     $("[name='" + nombre + "']").on("click", ".deleteRule", deleteFunction)


     // Add assigner function
     addAssigner(nombre, false);


     // Add assignee function
     addAssigner(nombre, true);

     var refs = [];

     function prueba(ref) {
         refs.push(ref);
     }
     // Add duty
     addDuty(false, nombre, prueba);


     var constraints = 0;
     // Change type of duty when dropdown value is selected
     $("[name='" + nombre + "']").find(".type").change(function(e) {
         console.log($("[name='" + nombre + "']").find(".type"));
         console.log($("[name='" + nombre + "']").find(".type").find(":selected").text());

         var text = "Duty";
         if ($("[name='" + $(this).parent('div').parent('div').parent('div').attr("name") + "']").find(".type").find(":selected").text() == "Obligation") {
             text = "Consequence";
             $("[name^='" + $(this).parent('div').parent('div').parent('div').attr("name") + ".duties[" + "']").each(function(e) {
                 if ($("[name^='" + $(this).attr("name") + ".duties[" + "']").length > 0) {
                     $("[name^='" + $(this).attr("name") + ".duties[" + "']").parent('div').remove();
                 }
                 if ($("[name^='" + $(this).attr("name") + "']").children(".buttons").children(".add_consequence").length > 0) {
                     $("[name^='" + $(this).attr("name") + "']").children(".buttons").children(".add_consequence").disable(true);
                 }
             });
             for (var i = 0; i < refs.length; i++) {
                 if (refs[i] != null) {
                     refs[i].val = 0;
                 }
             }
         } else if ($("[name='" + $(this).parent('div').parent('div').parent('div').attr("name") + "']").find(".type").find(":selected").text() == "Prohibition") {
             text = "Remedy";
             $("[name^='" + $(this).parent('div').parent('div').parent('div').attr("name") + ".duties[" + "']").each(function(e) {
                 if ($("[name^='" + $(this).attr("name") + ".duties[" + "']").length > 0) {
                     $("[name^='" + $(this).attr("name") + ".duties[" + "']").parent('div').remove();
                 }
                 if ($("[name^='" + $(this).attr("name") + "']").children(".buttons").children(".add_consequence").length > 0) {
                     $("[name^='" + $(this).attr("name") + "']").children(".buttons").children(".add_consequence").disable(true);
                 }

             });
             for (var i = 0; i < refs.length; i++) {
                 if (refs[i] != null) {
                     refs[i].val = 0;
                 }
             }
         } else {
             $("[name^='" + $(this).parent('div').parent('div').parent('div').attr("name") + ".duties[" + "']").each(function(e) {
                 if ($("[name^='" + $(this).attr("name") + "']").children(".buttons").children(".add_consequence").length > 0) {
                     $("[name^='" + $(this).attr("name") + "']").children(".buttons").children(".add_consequence").disable(false);
                 }
             });

         }

         $("[name='" + $(this).parent('div').parent('div').parent('div').attr("name") + "']").find(".duty").text(text);

     })

     // change description of action when is changed
     $("[name='" + nombre + ".actions[0].uid" + "']").change(function(e) {


         $("[name='" + nombre + ".actions[0].uid" + "']").siblings('span').text($("[name='" + nombre + ".actions[0].uid" + "']").find(":selected").attr("description"));

     })

     //add constraints
     addConstraint(nombre, null, "Constraint");

     rules++;


 }

 function addAssigner(nombre, assignee) {
     var assigners = 0;
     $("[name='" + nombre + "']").on("click", assignee ? ".add_assignee" : ".add_assigner", function(e) {
         console.log("si");
         console.log($(this));
         e.preventDefault();

         var templateAssigner = $("#addAssignerTemplate").html();
         var assignerName = $(this).parent('div').parent('div').attr("name") + (assignee ? ".assignees[" : ".assigners[") + assigners + "]";

         $(this).parent('div').parent('div').children(".fields").append(templateAssigner.replace("{{assignerName}}", assignerName + ".uid")
             .replace("{{deleteClass}}", "_delete")
             .replace("{{number}}", assigners)
             .replace(/{{name}}/g, assignerName)
             .replace("{{isCollectionName}}", assignerName + ".type")
             .replace("{{label}}", assignee ? "Assignee" : "Assigner"));

         console.log($("[name='" + assignerName + "']"));
         //delete function for assigner
         $("[name='" + assignerName + "']").children('.panel-heading').on("click", "._delete", function(e) {

             //change siblings number
             var number = parseInt($(this).parent('div').parent('div').attr('number'));

             for (var i = number + 1; i < assigners; i++) {
                 var newNumber = i - 1;
                 var prefix = $(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr("name") + (assignee ? ".assignees[" : ".assigners[") + i + "]";
                 var newprefix = $(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr("name") + (assignee ? ".assignees[" : ".assigners[") + newNumber + "]";

                 $("[name='" + prefix + "']").attr("number", newNumber);
                 $("[name^='" + prefix + "']").each(function() {
                     $(this).attr("name", $(this).attr("name").replace(prefix, newprefix))
                 });
                 $("[data-target^='" + "[pc=\\'" + prefix + "']").each(function() {
                     $(this).attr("data-target", $(this).attr("data-target").replace(prefix, newprefix))
                 });
                 $("[pc^='" + prefix + "']").each(function() {
                     $(this).attr("pc", $(this).attr("pc").replace(prefix, newprefix))
                 });
             }

             $(this).parent('div').parent('div').parent('div').remove();
             assigners = assigners - 1;
         })

         // add contraint function
         var refs = [];

         function prueba(ref) {
             refs.push(ref);
         }
         addConstraint(assignerName, prueba, "Refinement");

         // function when the checkbox change
         $("[name='" + assignerName + ".type" + "']").change(function(e) {

             if ($("[name='" + assignerName + ".type" + "']").is(":checked")) {
                 $("[name='" + assignerName + "']").children(".buttons").children(".add_constraint").disable(false);
                 $("[name='" + assignerName + "']").children(".buttons").children(".add_logicalConstraint").disable(false);
             } else {
                 $("[name='" + assignerName + "']").children(".buttons").children(".add_constraint").disable(true);
                 $("[name='" + assignerName + "']").children(".buttons").children(".add_logicalConstraint").disable(true);
                 $("[name^='" + assignerName + ".constraints[" + "']").parent('div').remove();
                 for (var i = 0; i < refs.length; i++) {
                     if (refs[i] != null) {
                         refs[i].val = 0;
                     }
                 }
             }
         })



         assigners++;
         x--;

     })


 }

 function addRemoveableTarget(nombre) {
       var targets = 0;
       $("[name='" + nombre + "']").on("click",  ".add_target" , function(e) {



           var templateAssigner = $("#removeableTargetTemplate").html();
           var targetName = $(this).parent('div').parent('div').attr("name") + ".targets[" + targets + "]";

           $(this).parent('div').parent('div').children(".fields").append(templateAssigner.replace("{{targetName}}", targetName + ".uid")
               .replace("{{number}}", targets)
               .replace(/{{name}}/g, targetName)
               .replace("{{isCollectionName}}", targetName + ".type")
               .replace("{{deleteName}}","_delete"));


           // add contraint function
           var refs = [];

           function prueba(ref) {
               refs.push(ref);
           }
           addConstraint(targetName, prueba, "Refinement");

           // function when the checkbox change
           $("[name='" + targetName + ".type" + "']").change(function(e) {

               if ($("[name='" + targetName + ".type" + "']").is(":checked")) {
                   $("[name='" + targetName + "']").children(".buttons").children(".add_constraint").disable(false);
                   $("[name='" + targetName + "']").children(".buttons").children(".add_logicalConstraint").disable(false);
               } else {
                   $("[name='" + targetName + "']").children(".buttons").children(".add_constraint").disable(true);
                   $("[name='" + targetName + "']").children(".buttons").children(".add_logicalConstraint").disable(true);
                   $("[name^='" + targetName + ".constraints[" + "']").parent('div').remove();
                   for (var i = 0; i < refs.length; i++) {
                       if (refs[i] != null) {
                           refs[i].val = 0;
                       }
                   }
               }
           })

         //delete function for assigner
         $("[name='" + targetName + "']").children('.panel-heading').on("click", "._delete", function(e) {
             $(this).parent('div').parent('div').parent('div').parent('div').parent('div').children('.buttons').append($("#addTargetButtonTemplate").html());


             $(this).parent('div').parent('div').parent('div').remove();
             targets = targets - 1;
         })

            $(this).remove();

           targets++;
           x--;

       })


   }

 function addTarget(nombre) {
      var targets = 0;
      //$("[name='" + nombre + "']").on("click", assignee ? ".add_assignee" : ".add_assigner", function(e) {

          var templateAssigner = $("#targetTemplate").html();
          var targetName = $("[name='" + nombre + "']").attr("name") + ".targets[" + targets + "]";

          $("[name='" + nombre + "']").children(".fields").append(templateAssigner.replace("{{targetName}}", targetName + ".uid")
              .replace("{{number}}", targets)
              .replace(/{{name}}/g, targetName)
              .replace("{{isCollectionName}}", targetName + ".type"));


          // add contraint function
          var refs = [];

          function prueba(ref) {
              refs.push(ref);
          }
          addConstraint(targetName, prueba, "Refinement");

          // function when the checkbox change
          $("[name='" + targetName + ".type" + "']").change(function(e) {

              if ($("[name='" + targetName + ".type" + "']").is(":checked")) {
                  $("[name='" + targetName + "']").children(".buttons").children(".add_constraint").disable(false);
                  $("[name='" + targetName + "']").children(".buttons").children(".add_logicalConstraint").disable(false);
              } else {
                  $("[name='" + targetName + "']").children(".buttons").children(".add_constraint").disable(true);
                  $("[name='" + targetName + "']").children(".buttons").children(".add_logicalConstraint").disable(true);
                  $("[name^='" + targetName + ".constraints[" + "']").parent('div').remove();
                  for (var i = 0; i < refs.length; i++) {
                      if (refs[i] != null) {
                          refs[i].val = 0;
                      }
                  }
              }
          })



          targets++;
          x--;

    //  })


  }

 function addConstraint(nombre, prueba, label,external) {


     // add constraint function
     var constraints = {
         val: 0
     };
     if (prueba != null) {
         prueba(constraints);
     }
     (external!=null?$(".add_constraint").parent('div'):$("[name='" + nombre + "']").children(".buttons")).on("click", ".add_constraint", function(e) {


         e.preventDefault();

         var templateConstraint = $("#addConstraintTemplate").html();
         var constraintName = (external!=null?"constraints[" + constraints.val + "]":$(this).parent('div').parent('div').attr("name") + ".constraints[" + constraints.val + "]");

         (external!=null?$("#constraints_wrapper"):$(this).parent('div').parent('div').children(".fields")).append(templateConstraint.replace(/{{name}}/g, constraintName)
             .replace("{{number}}", constraints.val)
             .replace("{{deleteName}}", "_delete")
             .replace("{{leftOperandName}}", constraintName + ".leftOperand")
             .replace("{{operatorName}}", constraintName + ".operator")
             .replace("{{rightOperandName}}", constraintName + ".rightOperand")
             .replace("{{label}}", label)
             .replace("{{isReferenceName}}",constraintName + ".isReference"));

         // change description of left operand when is changed
         $("[name='" + constraintName + ".leftOperand" + "']").change(function(e) {

             $(this).siblings('span').text($(this).find(":selected").attr("description"));

         })

         // change description of operator when is changed
         $("[name='" + constraintName + ".operator" + "']").change(function(e) {

             $(this).siblings('span').text($(this).find(":selected").attr("description"));

         })

         //delete function for constraint
         $("[name='" + constraintName + "']").children('.panel-heading').on("click", ".deleteConstraint", function(e) {

             //change siblings number
             var number = parseInt($(this).parent('div').parent('div').attr('number'));
             console.log("number: "+number);
             for (var i = number + 1; i < constraints.val; i++) {
                 var newNumber = i - 1;
                 var prefix = (external!=null?"constraints["+i+"]":$(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr("name") + ".constraints[" + i + "]");
                 var newprefix = (external!=null?"constraints["+newNumber+"]":$(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr("name") + ".constraints[" + newNumber + "]");
                 console.log( $("[name='" + prefix + "']"));
                 $("[name='" + prefix + "']").attr("number", newNumber);
                 $("[name^='" + prefix + "']").each(function() {
                     $(this).attr("name", $(this).attr("name").replace(prefix, newprefix))
                 });
                 $("[data-target^='" + "[pc=\\'" + prefix + "']").each(function() {
                     $(this).attr("data-target", $(this).attr("data-target").replace("[pc='" + prefix, "[pc='" + newprefix))
                 });
                 $("[pc^='" + prefix + "']").each(function() {
                     $(this).attr("pc", $(this).attr("pc").replace(prefix, newprefix))
                 });
             }

             $(this).parent('div').parent('div').parent('div').remove();
             constraints.val = constraints.val - 1;
         })

         //add unit function
         $("[name='" + constraintName + "']").children('.buttons').on("click", ".add_unit", function(e) {
             var templateUnit = $("#addUnitTemplate").html();
             var unitName = $(this).parent('div').parent('div').attr("name") + ".unit";
             $(this).parent('div').parent('div').children(".fields").append(templateUnit.replace("{{name}}", unitName)
                 .replace("{{deleteClass}}", "_delete"));
             $(this).remove();

             //delete function for unit
             $("[name='" + unitName + "']").parent('div').on("click", "._delete", function(e) {
                 //add "add unit" button again
                 $(this).parent('div').parent('div').parent('div').children(".buttons").append($("#addUnitButtonTemplate").html()

                 );
                 $(this).parent('div').remove();

             })


         })

         //add data type function
         $("[name='" + constraintName + "']").children('.buttons').on("click", ".add_dataType", function(e) {
             var templateUnit = $("#addDataTypeTemplate").html();
             var unitName = $(this).parent('div').parent('div').attr("name") + ".dataType";
             $(this).parent('div').parent('div').children(".fields").append(templateUnit.replace("{{name}}", unitName)
                 .replace("{{deleteClass}}", "_delete"));
             $(this).remove();

             //delete function for dataType
             $("[name='" + unitName + "']").parent('div').on("click", "._delete", function(e) {
                 //add "add dataType" button again
                 $(this).parent('div').parent('div').parent('div').children(".buttons").append($("#addDataTypeButtonTemplate").html()

                 );
                 $(this).parent('div').remove();

             })


         })

         //add status function
         $("[name='" + constraintName + "']").children('.buttons').on("click", ".add_status", function(e) {
             var templateUnit = $("#addStatusTemplate").html();
             var unitName = $(this).parent('div').parent('div').attr("name") + ".status";
             $(this).parent('div').parent('div').children(".fields").append(templateUnit.replace("{{name}}", unitName)
                 .replace("{{deleteClass}}", "_delete"));
             $(this).remove();

             //delete function for status
             $("[name='" + unitName + "']").parent('div').on("click", "._delete", function(e) {
                 //add "add status" button again
                 $(this).parent('div').parent('div').parent('div').children(".buttons").append($("#addStatusButtonTemplate").html()

                 );
                 $(this).parent('div').remove();

             })


         })

         if(external!=null){
             var templateUid = $("#addUidTemplate").html();
             $("[name='" + constraintName + "']").children('.fields').append(templateUid.replace("{{name}}",constraintName+".uid"));
         }

         constraints.val = constraints.val + 1;

     });

    (external!=null?$(".add_logicalConstraint").parent('div'):$("[name='" + nombre + "']").children(".buttons")).on("click", ".add_logicalConstraint", function(e) {


         e.preventDefault();

         var templateConstraint = $("#addLogicalConstraintTemplate").html();
         var constraintName = (external!=null?"constraints[" + constraints.val + "]":$(this).parent('div').parent('div').attr("name") + ".constraints[" + constraints.val + "]");

         (external!=null?$("#constraints_wrapper"):$(this).parent('div').parent('div').children(".fields")).append(templateConstraint.replace(/{{name}}/g, constraintName)
             .replace("{{number}}", constraints.val)
             .replace("{{deleteName}}", "_delete")
             .replace("{{operatorName}}", constraintName + ".operator")
             .replace("{{operandName1}}", constraintName + ".operands[0]")
             .replace("{{operandName2}}", constraintName + ".operands[1]")
             .replace("{{label}}", label));



         //delete function for constraint
         $("[name='" + constraintName + "']").children('.panel-heading').on("click", ".deleteConstraint", function(e) {

             //change siblings number
             var number = parseInt($(this).parent('div').parent('div').attr('number'));
             console.log("number: "+number);
             for (var i = number + 1; i < constraints.val; i++) {
                 var newNumber = i - 1;
                 var prefix = (external!=null?"constraints["+i+"]":$(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr("name") + ".constraints[" + i + "]");
                 var newprefix = (external!=null?"constraints["+newNumber+"]":$(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr("name") + ".constraints[" + newNumber + "]");
                 console.log( $("[name='" + prefix + "']"));
                 $("[name='" + prefix + "']").attr("number", newNumber);
                 $("[name^='" + prefix + "']").each(function() {
                     $(this).attr("name", $(this).attr("name").replace(prefix, newprefix))
                 });
                 $("[data-target^='" + "[pc=\\'" + prefix + "']").each(function() {
                     $(this).attr("data-target", $(this).attr("data-target").replace("[pc='" + prefix, "[pc='" + newprefix))
                 });
                 $("[pc^='" + prefix + "']").each(function() {
                     $(this).attr("pc", $(this).attr("pc").replace(prefix, newprefix))
                 });
             }

             $(this).parent('div').parent('div').parent('div').remove();
             constraints.val = constraints.val - 1;
         });


            var operands=2;

         $("[name='" + constraintName + "']").children('.buttons').on("click", ".add_operand", function(e) {
            var operandTemplate= $("#addOperandTemplate").html();

             var unitName = $(this).parent('div').parent('div').attr("name") + ".operands["+operands+"]";
             $(this).parent('div').parent('div').children(".fields").append(operandTemplate.replace("{{name}}", unitName)
                 .replace("{{deleteClass}}", "_delete")
                 .replace("{{number}}",operands));

                //delete function for status
              $("[name='" + unitName + "']").parent('div').on("click", "._delete", function(e) {
                      //change siblings number
                      var number = parseInt($(this).parent('div').children('input').attr('number'));
                      console.log("number: "+number);
                      for (var i = number + 1; i < operands; i++) {
                          var newNumber = i - 1;
                          var prefix = $(this).parent('div').parent('div').parent('div').attr("name") + ".operands[" + i + "]";
                          var newprefix = $(this).parent('div').parent('div').parent('div').attr("name") + ".operands[" + newNumber + "]";
                          console.log( $("[name='" + prefix + "']"));
                          $("[name='" + prefix + "']").attr("number", newNumber);
                          $("[name^='" + prefix + "']").each(function() {
                              $(this).attr("name", $(this).attr("name").replace(prefix, newprefix))
                          });
                      }
                      $(this).parent('div').remove();
                      operands--;
              });

              operands++;
         });

         if(external!=null){
             var templateUid = $("#addUidTemplate").html();
             $("[name='" + constraintName + "']").children('.fields').append(templateUid.replace("{{name}}",constraintName+".uid"));
         }

         constraints.val = constraints.val + 1;

     });

 }

 function addDuty(consequence, nombre, callbackReset) {

     var duties = {
         val: 0
     };
     if (consequence && callbackReset != null) {
         callbackReset(duties);
     }
     var button;
     if (!consequence) {
         button = ".add_duty";
     } else {
         button = ".add_consequence"
     }
     $("[name='" + nombre + "']").children(".buttons").on("click", button, function(e) {
         console.log("si");
         console.log($(this));
         e.preventDefault();

         var templateDuty;
         if (!consequence) {
             templateDuty = $("#addDutyTemplate").html();
         } else {
             templateDuty = $("#addConsequenceTemplate").html();
         }




         $(this).parent('div').parent('div').children(".fields").append(templateDuty.replace(/{{name}}/g, $(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "]")
             .replace("{{actionName}}", $(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "].actions[0].uid")
             .replace("{{deleteName}}", "_delete")
             .replace("{{number}}", duties.val));

         if (!consequence) {

             var text = "Duty";
             if ($("[name='" + $(this).parent('div').parent('div').attr("name") + "']").find(".type").find(":selected").text() == "Obligation") {
                 text = "Consequence";
                 $("[name='" + $(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "]" + "']").children(".buttons").children(".add_consequence").disable(true);
             } else if ($("[name='" + $(this).parent('div').parent('div').attr("name") + "']").find(".type").find(":selected").text() == "Prohibition") {
                 text = "Remedy";
                 $("[name='" + $(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "]" + "']").children(".buttons").children(".add_consequence").disable(true);
             }

             $("[name='" + $(this).parent('div').parent('div').attr("name") + "']").find(".duty").text(text);
         }
         //delete function
         $("[name='" + $(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "]" + "']").on("click", ".deleteDuty", function(e) {
             // change siblings number
             var number = parseInt($(this).parent('div').parent('div').attr('number'));
             for (var i = number + 1; i < duties.val; i++) {
                 var newNumber = i - 1;
                 var prefix = $(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr('name') + ".duties[" + i + "]";
                 var newprefix = $(this).parent('div').parent('div').parent('div').parent('div').parent('div').attr('name') + ".duties[" + newNumber + "]";

                 $("[name='" + prefix + "']").attr("number", newNumber);
                 $("[name^='" + prefix + "']").each(function() {
                     $(this).attr("name", $(this).attr("name").replace(prefix, newprefix))
                 });
                 $("[data-target^='" + "[pc=\\'" + prefix + "']").each(function() {
                     $(this).attr("data-target", $(this).attr("data-target").replace("[pc='" + prefix, "[pc='" + newprefix))
                 });
                 $("[pc^='" + prefix + "']").each(function() {
                     $(this).attr("pc", $(this).attr("pc").replace(prefix, newprefix))
                 });
             }

             $(this).parent('div').parent('div').parent('div').remove();
             duties.val = duties.val - 1;
         })


         // change description of action when is changed
         $("[name='" + $(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "].actions[0].uid" + "']").change(function(e) {

             $(this).siblings('span').text($(this).find(":selected").attr("description"));

         })


         //add consequence
         addDuty(true, $(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "]", callbackReset);

         //add constraints
         addConstraint($(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "]", null, "Constraint");

        addRemoveableTarget($(this).parent('div').parent('div').attr("name") + ".duties[" + duties.val + "]") ;

         duties.val = duties.val + 1;
     })



 }


 function onPageLoad() {
     var max_fields = 10;
     var wrapper = $("#wrapper");
     var add_button = $(".add_profile");


     console.log("prueba");

     $(".prueber").click(function(e) {
         console.log("yepa");
     });

     $(".add_profile").click(function(e) {
         console.log("hi");
         newProfile($(this));
         $(this).remove();
     });

      $(".add_inheritance").click(function(e) {
          newInheritance($(this));
          $(this).remove();
      });

     $(".add_conflict").click(function(e) {
         newConflict($(this));
         $(this).remove();
     });

     $(".add_rule").click(function(e) {
         newRule($(this));
     });


        addConstraint("",null,"Constraint",true)



     $("[name='prueba']").change(function(e) {

         console.log($("[name='prueba'").find(":selected").attr("description"))
     })

     $("#exampleSelector").change(function(e) {

         console.log($("#exampleSelector").find(":selected").attr("value"));

          if($("#exampleSelector").find(":selected").attr("value")!="0"){
              //change all other rules number
              for (var i = 0; i < rules; i++) {
                  $("[name='license.rules[" + i + "]']").remove();
              }



              rules=0;
              x--;
              newRule($(this));


               if($("#exampleSelector").find(":selected").attr("value")=="1"){
                    $("[name='license.uid']").val("http://odrleditor.appspot.com/samples/sample001");
                     $("[name='license.rules[0].actions[0].uid']").val("http://www.w3.org/ns/odrl/2/reproduce").trigger('change');
                     $("[name='license.rules[0].targets[0].uid']").val("http://odrleditor.appspot.com/samples/asset000");
               }
               else if($("#exampleSelector").find(":selected").attr("value")=="2"){
                    $("[name='license.uid']").val("http://odrleditor.appspot.com/samples/sample002");
                     $("[name='license.rules[0].actions[0].uid']").val("http://www.w3.org/ns/odrl/2/distribute").trigger('change');
                     $("[name='license.rules[0].targets[0].uid']").val("http://example.com/data:77");
                     if($(".add_profile").length > 0){
                        $(".add_profile").trigger('click');
                     }
                     $("[name='license.profile']").val("http://example.com/odrl:profile:09");
                      $(".add_assignee").trigger('click');
                      $("[name='license.rules[0].assignees[0].uid']").val("http://example.com/person:88");
                      $(".add_assigner").trigger('click');
                      $("[name='license.rules[0].assigners[0].uid']").val("http://example.com/org:99");
                      $(".add_duty").trigger('click');
                      $("[name='license.rules[0].duties[0].actions[0].uid']").val("http://www.w3.org/ns/odrl/2/attribute").trigger('change');
                      $(".add_target").trigger('click');
                      $("[name='license.rules[0].duties[0].targets[0].uid']").val("http://example.com/data:77");
                      $(".add_consequence").trigger('click');
                      $("[name='license.rules[0].duties[0].duties[0].actions[0].uid']").val("http://www.w3.org/ns/odrl/2/acceptTracking").trigger('change');

               }
               else if($("#exampleSelector").find(":selected").attr("value")=="3"){
                   $("[name='license.uid']").val("http://odrleditor.appspot.com/samples/sample003");
                    $("[name='license.rules[0].actions[0].uid']").val("http://www.w3.org/ns/odrl/2/display").trigger('change');
                    $("[name='license.rules[0].targets[0].uid']").val("http://example.com/data:77");
                    $("[name='license.rules[0].targets[0].type']").prop('checked', true);
                    $("[name='license.rules[0].targets[0]']").find(".add_logicalConstraint").disable(false);
                    $("[name='license.rules[0].targets[0]']").find(".add_constraint").disable(false);
                    $("[name='license.rules[0].targets[0]']").find(".add_logicalConstraint").trigger('click');
                    $("[name='license.rules[0].targets[0].constraints[0].operands[0]']").val("http://odrleditor.appspot.com/samples/cons01");
                    $("[name='license.rules[0].targets[0].constraints[0].operands[1]']").val("http://odrleditor.appspot.com/samples/cons02");
                    $("form").children("#wrapper").children(".panel").children(".buttons").children(".add_constraint").trigger('click');
                    $("form").children("#wrapper").children(".panel").children(".buttons").children(".add_constraint").trigger('click');
                    $("[name='constraints[0].leftOperand']").val("http://www.w3.org/ns/odrl/2/count").trigger('change');
                    $("[name='constraints[0].operator']").val("http://www.w3.org/ns/odrl/2/lt").trigger('change');
                    $("[name='constraints[0].rightOperand']").val("3");
                    $("[name='constraints[0].uid']").val("http://odrleditor.appspot.com/samples/cons01");
                    $("[name='constraints[1].leftOperand']").val("http://www.w3.org/ns/odrl/2/dateTime").trigger('change');
                    $("[name='constraints[1].operator']").val("http://www.w3.org/ns/odrl/2/lt").trigger('change');
                    $("[name='constraints[1].rightOperand']").val("2018-1-1");
                    $("[name='constraints[1].uid']").val("http://odrleditor.appspot.com/samples/cons02");

              }


          }

     })

     /*
             $(wrapper).on("click",".delete", function(e){
             console.log("si");
                 console.log($(this));
                 e.preventDefault(); $(this).parent('div').remove(); x--;
             })*/

     var frm = $('#form-log-button');

     frm.submit(function(e) {

         e.preventDefault();

         $.ajax({
             type: frm.attr('method'),
             url: frm.attr('action'),
             data: frm.serialize(),
             success: function(data) {
                 console.log('Submission was successful.');
                 console.log(data.msg);
                 $("#license").html("");
                 $("#license").text(data.msg);
                 $("#responseText").css('color',(data.valid?'green':'red'));
                 $("#responseText").text(data.txt);
                  if($("[name='format']").find(":selected").text() == "JSON-LD"){
                  $("#license").text("");
                    $("#license").html(syntaxHighlight(data.msg));
                  }
             },
             error: function(data) {
                 console.log('An error occurred.');
                 console.log(data);
             },
         });
     });


     $("body").on("hide.bs.collapse", function(e) {
         $(e.target).parent('div').children('.panel-heading').children('.collapseb').html('<span class="glyphicon glyphicon-expand"></span>');

     });
     $("body").on("show.bs.collapse", function(e) {
         $(e.target).parent('div').children('.panel-heading').children('.collapseb').html('<span class="glyphicon glyphicon-collapse-down"></span>');

     });


     // Disable function
     jQuery.fn.extend({
         disable: function(state) {
             return this.each(function() {
                 this.disabled = state;
             });
         }
     });

     function syntaxHighlight(json) {

         json = JSON.stringify(JSON.parse(json), null, 2);
        // return json;

    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
     }


     newRule($(this));

 }