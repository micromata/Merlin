# Merlin Core

## Functionality
Merlin core contains the base library supporting:
* Reading Excel files with validation of the data (xls and xlsx).
* Creating new and manipulating existing Excel files (xls and xlsx).
* Importing Excel data in a convenient way for the user by comparing
  uploaded Excel data with already imported data (not yet implemented).
  Refer the merlin-app for a demo use-case (not yet implemented).
* Manipulating of existing Word documents (docx): replace regions.
* Word templates (docx) and the variable and template definition. Variable
  substitution is as well supported as conditionals (equals, greater-less etc.)
  for having dynamic content in the result word file. Support of serial letters.

## Excel validation
tbd. Refer e. g. de.reinhard.merlin.word.templating.TemplateDefinitionExcelReader as a full example of
reading and validating an excel file.
Refer e. g.  de.reinhard.merlin.excel.ExcelWorkbookTest for returning Excel files with marked validation
errors.

## Templating
### Glossar
* Templates (or template files) are Word files containing variables and conditionals such as
  > This is an example word content for __${user}__.
  > __{if fox = "lazy"}__ The __${color}__ fox is lazy.__{endif}__

  See [ContractTemplate.docx](https://github.com/kreinhard/merlin/raw/master/examples/ContractTemplate.docx)
  in the [examples](https://github.com/kreinhard/merlin/tree/master/examples) directory.
* Template definitions (optional) describes variables and also so called dependent variables.
  An template can be assigned to a template definition for more functionality.

  See [ContractDefinition.xlsx](https://github.com/kreinhard/merlin/raw/master/examples/ContractDefinition.xlsx)
  in the [examples](https://github.com/kreinhard/merlin/tree/master/examples) directory.
* Variables are customizable by the end-user running a template process. All variables in
  the template file will be replaced by the customized values and all conditionals will be executed
  by using the variables.
* Dependent variables are defined inside template definitions. These variables are dependent from
  variables, e. g. a salutation in a letter is dependent on the gender of the receiver. For female
  receivers 'Dear Miss' and for male receivers 'Dear Mister' should be used. Dependent variables
  will be set automatically dependent on the assigned variable and has to be defined once on template
  definition creation.
* Serial letters: You may run templates multiple times for different variables (such as letters for
  many receivers.)
  
  See [Contract-Serial.xlsx](https://github.com/kreinhard/merlin/raw/master/examples/Contract-Serial.xlsx)
  in the [examples](https://github.com/kreinhard/merlin/tree/master/examples) directory.


### Running a template
* You may run a template with the following settings:
  * Customized variables (gender, name of receiver, etc.)
  * A template file. This template file may contain a reference to an existing template definition:
  
    > ${template.id = "JZpnpojeSuN5JDqtm9KZ"} (template definition referenced by id)
    > ${template.name = "ContractTemplate"} (template definition referenced by name)
    
    Template definitions are automatically assigned if they have the same file name (without file extension) as the template
     file, e. g. a template named ContractTemplate.docx matches a definition file named ContractTemplate.xlsx.
  * Optional a template definition file containing dependent variables.
* You may run a template with the following settings:
  * Serial definition file containg variables. The template and template definition file may be specified
    inside this definition file (Excel).
  * A template file to run (required if not defined in the serial definition file).
  * A template definition file for usage of dependent variables (may already be defined in the serial
    definition file).


