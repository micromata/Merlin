package de.reinhard.merlin.app.storage;

import de.reinhard.merlin.word.templating.DependentVariableDefinition;
import de.reinhard.merlin.word.templating.TemplateDefinition;
import de.reinhard.merlin.word.templating.VariableDefinition;
import de.reinhard.merlin.word.templating.VariableType;

import java.util.List;

/**
 * Creates test data.
 */
public class TestData {
    public static void create() {
        List<TemplateDefinition> templates = Storage.getInstance().getTemplatesList();
        TemplateDefinition template = new TemplateDefinition();
        template.setName("Employment contract").setFilenamePattern("employment-contract-${Employee}").setDescription("This template is used for the generation of emloyee contracts.");
        templates.add(template);
        VariableDefinition gender = createStringVariable("Gender", "Gender of the employee.", true, true).addAllowedValues("male", "female");
        template.add(createStringVariable("Employee", "Name of the employee.", true, true));
        template.add(new VariableDefinition(VariableType.DATE, "BeginDate").setDescription("Begin of the contract.").setRequired());
        template.add(gender);
        template.add(new VariableDefinition(VariableType.INT, "WeeklyHours").setDescription("The weekly working hours.").setRequired().setMinimumValue(1).setMaximumValue(40));
        template.add(new DependentVariableDefinition().setName("Mr_Mrs").setDependsOn(gender).addMapping("male", "Mr.").addMapping("female", "Mrs."));
        template.add(new DependentVariableDefinition().setName("He_She").setDependsOn(gender).addMapping("male", "He").addMapping("female", "She"));
        template.add(new DependentVariableDefinition().setName("he_she").setDependsOn(gender).addMapping("male", "he").addMapping("female", "she"));
        template.add(new DependentVariableDefinition().setName("His_Her").setDependsOn(gender).addMapping("male", "His").addMapping("female", "Her"));
        template.add(new DependentVariableDefinition().setName("his_her").setDependsOn(gender).addMapping("male", "his").addMapping("female", "her"));

        template = new TemplateDefinition();
        template.setName("Service agreement").setFilenamePattern("service-agreement-${contractNumber}").setDescription("This template is used for the generation of service contracts (such as SLA).");
        templates.add(template);
        template.add(createStringVariable("Customer", "Our customer.", true, true));
        template.add(new VariableDefinition(VariableType.DATE, "BeginDate").setDescription("Begin of the contract.").setRequired());
        template.add(new VariableDefinition(VariableType.INT, "contractNumber").setDescription("The unique contract number.").setRequired().setMinimumValue(1));
    }

    private static VariableDefinition createStringVariable(String name, String description, boolean required, boolean unique) {
        return new VariableDefinition(name)
                .setDescription(description)
                .setRequired(required).setUnique(unique);
    }
}
